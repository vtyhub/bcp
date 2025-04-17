package constant;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public interface CommonClass {

	public static final class limitedTFDocument extends PlainDocument {

		private static final long serialVersionUID = 1L;
		
		private int maxlen;
		private JTextField jf;

		public limitedTFDocument(JTextField jf, int maxlen) {
			// TODO Auto-generated constructor stub
			this.jf = jf;
			this.maxlen = maxlen;
		}

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			// TODO Auto-generated method stub
			if (a != null) {
				return;
			}
			if (jf.getText().length() + str.length() <= maxlen) {
				super.insertString(offs, str, a);
			}
		}
	}

	// ---------------------------------------------------------------------
	public static final class DigitTFDocument extends PlainDocument {

		private static final long serialVersionUID = 1L;

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			// TODO Auto-generated method stub
			if (a != null) {
				return;
			}
			if (str.matches("^\\d+$")) {
				super.insertString(offs, str, a);
			}
		}
	}

	// ----------------------------------------------------------------
	public static final class limitedPortTFDocument extends PlainDocument {

		private static final long serialVersionUID = 1L;
		
		// 在给TF设置Document时,会把之前文本中的内容自动擦除，之后每次setText方法中的内容也会被检查
		private JTextField jf;
		private int maxlen;

		public limitedPortTFDocument(JTextField jf, int maxlen) {
			// TODO Auto-generated constructor stub
			this.jf = jf;
			this.maxlen = maxlen;
		}

		// 该方法甚至会影响setText()对文本内容的设置
		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			// TODO Auto-generated method stub
			if (a != null) {
				return;
			}

			if ("0".equals(jf.getText())) {
				if (jf.getCaret().getDot() != 0 || "0".equals(String.valueOf(str.charAt(0)))) {
					return;
				}
			}

			if ("0".equals(String.valueOf(str.charAt(0)))) {
				if (jf.getCaret().getDot() == 0 && !"".equals(jf.getText())) {
					return;
				}
			}

			if (str.matches("^\\d+$") && jf.getText().length() + str.length() <= maxlen) {
				super.insertString(offs, str, a);
			}
		}
	}

	// ----------------------------------------------------------
	public static final class PortTFDocument extends PlainDocument {

		private static final long serialVersionUID = 1L;
		
		// 在给TF设置Document时,会把之前文本中的内容自动擦除，之后每次setText方法中的内容也会被检查
		private JTextField jf;

		public PortTFDocument(JTextField jf) {
			// TODO Auto-generated constructor stub
			this.jf = jf;
		}

		// 该方法甚至会影响setText()对文本内容的设置
		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			// TODO Auto-generated method stub
			if (a != null) {
				return;
			}
			if ("0".equals(jf.getText())) {
				if (jf.getCaret().getDot() != 0 || "0".equals(String.valueOf(str.charAt(0)))) {
					return;
				}
			}
			if ("0".equals(String.valueOf(str.charAt(0)))) {
				if (jf.getCaret().getDot() == 0 && !"".equals(jf.getText())) {
					return;
				}
			}
			if (str.matches("^\\d+$")) {
				super.insertString(offs, str, a);
			}
		}
	}

	// ------------------------------------------------------------
	public static final class limitedUserTFDocument extends PlainDocument {

		private static final long serialVersionUID = 1L;
		
		private JTextField jf;// JText是Jpass的父类，增加可重用性没有使用jpf
		private int maxlen;

		public limitedUserTFDocument(JTextField jf, int maxlen) {
			// TODO Auto-generated constructor stub
			this.jf = jf;
			this.maxlen = maxlen;
		}

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			// TODO Auto-generated method stub
			if (a != null) {
				return;
			}
			if (str.contains(" ") || jf.getText().length() + str.length() > maxlen) {
				return;
			}
			super.insertString(offs, str, a);

		}
	}

	// ----------------------------------------------------------------------
	public static final class PasswordPFDocument extends PlainDocument {

		private static final long serialVersionUID = 1L;

		public PasswordPFDocument() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			// TODO Auto-generated method stub
			if (a != null) {
				return;
			}
			if (str.contains(" ")) {
				return;
			}
			super.insertString(offs, str, a);

		}
	}

	// --------------------------------------------------------
	public static final class tfFocusListener implements FocusListener {
		private String info;
		private JTextField jtf;

		public tfFocusListener(String info, JTextField jtf) {
			this.info = info;
			this.jtf = jtf;
		}

		@Override
		public void focusGained(FocusEvent e) {// 获得焦点的时候,清空提示文字
			String temp = jtf.getText();
			if (info.equals(temp)) {
				jtf.setText("");
			}
		}

		@Override
		public void focusLost(FocusEvent e) {// 失去焦点的时候,判断如果为空,就显示提示文字
			String temp = jtf.getText();
			if ("".equals(temp)) {
				jtf.setText(info);
			}
		}
	}

	// -----------------------------------------------------------------------------
	public static class SingleClassFileFilter extends FileFilter {

		private String extension;

		public SingleClassFileFilter(String extension) {
			// TODO Auto-generated constructor stub
			this.extension = extension;
		}

		@Override
		public boolean accept(File f) {
			// TODO Auto-generated method stub
			if (f.isDirectory()) {
				return true;
			}
			if (f.getName().endsWith(extension)) {
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return extension + " files(*." + extension + ")";
		}

	}

	// --------------------------------------------------------------------------
	public static class SingleClassFileSaveFilter extends SingleClassFileFilter {

		public SingleClassFileSaveFilter(String extension) {
			super(extension);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean accept(File f) {
			// TODO Auto-generated method stub
			if (f.isDirectory()) {
				return true;
			} else {
				return false;
			}
		}

	}

	// ---------------------------------------------------------------
	public static final class MyData<D extends Comparable<D>> implements Comparable<MyData<D>> {
		private D data;

		public MyData(D data) {
			// TODO Auto-generated constructor stub
			this.data = data;
		}

		public MyData() {
			// TODO Auto-generated constructor stub
			this(null);
		}

		public D getData() {
			return data;
		}

		public void setData(D data) {
			this.data = data;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return data.toString();
		}

		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			return data.equals(obj);
		}

		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return data.hashCode();
		}

		@Override
		public int compareTo(MyData<D> o) {
			// TODO Auto-generated method stub
			return data.compareTo(o.data);
		}

	}

	// ---------------------------------------------------------------
	public static class RemoteHostNetworkInfo {

		private Socket socket;
		private InetAddress inet;
		private String hostname;
		private int usingPort;
		private String ip;
		private byte[] ipByte;
		private String domainName;
		private InputStream in;
		private OutputStream out;

		public RemoteHostNetworkInfo(Socket socket, String domainName) {
			// TODO Auto-generated constructor stub
			this.domainName = domainName;

			this.socket = socket;
			try {
				in = this.socket.getInputStream();
				out = this.socket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.inet = socket.getInetAddress();
			this.hostname = inet.getHostName();
			this.usingPort = socket.getPort();
			this.ip = inet.getHostAddress();
			this.ipByte = inet.getAddress();
		}

		public RemoteHostNetworkInfo(Socket socket) {
			// TODO Auto-generated constructor stub
			this(socket, null);
		}

		public InetAddress getInet() {
			return inet;
		}

		public void setInet(InetAddress inet) {
			this.inet = inet;
		}

		public String getHostname() {
			return hostname;
		}

		public void setHostname(String hostname) {
			this.hostname = hostname;
		}

		public int getUsingPort() {
			return usingPort;
		}

		public void setUsingPort(int usingPort) {
			this.usingPort = usingPort;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public byte[] getIpByte() {
			return ipByte;
		}

		public void setIpByte(byte[] ipByte) {
			this.ipByte = ipByte;
		}

		public String getDomainName() {
			return domainName;
		}

		public void setDomainName(String domainName) {
			this.domainName = domainName;
		}

		public Socket getSocket() {
			return socket;
		}

		public void setSocket(Socket socket) {
			this.socket = socket;
		}

		public InputStream getIn() {
			return in;
		}

		public OutputStream getOut() {
			return out;
		}

	}

	// --------------------------------
	public static abstract class StopableThread extends Thread {

		protected volatile boolean end = false;
		protected volatile boolean pause = false;

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

	}
	// ---------------------------------------------------------
}
