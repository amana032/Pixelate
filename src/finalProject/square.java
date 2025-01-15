package cs178;

import java.awt.Color;

public class square {
	Color squareColor = Color.white;
	
	public void setColorString(String hex) {
		this.squareColor = Color.decode(hex);
		
	}
	
	public void setColorBasic(Color colorName) {
		this.squareColor = colorName;
		
	}
	
	public Color getColor() {
		return this.squareColor;
		
	}
}
