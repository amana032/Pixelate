package finalProject;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


@SuppressWarnings("serial")

public class Grid extends JPanel implements MouseListener, MouseMotionListener {
	
	static JFrame window = new JFrame("Pixelate");
	
	// Declare buttons for the Tool Suite 
	JButton brushButton = new JButton("Brush");
	JButton fillButton = new JButton("Fill");
	JButton resetButton = new JButton("Reset");
	JButton undoButton = new JButton("Undo");
	
	
	static boolean isFill = false;
	static boolean isUndo = false;
	
	// Grid setup
	int gridNum; // number of pixel boxes
	int gridLength; // exact pixel measurement of grid
	static int brushSize = 1; // current brush size
	
	// Color objects and array
	Color[] paletteColors = new Color[] { Color.red, Color.orange, Color.yellow, Color.green, Color.cyan, Color.blue,
			Color.MAGENTA, Color.pink, Color.darkGray, Color.lightGray, Color.black, Color.white };
	Color currentColor = Color.blue;
	Color secondColor = Color.black;
	Color backgroundColor = new Color(238, 238, 238);
	
	// Canvas + copies of the canvas for undo-redo
	static square[][] gridColors;
	static square[][] previousColors;
	static square[][] recentColors;

	public Grid(int gridSize) {
		gridNum = gridSize;
		gridLength = gridSize * 15 + 30; // Pixel dimensions of the grid
		
		// Create the buttons on the window
		window.setBackground(backgroundColor);
		window.add(brushButton);
		window.add(fillButton);
		window.add(resetButton);
		window.add(undoButton);

		// Brush commands
		brushButton.setActionCommand("brush thing");
		brushButton.setBounds(575, 50, 50, 50);
		brushButton.setMargin(new Insets(0, 0, 0, 0));
		brushButton.addActionListener(new listener());

		// Fill Tool commands
		fillButton.setActionCommand("fill thing");
		fillButton.setBounds(575, 200, 50, 50);
		fillButton.addActionListener(new listener());

		// Reset commands
		resetButton.setActionCommand("reset thing");
		resetButton.setBounds(770, 50, 50, 50);
		resetButton.setMargin(new Insets(0, 0, 0, 0));
		resetButton.addActionListener(new listener());

		// Undo Tool commands
		undoButton.setActionCommand("undo thing");
		undoButton.setBounds(770, 200, 50, 50);
		undoButton.setMargin(new Insets(0, 0, 0, 0));
		undoButton.addActionListener(new listener());

		setFocusable(true);

		// Event listeners created and added to the window
		addMouseListener(this);
		addMouseMotionListener(this);
		window.addMouseListener(this);
		window.addMouseMotionListener(this);

		// 2D arrays containing the current canvas, a copy of the canvas before the last stroke, and a copy of the current canvas
		gridColors = new square[gridSize][gridSize];
		previousColors = new square[gridSize][gridSize];
		recentColors = new square[gridSize][gridSize];
		
		// Fill each array with square objects
		for (int i = 0; i < gridColors.length; i++) {
			for (int j = 0; j < gridColors[i].length; j++) {
				gridColors[i][j] = new square();

			}
		}

		for (int i = 0; i < recentColors.length; i++) {
			for (int j = 0; j < recentColors[i].length; j++) {
				recentColors[i][j] = new square();

			}
		}

		for (int i = 0; i < previousColors.length; i++) {
			for (int j = 0; j < previousColors[i].length; j++) {
				previousColors[i][j] = new square();

			}
		}
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		int colorCounter = 0;

		// paints grid
		for (int i = 0; i < gridNum; i++) {
			for (int j = 0; j < gridNum; j++) {

				// Buttons are rude and wont change colors dynamically
				// so here's a toggle instead
				if (isFill) {
					g2d.setColor(Color.GRAY);
					g2d.fillRect(570, 195, 5, 55);
					g2d.fillRect(570, 195, 55, 5);
					g2d.fillRect(625, 195, 5, 60);
					g2d.fillRect(570, 250, 55, 5);
				}

				// Draws the color boxes
				g2d.setColor(gridColors[i][j].getColor());
				g2d.fillRect((i * 15) + 30, (j * 15) + 30, 15, 15);

				// Draws the borders of color boxes
				g2d.setColor(Color.gray);
				g2d.drawRect((i * 15) + 30, (j * 15) + 30, 15, 15);

			}
		}

		// Paints the color palette
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 2; j++) {
				g2d.setColor(paletteColors[colorCounter]);
				colorCounter++;
				g2d.fillRect((i * 30) + 30, (j * 30) + gridLength + 30, 30, 30);
				g2d.setColor(Color.darkGray);
				g2d.drawRect((i * 30) + 30, (j * 30) + gridLength + 30, 30, 30);

			}
		}

		// Paints Current Color Box
		g2d.setColor(currentColor);
		g2d.fillRect(240, 540, 60, 60);
		g2d.setColor(Color.darkGray);
		g2d.drawRect(240, 540, 60, 60);

		// Paints Second Color Box
		g2d.setColor(secondColor);
		g2d.fillRect(330, 540, 60, 60);
		g2d.setColor(Color.darkGray);
		g2d.drawRect(330, 540, 60, 60);

	}

	// Mouse Listener overrides
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int row = (int) (x / 15) - 2;
		int col = (int) (y / 15) - 2;
		int colorRow = (int) (x / 30) - 1;
		int colorCol = (int) (y / 30) - 18;
		Color setColor = null;

		if (SwingUtilities.isLeftMouseButton(e))
			setColor = currentColor;

		if (SwingUtilities.isRightMouseButton(e))
			setColor = secondColor;

		if (SwingUtilities.isMiddleMouseButton(e))
			return;

		// System.out.println(row + ", " + col);
		// System.out.println("Point: " + x + ", " + y);

		// Painting on the grid
		if (x < 30 + (gridNum * 15) && x > 30 && y < 30 + (gridNum * 15) && y > 30) {

			// Reset button (makes backup grid)
			for (int i = 0; i < 32; i++) {
				for (int j = 0; j < 32; j++) {
					previousColors[i][j].setColorBasic(gridColors[i][j].getColor());

					if (isUndo == true) {
						recentColors[i][j].setColorBasic(gridColors[i][j].getColor());
						isUndo = false;
					}
				}
			}

			// fill tool
			if (isFill == true) {
				fillTool(row, col, gridColors[row][col].getColor(), setColor);

			} else if (isFill == false) {

				gridColors[row][col].setColorBasic(setColor);
				brushCheck(row, col, setColor);
				
			}
		}

		// Clicking on the color palette
		if (x < 210 && x > 30 && y < gridLength + 90 && y > gridLength + 30) {
			if (colorCol == 0) {
				if (colorRow == 0)
					setColor = Color.red;

				if (colorRow == 1)
					setColor = Color.yellow;

				if (colorRow == 2)
					setColor = Color.cyan;

				if (colorRow == 3)
					setColor = Color.magenta;

				if (colorRow == 4)
					setColor = Color.darkGray;

				if (colorRow == 5)
					setColor = Color.black;

			} else {
				if (colorRow == 0)
					setColor = Color.orange;

				if (colorRow == 1)
					setColor = Color.green;

				if (colorRow == 2)
					setColor = Color.blue;

				if (colorRow == 3)
					setColor = Color.pink;

				if (colorRow == 4)
					setColor = Color.lightGray;

				if (colorRow == 5)
					setColor = Color.white;
			}

			if (SwingUtilities.isLeftMouseButton(e))
				currentColor = setColor;

			if (SwingUtilities.isRightMouseButton(e))
				secondColor = setColor;
		}

		window.repaint();

	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	public void brushCheck(int row, int col, Color setColor) {
		if (brushSize == 2) {
			if (row > 0) {
				gridColors[row - 1][col].setColorBasic(setColor);
			}
			if (row < gridNum - 1) {
				gridColors[row + 1][col].setColorBasic(setColor);
			}
			if (col > 0) {
				gridColors[row][col - 1].setColorBasic(setColor);
			}
			if (col < gridNum - 1) {
				gridColors[row][col + 1].setColorBasic(setColor);
			}

		} else if (brushSize == 3) {
			if (row > 0)
				gridColors[row - 1][col].setColorBasic(setColor);

			if (row < gridNum - 1)
				gridColors[row + 1][col].setColorBasic(setColor);

			if (col > 0)
				gridColors[row][col - 1].setColorBasic(setColor);

			if (col < gridNum - 1)
				gridColors[row][col + 1].setColorBasic(setColor);

			if (row > 0 && col > 0)
				gridColors[row - 1][col - 1].setColorBasic(setColor);

			if (row < gridNum - 1 && col < gridNum - 1)
				gridColors[row + 1][col + 1].setColorBasic(setColor);

			if (row > 0 && col < gridNum - 1)
				gridColors[row - 1][col + 1].setColorBasic(setColor);

			if (row < gridNum - 1 && col > 0)
				gridColors[row + 1][col - 1].setColorBasic(setColor);

			if (row > 1)
				gridColors[row - 2][col].setColorBasic(setColor);

			if (col > 1)
				gridColors[row][col - 2].setColorBasic(setColor);

			if (row < gridNum - 2)
				gridColors[row + 2][col].setColorBasic(setColor);

			if (col < gridNum - 2)
				gridColors[row][col + 2].setColorBasic(setColor);
		}

	}

	// Motion Listener overrides
	@Override
	public void mouseDragged(MouseEvent e) {

		if (!isFill) {
			int x = e.getX();
			int y = e.getY();
			int row = (int) (x / 15) - 2;
			int col = (int) (y / 15) - 2;
			Color setColor = null;

			if (SwingUtilities.isLeftMouseButton(e))
				setColor = currentColor;

			if (SwingUtilities.isRightMouseButton(e))
				setColor = secondColor;

			if (SwingUtilities.isMiddleMouseButton(e))
				return;

			// Paints grid while mouse is dragging
			if (x < 28 + (gridNum * 15) && x > 30 && y < 30 + (gridNum * 15) && y > 30) {
				gridColors[row][col].setColorBasic(setColor);
				brushCheck(row, col, setColor);

			}

			repaint();
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	public void fillTool(int x, int y, Color prev, Color current) {
		// System.out.println("fill function starts");
		if (x < 0 || x >= gridNum || y < 0 || y >= gridNum) { // Current square does not exist within bounds
			// System.out.println("first base case fails");
			return;
		}

		if (gridColors[x][y].getColor() != prev) { // Current square is not the color we want to replace
			// System.out.println("second base case fails"); 
			return;
		}

		if (gridColors[x][y].getColor() == current) { // Current square is already the target color, skip it
			// System.out.println("third base case fails"); 
			return;
		}

		gridColors[x][y].setColorBasic(current);

		// Attempt to fill squares in the cardinal directions
		fillTool(x + 1, y, prev, current);
		fillTool(x - 1, y, prev, current);
		fillTool(x, y + 1, prev, current);
		fillTool(x, y - 1, prev, current);

	}

	public class listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			// Fill tool button pressed
			if (e.getActionCommand().equals("fill thing")) {
				if (Grid.isFill == false) {
					// System.out.println("turn on fill");
					Grid.isFill = true;

				} else if (Grid.isFill == true) {
					Grid.isFill = false;
					// System.out.println("turn off fill");

				}
			}

			// Brush size tool button pressed
			if (e.getActionCommand().equals("brush thing")) {
				if (brushSize == 3) {
					Grid.brushSize = 1;
				} else {
					Grid.brushSize++;
				}

				// System.out.println(brushSize);
			}

			// Reset tool button pressed
			if (e.getActionCommand().equals("reset thing")) {
				// System.out.println("reset");

				for (int i = 0; i < 32; i++)
					for (int j = 0; j < 32; j++)
						previousColors[i][j].setColorBasic(gridColors[i][j].getColor());

				for (int i = 0; i < gridColors.length; i++)
					for (int j = 0; j < gridColors[i].length; j++)
						Grid.gridColors[i][j].setColorBasic(Color.white);
			}

			// Undo tool button pressed
			if (e.getActionCommand().equals("undo thing")) {
				// System.out.println("reset");

				if (Grid.isUndo == false) {
					Grid.isUndo = true;
					// System.out.println("undo");

					for (int i = 0; i < 32; i++)
						for (int j = 0; j < 32; j++)
							Grid.recentColors[i][j].setColorBasic(Grid.gridColors[i][j].getColor());
					for (int i = 0; i < gridColors.length; i++)
						for (int j = 0; j < gridColors[i].length; j++)
							Grid.gridColors[i][j].setColorBasic(Grid.previousColors[i][j].getColor());

				} else if (Grid.isUndo == true) {
					Grid.isUndo = false;
					// System.out.println("redo");
					undoButton.setText("Undo");

					for (int i = 0; i < 32; i++)
						for (int j = 0; j < 32; j++)
							Grid.gridColors[i][j].setColorBasic(Grid.recentColors[i][j].getColor());

				}
			}
			
			window.repaint();
			Grid.window.requestFocus();

		}
	}

	public void makeGrid(int gridSize) {
		window.add(new Grid(gridSize));
		window.setSize(900, 700);
		window.setVisible(true);
		window.setLayout(null);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
