package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.Client;

public class MainUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private Client Client;

	private JPanel contentPane;
	private MainTabbed mainTabbed;
	private JMenuBar mainBar;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI frame = new MainUI(new Client());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainUI(Client client) {
		this.Client = client;

		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(350, 140, 1015, 660);// 500 140 1015 660  ,500 140 989 660 比 tabbed大26

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		// panel大小若超过JTabbedPane大小，则无法加载
		mainTabbed = new MainTabbed(Client);
		contentPane.add(mainTabbed);

		mainBar = new JMenuBar();
		contentPane.add(mainBar);
	}

}
