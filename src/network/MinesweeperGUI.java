package network;

import javax.swing.*;
import lib.List;
import logic.MinesweeperGame;
import logic.MinesweeperGame.Coordinates;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.omg.PortableServer.ServantRetentionPolicyValue;

public class MinesweeperGUI {
	private class CellButton extends JButton {
		private static final long serialVersionUID = -1722545135914719909L;
		
		private int x, y, mark;
		
		private CellButton(int x, int y) {
			super();
			this.x = x;
			this.y = y;
			mark = -1;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
		
		public void setMark(int mark) {
			this.mark = mark;
			setText(getMarkString(mark));
		}

		public int getNextMark() {
			int nextMark = mark - 1;
			if (nextMark == -3) {
				nextMark = -1;
			}
			return nextMark;
		}
		
		private String getMarkString(int mark) {
			switch (mark) {
			case MinesweeperGame.Cell.MARK_NONE:
				return "";
			case MinesweeperGame.Cell.MARK_MINE:
				return "M";
			case MinesweeperGame.Cell.MARK_UNKNOWN:
				return "?";
			default:
				throw new IllegalArgumentException("Illegal mark int: " + mark);
			}
		}
	}
	
	private static final int BUTTON_SIZE = 40;

	private JFrame frmMinesweeper;
	private MinesweeperClient client;
	private JPanel pnlField;
	private CellButton[][] field;
	
	private MinesweeperConnectGUI connectGui;
	private DefaultListModel<String> lstMdlPlayers;
	private JPanel pnlNewGame;
	private JLabel lblWdith;
	private JTextField txtWidth;
	private JLabel lblHeight;
	private JTextField txtHeight;
	private JLabel lblMines;
	private JTextField txtMinecount;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					MinesweeperGUI window = new MinesweeperGUI();
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the application.
	 */
	public MinesweeperGUI(String host, int port, String nick, MinesweeperConnectGUI connectGui) {
		client = new MinesweeperClient(host, port, nick, this);
		initialize(host, port, nick);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String host, int port, String nick) {
		frmMinesweeper = new JFrame();
		frmMinesweeper.setTitle("Minesweeper: " + nick + "@" + host + ":" + port);
		frmMinesweeper.setBounds(100, 100, 500, 420);
		frmMinesweeper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMinesweeper.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				connectGui.setVisible(true);
				super.windowClosed(e);
			}
		});;

		pnlField = new JPanel();
		frmMinesweeper.getContentPane().add(pnlField, BorderLayout.CENTER);
		
		JList<String> lstPlayers = new JList<>();
		lstPlayers.setModel((lstMdlPlayers = new DefaultListModel<>()));
		frmMinesweeper.getContentPane().add(lstPlayers, BorderLayout.EAST);
		
		pnlNewGame = new JPanel();
		frmMinesweeper.getContentPane().add(pnlNewGame, BorderLayout.SOUTH);
		
		lblWdith = new JLabel("Width");
		
		txtWidth = new JTextField();
		txtWidth.setText("10");
		txtWidth.setColumns(10);
		
		lblHeight = new JLabel("Height");
		
		txtHeight = new JTextField();
		txtHeight.setText("10");
		txtHeight.setColumns(10);
		
		lblMines = new JLabel("Mines");
		
		txtMinecount = new JTextField();
		txtMinecount.setText("30");
		txtMinecount.setColumns(10);
		
		JButton btnNewGame = new JButton("New game");
		btnNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.newGame(Integer.parseInt(txtWidth.getText()), Integer.parseInt(txtHeight.getText()), Integer.parseInt(txtMinecount.getText()));
			}
		});
		GroupLayout gl_pnlNewGame = new GroupLayout(pnlNewGame);
		gl_pnlNewGame.setHorizontalGroup(
			gl_pnlNewGame.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlNewGame.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblWdith)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtWidth, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblHeight)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtHeight, GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblMines)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtMinecount, GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewGame)
					.addGap(2))
		);
		gl_pnlNewGame.setVerticalGroup(
			gl_pnlNewGame.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlNewGame.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(gl_pnlNewGame.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblWdith)
						.addComponent(txtWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblHeight)
						.addComponent(txtHeight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMines)
						.addComponent(txtMinecount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnNewGame)))
		);
		pnlNewGame.setLayout(gl_pnlNewGame);

		frmMinesweeper.setVisible(true);
	}

	public void mark(int x, int y, int mark) {
		field[x][y].setMark(mark);
	}
	
	public void won(List mineCoordinates) {
		markMines(mineCoordinates);
		removeCellButtonListeners();
	}

	public void lost(List mineCoordinates) {
		markMines(mineCoordinates);
		removeCellButtonListeners();
	}
	
	private void markMines(List mineCoordinates) {
		Coordinates coordinates;
		mineCoordinates.toFirst();
		while (mineCoordinates.hasAccess()) {
			coordinates = ((Coordinates) mineCoordinates.getObject());
			mark(coordinates.getX(), coordinates.getY(), MinesweeperGame.Cell.MARK_MINE);
			mineCoordinates.next();
		}		
	}
	
	private void removeCellButtonListeners() {
		for (CellButton[] column : field) {
			for (CellButton button : column) {
				button.removeActionListener(button.getActionListeners()[0]);
				if (button.isEnabled()) {
					button.removeMouseListener(button.getMouseListeners()[0]);
				}
			}
		}		
	}

	public void open(int x, int y, int number) {
		field[x][y].setEnabled(false);
		field[x][y].removeMouseListener(field[x][y].getMouseListeners()[0]);
		field[x][y].setText("" + number);
	}

	public void setField(int width, int height, int mineCount) {
		pnlField.setLayout(new GridLayout(width, height));
		pnlField.setSize(width * BUTTON_SIZE, height * BUTTON_SIZE);
		field = new CellButton[width][height];
		
		// build buttons
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				field[x][y] = new CellButton(x, y);
				field[x][y].setSize(BUTTON_SIZE, BUTTON_SIZE);
				field[x][y].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						CellButton source = (CellButton) e.getSource();
						client.open(source.getX(), source.getY());
					}
				});
				field[x][y].addMouseListener(new MouseListener() {
					@Override
					public void mouseReleased(MouseEvent e) {
					}
					
					@Override
					public void mousePressed(MouseEvent e) {
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
					}
					
					@Override
					public void mouseEntered(MouseEvent e) {
					}
					
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.isPopupTrigger()) {
							CellButton source = (CellButton) e.getSource();
							client.mark(source.getX(), source.getY(), source.getNextMark());
						}
					}
				});
				pnlField.add(field[x][y]);
			}
		}
	}

	public void removePlayer(String nickname) {
		lstMdlPlayers.removeElement(nickname);
	}

	public void addPlayer(String nickname) {
		lstMdlPlayers.addElement(nickname);
	}
	
	public void serverError() {
		System.out.println("Server answered with error");
	}
}
