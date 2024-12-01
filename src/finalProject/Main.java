package finalProject;

@SuppressWarnings("serial")
public class Main extends Grid{

	public Main(int gridSize) {
		super(gridSize);
	}
	
	public static void main(String args[]) {
		new Grid(32).makeGrid(32);

	}
}