package method;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Closeable;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class CommonMethod {

	public static String byteToHexStr(byte[] b) // 二行制转字符串
	{
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			// if (n < b.length - 1)
			// hs = hs + ":";
		}
		return hs.toUpperCase();
	}
	
	public static void batchClose(Closeable... streams) throws IOException {
		for (Closeable stream : streams) {
			stream.close();
		}
	}
	
	public static void setComponentsEnable(JComponent... Components) {
		for (JComponent b : Components) {
			if (b != null) {
				b.setEnabled(true);
			}
		}
	}

	public static void setComponentsDisable(JComponent... Components) {
		for (JComponent b : Components) {
			if (b != null) {
				b.setEnabled(false);
			}
		}
	}
	
	public static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
