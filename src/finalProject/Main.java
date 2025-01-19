// Declaring our Pixelate package.
package Pixelate;

// Our main class.
public class Main extends Grid {
	public Main(int gridSize) {
		super(gridSize);
	}
	
	public static void main(String args[]) {
		new Grid(32).makeGrid(32);
	}
}
