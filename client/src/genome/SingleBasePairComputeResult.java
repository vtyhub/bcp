package genome;

import java.io.Serializable;
import java.math.BigInteger;

public class SingleBasePairComputeResult implements Serializable {

	// 表里用四个字段来将add result和multresutl存储，一条记录正好可以对应一个singlebase
	// 两个用户之间的计算结果是包含single数组和两个用户名的computeresult，可以在数据库以多条记录实现
	// 服务器存储的是多个客户之间计算结果，computeresult数组，全都放到一个表里也不是不行
	// 加密公钥可能是大PK，也可能是各个用户自己的H
	private static final long serialVersionUID = 4176247058599187303L;

	// 分为A和B，长度均应为2
	private BigInteger[] addResult;
	private BigInteger[] multResult;

	public SingleBasePairComputeResult() {
		// TODO Auto-generated constructor stub
	}

	public SingleBasePairComputeResult(BigInteger[] add, BigInteger[] mult) {
		// TODO Auto-generated constructor stub
		addResult = add;
		multResult = mult;
	}

	public BigInteger[] getAddResult() {
		return addResult;
	}

	public void setAddResult(BigInteger[] addResult) {
		this.addResult = addResult;
	}

	public BigInteger[] getMultResult() {
		return multResult;
	}

	public void setMultResult(BigInteger[] multResult) {
		this.multResult = multResult;
	}

}
