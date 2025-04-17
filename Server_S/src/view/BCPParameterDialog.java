package view;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class BCPParameterDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		try {
			BCPParameterDialog dialog = new BCPParameterDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//BorderLayout和GridLayout均可让所有组件跟随JFrame变化而调整大小
	//但也只适用于这样组件很少的对话框，组件多了两者都无法很好的从绝对布局转换过去
	public BCPParameterDialog(String paraname, String para, int len) {
		setBounds(100, 100, 450, 300);
		this.setTitle(paraname);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JScrollPane ParaSP = new JScrollPane();
		getContentPane().add(ParaSP);

		JTextArea paraTA = new JTextArea();
		paraTA.append(paraname + " = " + para + "\n\n");
		paraTA.append(paraname + " 's length = " + String.valueOf(len));
		ParaSP.setViewportView(paraTA);
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public BCPParameterDialog() {
		// TODO Auto-generated constructor stub
		this("N", "12312423543534534", 12);
	}
}
