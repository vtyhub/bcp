package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import server_c.ServerC;

public class MainUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private MainTabbed mainTabbed;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerC serverC = new ServerC();
					MainUI frame = new MainUI(serverC);
					serverC.setMainui(frame);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainUI(ServerC c) {
		setTitle("C");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 100, 900, 800);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		mainTabbed = new MainTabbed(c);
		add(mainTabbed);
	}

	public MainTabbed getMainTabbed() {
		return mainTabbed;
	}

	public void setMainTabbed(MainTabbed mainTabbed) {
		this.mainTabbed = mainTabbed;
	}
	
}
