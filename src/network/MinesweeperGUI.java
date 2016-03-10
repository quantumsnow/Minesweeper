package network; 
import java.awt.*;
import javax.swing.*; 
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class MinesweeperGUI {

	private JFrame frame;
	private MinesweeperClient client;
	private JPanel panel;
	private JButton squares [][];
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MinesweeperGUI window = new MinesweeperGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MinesweeperGUI() {
		client = new MinesweeperClient("localhost", 9000);
		client.getField();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 500, 420);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);

		 panel.setSize(400,400);
	     panel.setLayout(new GridLayout(10,10));
	     squares = new JButton[10][10];
	     buildButtons();
	     
	}
	
	private void buildButtons()
	{
		  for(int i=0;i<10;i++){
	          for(int j=0;j<10;j++){
	               squares[i][j] = new JButton();
	               squares[i][j].setSize(400,400);
	               panel.add(squares[i][j]);

	          }
	     }
	}
}
