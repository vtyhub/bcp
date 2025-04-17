package view;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

import constant.SysConstant;
import model.Log;
import server_s.AbstractServerS.LogType;
import server_s.ServerS;

import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

public class LogPanel extends JPanel implements SysConstant {

	private static final long serialVersionUID = 1L;
	// 其他实例
	private Log log;

	// 视图组件
	private JScrollPane logSP;
	private JTextArea logTA;
	private JButton btnGetcurrentlog;

	// 应该用模型存储数据，而不是视图组件
	public LogPanel(ServerS S, LogType logtype) {
//		this.log = instance.getLog(logtype);// 从此这个视图类和一个模型绑定，所有数据全都存储在服务器实例中的日志中
		if (log == null) {
			return;
		}
		setLayout(null);
		// logTestArea
		logTA = new JTextArea();
		logTA.setBounds(0, 0, 4, 24);

		// logScrollPane
		logSP = new JScrollPane();
		logSP.setBounds(10, 10, 630, 247);
		logSP.setViewportView(logTA);
		add(logSP);

		// outputButton
		JButton btnOutput = new JButton("Output");
		btnOutput.setBounds(547, 267, 93, 23);
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// System.out.println(logTA.getText().replaceAll("\n", "\r\n"));
				JFileChooser jfc = new JFileChooser(constant.SysConstant.DESKTOP_PATH);
				jfc.setAcceptAllFileFilterUsed(false);// 禁止所有文件选项
				jfc.setFileFilter(new LogFileFilter());
				// jfc.addChoosableFileFilter(jfcfilter);//不知道和setFileFilter的区别是什么
				jfc.showSaveDialog(new JLabel());// 保存选项，显示fileChooser
				File file = jfc.getSelectedFile();
				if (file == null) {
					return;
				}
				try (PrintWriter pr = new PrintWriter(file)) {
					String[] all = log.getAll();
					for (int i = 0; i < all.length; i++) {
						pr.println(all[i]);
					}
					/*
					 * String osname = System.getProperty("os.name"); if
					 * (osname.contains("Windows")) { pr.print(logTA.getText().replaceAll("\n",
					 * "\r\n"));// 在windows下文件换行是"\r\n" } else if (osname.contains("Linux") ||
					 * osname.contains("UNIX")) { pr.print(logTA.getText().replaceAll("\n",
					 * "\r"));// linux下是\r,这里就不考虑BSD等情况了，虽然实际确实会遇到 } else {
					 * pr.print(logTA.getText());// MAC OS X等其他的系统为\n }
					 */
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		add(btnOutput);

		// currentlog button
		btnGetcurrentlog = new JButton("getCurrentLog");
		btnGetcurrentlog.setBounds(403, 267, 134, 23);
		btnGetcurrentlog.addActionListener((e) -> {
			logTA.setText("");
			String[] all = log.getAll();
			for (int i = 0; i < all.length; i++) {
				logTA.append(all[i] + '\n');
			}
		});
		add(btnGetcurrentlog);

	}

	private final class LogFileFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			// TODO Auto-generated method stub
			if (f.isDirectory()) {
				return true;
			} else if (f.getName().endsWith(".txt")) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return "文本文件(*.txt)";
		}

	}

	public static void main(String[] args) {
		JFrame jf = new JFrame();
		Rectangle rec = new Rectangle(10, 10, 500, 500);

		jf.setBounds(rec);
		jf.setContentPane(new LogPanel(new ServerS(), LogType.bcpLog));
		jf.setVisible(true);
	}
}
