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
import javax.swing.border.EmptyBorder;

import org.omg.PortableServer.ServantRetentionPolicyValue;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class MinesweeperGUI {
	public class EndDialog extends JDialog {
		private static final String MESSAGE_WON = "Sie haben gewonnen.", MESSAGE_LOST = "Sie haben verloren.";

		private EndDialog(boolean won) {
			setTitle("Ende");
			getContentPane().setLayout(new BorderLayout());

			JPanel contentPanel = new JPanel();
			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			contentPanel.setLayout(new BorderLayout(0, 0));
			{
				JLabel lblStatus = new JLabel(won ? MESSAGE_WON : MESSAGE_LOST);
				contentPanel.add(lblStatus);
			}
			{
				JPanel buttonPane = new JPanel();
				buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
				getContentPane().add(buttonPane, BorderLayout.SOUTH);
				{
					JButton okButton = new JButton("OK");
					okButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							dispose();
						}
					});
					okButton.setActionCommand("OK");
					buttonPane.add(okButton);
					getRootPane().setDefaultButton(okButton);
				}
			}
			
			pack();
			setLocationRelativeTo(null);

			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			setVisible(true);
		}
	}
	
	private class CellButton extends JButton {
		private static final long serialVersionUID = -1722545135914719909L;

		private int indexX, indexY, mark;

		private CellButton(int indexX, int indexY) {
			super();
			this.indexX = indexX;
			this.indexY = indexY;
			mark = MinesweeperGame.Cell.MARK_NONE;
		}

		public int getIndexX() {
			return indexX;
		}

		public int getIndexY() {
			return indexY;
		}

		public void setMark(int mark) {
			if (mark != this.mark) {
				this.mark = mark;
				setText(getMarkString(mark));
				if (mark == MinesweeperGame.Cell.MARK_NONE) {
					addActionListener(openListener);
				} else if (getActionListeners().length > 0) {
					removeActionListener(openListener);
				}
			}
		}

		public int getNextMark() {
			int nextMark = mark - 1;
			if (nextMark == -4) {
				nextMark = MinesweeperGame.Cell.MARK_NONE;
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

	private JFrame frmMinesweeper;
	private MinesweeperClient client;
	private JPanel pnlField;
	private CellButton[][] field;

	private ActionListener openListener;
	private MouseListener markListener;
	private MinesweeperConnectGUI connectGui;
	private DefaultListModel<String> lstMdlPlayers;
	private JPanel pnlNewGame;
	private JLabel lblWdith;
	private JTextField txtWidth;
	private JLabel lblHeight;
	private JTextField txtHeight;
	private JLabel lblMines;
	private JTextField txtMinecount;
	private JPanel pnlPlayers;

	/**
	 * Create the application.
	 */
	public MinesweeperGUI(String host, int port, String nick, MinesweeperConnectGUI connectGui) {
		initialize(host, port, nick);
		openListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CellButton source = (CellButton) e.getSource();
				client.open(source.getIndexX(), source.getIndexY());
			}
		};
		markListener = new MouseListener() {
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
				if (SwingUtilities.isRightMouseButton(e)) {
					CellButton source = (CellButton) e.getSource();
					client.mark(source.getIndexX(), source.getIndexY(), source.getNextMark());
				}
			}
		};
		client = new MinesweeperClient(host, port, nick, this);
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
		});
		;

		pnlPlayers = new JPanel();
		frmMinesweeper.getContentPane().add(pnlPlayers, BorderLayout.EAST);

		JList<String> lstPlayers = new JList<>();
		lstPlayers.setModel((lstMdlPlayers = new DefaultListModel<>()));
		lstPlayers.setMinimumSize(new Dimension(40, 0));
		GroupLayout gl_pnlPlayers = new GroupLayout(pnlPlayers);
		gl_pnlPlayers.setHorizontalGroup(gl_pnlPlayers.createParallelGroup(Alignment.LEADING).addComponent(lstPlayers,
				GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		gl_pnlPlayers.setVerticalGroup(gl_pnlPlayers.createParallelGroup(Alignment.LEADING).addComponent(lstPlayers,
				GroupLayout.PREFERRED_SIZE, 363, GroupLayout.PREFERRED_SIZE));
		pnlPlayers.setLayout(gl_pnlPlayers);

		pnlField = new JPanel();
		frmMinesweeper.getContentPane().add(pnlField, BorderLayout.CENTER);

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
				client.newGame(Integer.parseInt(txtWidth.getText()), Integer.parseInt(txtHeight.getText()),
						Integer.parseInt(txtMinecount.getText()));
			}
		});
		GroupLayout gl_pnlNewGame = new GroupLayout(pnlNewGame);
		gl_pnlNewGame
				.setHorizontalGroup(
						gl_pnlNewGame
								.createParallelGroup(
										Alignment.LEADING)
								.addGroup(
										gl_pnlNewGame.createSequentialGroup().addContainerGap().addComponent(lblWdith)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(txtWidth, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
												.addPreferredGap(ComponentPlacement.RELATED).addComponent(lblHeight)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(txtHeight, GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
												.addPreferredGap(ComponentPlacement.RELATED).addComponent(lblMines)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(txtMinecount, GroupLayout.DEFAULT_SIZE, 80,
														Short.MAX_VALUE)
												.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnNewGame)
												.addGap(2)));
		gl_pnlNewGame
				.setVerticalGroup(gl_pnlNewGame.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_pnlNewGame.createSequentialGroup()
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(gl_pnlNewGame.createParallelGroup(Alignment.BASELINE).addComponent(lblWdith)
										.addComponent(txtWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
								.addComponent(lblHeight)
								.addComponent(txtHeight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE).addComponent(lblMines)
								.addComponent(txtMinecount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE).addComponent(btnNewGame))));
		pnlNewGame.setLayout(gl_pnlNewGame);
		
		frmMinesweeper.setLocationRelativeTo(null);
		frmMinesweeper.setVisible(true);
	}

	public void mark(int x, int y, int mark) {
		field[x][y].setMark(mark);
	}

	public void won() {
		// mark mines and remove listeners
		for (CellButton[] column : field) {
			for (CellButton button : column) {
				button.removeActionListener(openListener);
				button.removeMouseListener(markListener);
				if (button.isEnabled()) {
					button.setMark(MinesweeperGame.Cell.MARK_MINE);
				}
			}
		}
		new EndDialog(true);
	}

	public void lost(List mineCoordinates) {
		// remove listeners
		for (CellButton[] column : field) {
			for (CellButton button : column) {
				button.removeActionListener(openListener);
				button.removeMouseListener(markListener);
			}
		}

		// mark mines
		Coordinates coordinates;
		mineCoordinates.toFirst();
		while (mineCoordinates.hasAccess()) {
			coordinates = ((Coordinates) mineCoordinates.getObject());
			mark(coordinates.getX(), coordinates.getY(), MinesweeperGame.Cell.MARK_MINE);
			mineCoordinates.next();
		}
		
		new EndDialog(false);
	}

	public void open(int x, int y, int number) {
		field[x][y].setEnabled(false);
		field[x][y].removeMouseListener(markListener);
		field[x][y].setText(number == 0 ? "" : "" + number);
	}

	public void setField(int width, int height, int mineCount) {
		frmMinesweeper.getContentPane().remove(pnlField);
		pnlField = new JPanel();
		frmMinesweeper.getContentPane().add(pnlField, BorderLayout.CENTER);
		pnlField.setLayout(new GridLayout(width, height));
		field = new CellButton[width][height];

		// build buttons
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				field[x][y] = new CellButton(x, y);
				field[x][y].addActionListener(openListener);
				field[x][y].addMouseListener(markListener);
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
