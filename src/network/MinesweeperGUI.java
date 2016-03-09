package network;

import java.awt.EventQueue;

import javax.swing.JFrame;

import lib.List;

public class MinesweeperGUI {

	private JFrame frame;

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
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void open(int x, int y, int number) {
		// TODO
	}

	public void mark(int x, int y, int mark) {
		// TODO
	}
	
	public void won(int x, int y, List mines) {
		
	}
	
	public void lost(int x, int y, List mines) {
		
	}
	
	public void addPlayer(String nick) {
		
	}
	
	public void removePlayer(String nick) {
		
	}
	
	public void newGame(int width, int height, int mineCount) {
		
	}
	
	public void serverError() {
		
	}
}
