// Declaring our Pixelate package.
package Pixelate;
// For our colors, we'll use the following...
import java.awt.Color;

public class Pixel {
	// Our default color will be white.
	Color pixelColor = Color.white;
	
	// Given a hex string, we set our pixel color accordingly.
	public void setColorFromHex(String hex) {
		this.pixelColor = Color.decode(hex);
		
	}
	
	// Setting our pixel color.
	public void setColor(Color color) {
		this.pixelColor = color;
		
	}
	
	// Getting our pixel color.
	public Color getColor() {
		return this.pixelColor;
		
	}
}
