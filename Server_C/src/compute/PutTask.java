package compute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import constant.ThreadConstant;
import database.SQLStatement;
import method.CommonMethod;
import server_c.ServerC;

/**
 * 定期扫描数据库中所有参与者都提交数据，可以开始计算的spec任务，不负责rand任务的提交
 * put进阻塞队列后，将在invitation中写入put时间字段
 */
public class PutTask implements Runnable, ThreadConstant, SQLStatement {
	protected volatile boolean end = false;
	protected volatile boolean pause = false;

	private BlockingQueue<Computation> blockQueue;
	private long blockTime;
	private ServerC c;
	private Connection dbconn;

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public PutTask(ServerC C, BlockingQueue<Computation> blockQueue) {
		// TODO Auto-generated constructor stub
		this(C, blockQueue, DEFAULT_PUT_BLOCK_TIME);
	}

	public PutTask(ServerC C, BlockingQueue<Computation> blockQueue, long blockTime) {
		// TODO Auto-generated constructor stub
		c = C;
		this.blockQueue = blockQueue;
		this.blockTime = blockTime;
		dbconn = c.getDbConnection();
	}

	// 定时检测是否有满足条件的指定计算线程，将提交后的清理放在这里
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!end) {
			while (!end && !pause) {
				try {
					Thread.sleep(blockTime);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					ResultSet readynumber = dbconn.createStatement().executeQuery(SELECT_READY_SQL);

					PreparedStatement getinviterbynumbersql = dbconn.prepareStatement(SELECT_INVITER_BY_NUMBER_SQL);
					PreparedStatement readyineesql = dbconn.prepareStatement(SELECT_READY_INVITEE_SQL);//
					PreparedStatement updatestartsql = dbconn.prepareStatement(UPDATE_PUTTIME_BY_NUMBER_SQL);// 更新puttime的语句
					while (readynumber.next()) {
						// 每循环一次就要put一个computation，内容已经计算好PK和各种必须属性
						int number = readynumber.getInt(1);

						// 取得inviter用户名
						getinviterbynumbersql.setInt(1, number);
						ResultSet inviterresult = getinviterbynumbersql.executeQuery();
						inviterresult.next();
						String inviterName = inviterresult.getString(INVITATION_COLUMN_NAME_INVITER);

						// 取得invitees用户名
						readyineesql.setInt(1, number);
						ResultSet readyinee = readyineesql.executeQuery();
						ArrayList<String> readyineelist = new ArrayList<String>();// 本次计算可参与者列表
						while (readyinee.next()) {
							readyineelist.add(readyinee.getString(1));
						}
						if (readyineelist.size() == 0) {
							continue;
						}

						// 用取得的inviter和invitees构建一次specific computation，并压入队列
						Computation specComputation = ComputeMethod.getSpecComputation(dbconn, inviterName,
								readyineelist);
						specComputation.setNumber(number);

						blockQueue.put(specComputation);

						// 给本次计算的指定计算编号打上开始标记
						updatestartsql.setString(1, CommonMethod.getTimeNow());
						updatestartsql.setInt(2, number);
						updatestartsql.executeUpdate();
					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			if (pause) {
				try {
					Thread.sleep(DEFAULT_SLEEP_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
