package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import server_s.ServerS;

public class MainUI extends JFrame {

	private static final long serialVersionUID = 1L;

	// 视图组件
	private JPanel contentPane;

	// 模型组件
	private ServerS S;

	private MainTabbed mainTabbed;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerS serverS = new ServerS();
					MainUI frame = new MainUI(serverS);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainUI(ServerS s) {
		S = s;
		setTitle("S");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 100, 800, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		setContentPane(contentPane);

		mainTabbed = new MainTabbed(S);
		contentPane.add(mainTabbed);
	}

}
