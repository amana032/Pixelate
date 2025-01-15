package cs178;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")

public class Grid extends JPanel implements MouseListener, MouseMotionListener {
	
	static JFrame window = new JFrame("Pixelate");
	static JFrame toolSuite = new JFrame("Tools");
	JPanel tools = new JPanel(new GridLayout(5, 1));
	colorChooser colorWheel = new colorChooser();
	
	// Declare buttons for the Tool Suite 
	JButton brushButton = new JButton("Brush");
	JButton fillButton = new JButton("Fill");
	JButton resetButton = new JButton("Reset");
	JButton undoButton = new JButton("Undo");
	JButton colorButton = new JButton("Color Picker");
	
	// Text for color approximation
	JLabel colorApprox = new JLabel("");
	
	static boolean isFill = false;
	static boolean isUndo = false;
	
	// Mouse listeners
    int globalX = 10000;
    int globalY;
    int globalRow; 
    int globalCol;
	
	// Grid setup
	int gridNum; // number of pixel boxes
	int gridLength; // exact pixel measurement of grid
	static int brushSize = 1; // current brush size
	
	// Color objects and array
	Color[] paletteColors = new Color[] { Color.red, Color.orange, Color.yellow, Color.green, Color.cyan, Color.blue,
			Color.MAGENTA, Color.pink, Color.darkGray, Color.lightGray, Color.black, Color.white };
	Color currentColor = Color.blue;
	Color customColor = Color.white;
	Color secondColor = Color.black;
	Color backgroundColor = new Color(238, 238, 238);
	
	// Canvas + copies of the canvas for undo-redo
	static square[][] gridColors;
	static square[][] previousColors;
	static square[][] recentColors;
	
	public void Tools() { 
	    tools.setLayout(new GridLayout(4, 1)); 
	    tools.add(brushButton);
	    tools.add(fillButton);
	    tools.add(resetButton);
	    tools.add(undoButton);
	    tools.add(colorButton);
	}

	public Grid(int gridSize) {
		gridNum = gridSize;
		gridLength = gridSize * 15 + 30; // Pixel dimensions of the grid
		
		// Create the buttons on the window
		window.setBackground(backgroundColor);
		window.add(colorApprox);
		colorApprox.setBounds(10,10,200,20);
		
		// Brush commands
		brushButton.setActionCommand("brush thing");
		brushButton.addActionListener(new listener());
		brushButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		brushButton.setBackground(Color.WHITE);

		// Fill Tool commands
		fillButton.setActionCommand("fill thing");
		fillButton.addActionListener(new listener());
		fillButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		fillButton.setBackground(Color.WHITE);

		// Reset commands
		resetButton.setActionCommand("reset thing");
		resetButton.addActionListener(new listener());
		resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		resetButton.setBackground(Color.WHITE);

		// Undo Tool commands
		undoButton.setActionCommand("undo thing");
		undoButton.addActionListener(new listener());
		undoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		undoButton.setBackground(Color.WHITE);
		
		colorButton.setActionCommand("color thing");
		colorButton.addActionListener(new listener());
		colorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		colorButton.setBackground(Color.WHITE);
		
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
	public void paint(Graphics g) { // Window painting (aka draw)
		Graphics2D g2d = (Graphics2D) g;
		int colorCounter = 0;

		// paints grid
		for (int i = 0; i < gridNum; i++) {
			for (int j = 0; j < gridNum; j++) {

				// Draws the pixel boxes
				g2d.setColor(gridColors[i][j].getColor());
				g2d.fillRect((i * 15) + 30, (j * 15) + 30, 15, 15);

				// Draws the borders of pixel boxes
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
		window.repaint();
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

//		// Clicking on the color palette
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
		colorApprox.setText("");
        globalX = e.getX();
        globalY = e.getY();
        Color color = null;
        String colorName;
        if (e.getX() < 30 + (gridNum * 15) && e.getX() > 30 && e.getY() < 30 + (gridNum * 15) && e.getY() > 30) {
            globalRow = (globalX / 15) - 2;
            globalCol = (globalY / 15) - 2;
            color = gridColors[globalRow][globalCol].getColor();
            
            Map<Color, String> colorNames = new HashMap<>();
            colorNames.put(Color.RED, "Red");
            colorNames.put(Color.ORANGE, "Orange");
            colorNames.put(Color.YELLOW, "Yellow");
            colorNames.put(Color.GREEN, "Green");
            colorNames.put(Color.CYAN, "Cyan");
            colorNames.put(Color.BLUE, "Blue");
            colorNames.put(Color.MAGENTA, "Magenta");
            colorNames.put(Color.darkGray, "Dark Gray");
            colorNames.put(Color.lightGray, "Light Gray");
            colorNames.put(Color.WHITE, "White");
            colorNames.put(Color.BLACK, "Black");
            
            colorName = colorNames.get(color);
            colorApprox.setText(colorName);
        }
        
        window.repaint();

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
			
			// Color picker pressed
			if (e.getActionCommand().equals("color thing")) {
				 Color initialcolor = Color.WHITE;
				 
			        // color chooser Dialog Box
				 customColor = JColorChooser.showDialog(null,
			                    "Select a color", initialcolor);
				 Grid.window.requestFocus();
			 
			        
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
		window.setSize(560, 700);
		window.setVisible(true);
		window.setLayout(null);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Set up tool suite
	    Tools(); // Ensure buttons are added properly
	    toolSuite.add(tools);
	    toolSuite.setSize(250, 200);
	    toolSuite.setResizable(false);
	    toolSuite.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    toolSuite.setVisible(true);
	    
	    //colorWheel.createAndShowGUI();
	}
}
