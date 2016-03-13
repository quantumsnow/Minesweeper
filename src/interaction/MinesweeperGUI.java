package interaction;

import javax.swing.*;
import lib.List;
import logic.MinesweeperGame;
import logic.MinesweeperGame.Coordinates;
import logic.MinesweeperGame.Difficulty;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
	public class NewGameDialog extends JDialog {
		private static final long serialVersionUID = -2771354176586180644L;
		
		private final JPanel pnlCustom = new JPanel();
		private JTextField txtWidth;
		private JTextField txtHeight;
		private JTextField txtMineCount;

		/**
		 * Create the dialog.
		 */
		public NewGameDialog(MinesweeperClient client) {
			setTitle("Neues Spiel");
			getContentPane().setLayout(new BorderLayout());
			
			JPanel contentPanel = new JPanel();
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			pnlCustom.setBorder(new EmptyBorder(5, 5, 5, 5));
			
			JLabel lblSize = new JLabel("Größe");
			
			JLabel lblMinecount = new JLabel("Minenanzahl");
			
			txtWidth = new JTextField();
			txtWidth.setColumns(3);
			
			JLabel lblTimes = new JLabel("x");
			
			txtHeight = new JTextField();
			txtHeight.setColumns(3);
			
			txtMineCount = new JTextField();
			txtMineCount.setColumns(9);
			GroupLayout gl_pnlCustom = new GroupLayout(pnlCustom);
			gl_pnlCustom.setHorizontalGroup(
				gl_pnlCustom.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_pnlCustom.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_pnlCustom.createParallelGroup(Alignment.LEADING)
							.addComponent(lblMinecount)
							.addComponent(lblSize))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_pnlCustom.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_pnlCustom.createSequentialGroup()
								.addComponent(txtWidth, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(lblTimes)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(txtHeight, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
							.addComponent(txtMineCount, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)))
			);
			gl_pnlCustom.setVerticalGroup(
				gl_pnlCustom.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_pnlCustom.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_pnlCustom.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblSize)
							.addComponent(lblTimes)
							.addComponent(txtHeight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(txtWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_pnlCustom.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblMinecount)
							.addComponent(txtMineCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
			);
			pnlCustom.setLayout(gl_pnlCustom);
			
			JComboBox<String> comboBox = new JComboBox<>();
			comboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					switch (comboBox.getSelectedIndex()) {
					case 3:
						pnlCustom.setVisible(true);
						pack();
						break;
					default:
						pnlCustom.setVisible(false);
						pack();
						break;
					}
				}
			});
			comboBox.setModel(new DefaultComboBoxModel<>(new String[] {"Einfach", "Mittel", "Schwer", "Manuell"}));
			comboBox.setSelectedIndex(0);
			GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
			gl_contentPanel.setHorizontalGroup(
				gl_contentPanel.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_contentPanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
							.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
								.addComponent(comboBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addGroup(gl_contentPanel.createSequentialGroup()
								.addComponent(pnlCustom, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
			);
			gl_contentPanel.setVerticalGroup(
				gl_contentPanel.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_contentPanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(pnlCustom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			);
			contentPanel.setLayout(gl_contentPanel);
			
			{
				JPanel buttonPane = new JPanel();
				buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
				getContentPane().add(buttonPane, BorderLayout.SOUTH);
				{
					JButton okButton = new JButton("OK");
					okButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							switch (comboBox.getSelectedIndex()) {
							case 3:
								client.newGame(Integer.parseInt(txtWidth.getText()), Integer.parseInt(txtHeight.getText()), Integer.parseInt(txtMineCount.getText()));
								break;
							default:
								client.newGame(Difficulty.fromNumber(comboBox.getSelectedIndex()));
								break;
							}
							dispose();
						}
					});
					okButton.setActionCommand("OK");
					buttonPane.add(okButton);
					getRootPane().setDefaultButton(okButton);
				}
				{
					JButton cancelButton = new JButton("Abbrechen");
					cancelButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							dispose();
						}
					});
					cancelButton.setActionCommand("Cancel");
					buttonPane.add(cancelButton);
				}
			}
			
			pack();
			setLocationRelativeTo(null);
			
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			setVisible(true);
		}
	}
	
	private class EndDialog extends JDialog {
		private static final long serialVersionUID = 561800084824090963L;
		
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
			mark(MinesweeperGame.Cell.MARK_NONE);
			addMouseListener(markListener);
		}

		public int getIndexX() {
			return indexX;
		}

		public int getIndexY() {
			return indexY;
		}

		public void open(byte number) {
			removeMouseListener(markListener);
			setEnabled(false);
			setText(number == 0 ? "" : "" + number);
		}

		public void mark(int mark) {
			if (mark != this.mark) {
				this.mark = mark;
				setIcon(getMarkIcon(mark));
				if (mark == MinesweeperGame.Cell.MARK_NONE) {
					addActionListener(openListener);
				} else {
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

		private ImageIcon getMarkIcon(int mark) {
			switch (mark) {
			case MinesweeperGame.Cell.MARK_NONE:
				return null;
			case MinesweeperGame.Cell.MARK_MINE:
				return ICON_MARK_FLAG;
			case MinesweeperGame.Cell.MARK_UNKNOWN:
				return ICON_MARK_UNKNOWN;
			default:
				throw new IllegalArgumentException("Illegal mark int: " + mark);
			}
		}
	}

	private static final String PATH_ICONS = "icons/";
	private static final ImageIcon ICON_MINE = new ImageIcon(PATH_ICONS + "icon_mine.png"),
			ICON_MARK_FLAG = new ImageIcon(PATH_ICONS + "icon_mark_flag.png"),
			ICON_MARK_UNKNOWN = new ImageIcon(PATH_ICONS + "icon_mark_unknown.png"),
			ICON_EXPLOSION = new ImageIcon(PATH_ICONS + "icon_explosion.png");

	private JFrame frmMinesweeper;
	private MinesweeperClient client;
	private JPanel pnlField;
	private CellButton[][] field;

	private ActionListener openListener;
	private MouseListener markListener;
	private MinesweeperConnectGUI connectGui;
	private DefaultListModel<String> lstMdlPlayers;
	private JPanel pnlNewGame;
	private JPanel pnlPlayers;
	private JLabel lblPlayers;

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
		frmMinesweeper.setSize(500, 500);
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

		lblPlayers = new JLabel("Spieler:");

		JList<String> lstPlayers = new JList<>();
		lstPlayers.setModel((lstMdlPlayers = new DefaultListModel<>()));
		GroupLayout gl_pnlPlayers = new GroupLayout(pnlPlayers);
		gl_pnlPlayers.setHorizontalGroup(gl_pnlPlayers.createParallelGroup(Alignment.LEADING).addComponent(lstPlayers,
				100, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE).addComponent(lblPlayers,
						100, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));
		gl_pnlPlayers.setVerticalGroup(gl_pnlPlayers.createSequentialGroup().addComponent(lblPlayers,
				GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lstPlayers,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));
		pnlPlayers.setLayout(gl_pnlPlayers);
		

		pnlField = new JPanel();
		frmMinesweeper.getContentPane().add(pnlField, BorderLayout.CENTER);

		pnlNewGame = new JPanel();
		frmMinesweeper.getContentPane().add(pnlNewGame, BorderLayout.SOUTH);

		JButton btnNewGame = new JButton("Neues Spiel");
		btnNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new NewGameDialog(client);
			}
		});
		pnlNewGame.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		pnlNewGame.add(btnNewGame);

		frmMinesweeper.setLocationRelativeTo(null);
		frmMinesweeper.setVisible(true);
	}

	public void mark(int x, int y, int mark) {
		field[x][y].mark(mark);
	}

	public void won() {
		// mark mines and remove listeners
		for (CellButton[] column : field) {
			for (CellButton button : column) {
				button.removeActionListener(openListener);
				button.removeMouseListener(markListener);
				if (button.isEnabled()) {
					button.setIcon(ICON_MINE);
				}
			}
		}
		new EndDialog(true);
	}

	public void lost(Coordinates trigger, List mineCoordinates) {
		// remove listeners
		for (CellButton[] column : field) {
			for (CellButton button : column) {
				button.removeActionListener(openListener);
				button.removeMouseListener(markListener);
			}
		}
		
		// mark trigger
		field[trigger.getX()][trigger.getY()].setText(null);
		field[trigger.getX()][trigger.getY()].setIcon(ICON_EXPLOSION);
		field[trigger.getX()][trigger.getY()].setDisabledIcon(ICON_EXPLOSION);
		field[trigger.getX()][trigger.getY()].setEnabled(false);		
		
		// mark mines
		Coordinates coordinates;
		mineCoordinates.toFirst();
		while (mineCoordinates.hasAccess()) {
			coordinates = ((Coordinates) mineCoordinates.getObject());
			if (!coordinates.equals(trigger)) {
				field[coordinates.getX()][coordinates.getY()].setIcon(ICON_MINE);
			}
			mineCoordinates.next();
		}

		new EndDialog(false);
	}

	public void open(int x, int y, byte number) {
		field[x][y].open(number);
		pnlField.repaint();
	}

	public void newField(int width, int height, int mineCount) {
		frmMinesweeper.getContentPane().remove(pnlField);
		pnlField = new JPanel();
		frmMinesweeper.getContentPane().add(pnlField, BorderLayout.CENTER);
		pnlField.setLayout(new GridLayout(width, height));
		field = new CellButton[width][height];

		// build buttons
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x ++) {
				field[x][y] = new CellButton(x, y);
				pnlField.add(field[x][y]);
			}
		}
		pnlField.revalidate();
	}

	public void removePlayer(String nickname) {
		lstMdlPlayers.removeElement(nickname);
	}

	public void addPlayer(String nickname) {
		int i = 0;
		while (i < lstMdlPlayers.size() && nickname.compareToIgnoreCase(lstMdlPlayers.getElementAt(i)) >= 0) {
			i++;
		}
		lstMdlPlayers.add(i, nickname);
	}

	public void serverError() {
		System.out.println("Server answered with error");
	}
}
