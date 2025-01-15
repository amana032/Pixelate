// Java program to implement JColorChooser
// class using ChangeListener

package cs178;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.colorchooser.*;
 
@SuppressWarnings("serial")

public class colorChooser extends JPanel
 
    implements ChangeListener {
 
    public JColorChooser Jcc;
    public Color chosenColor;
    
    public Color getCurrentColor() {
    	System.out.println(chosenColor);
        return chosenColor; // Returns the color selected by the user
    }
 
    public colorChooser()
    {
        super(new BorderLayout());
 
        // Set up color chooser for setting text color
        Jcc = new JColorChooser();
        Jcc.setColor(Color.GREEN);
        Jcc.getSelectionModel().addChangeListener(this);
        Jcc.setBorder(BorderFactory.createTitledBorder("Color Picker"));
 
        add(Jcc, BorderLayout.PAGE_END);
    }
 
    public void stateChanged(ChangeEvent e)
    {
        
    	chosenColor = Jcc.getColor();
    	Jcc.setColor(chosenColor);
        System.out.println(chosenColor);
    }
 
    // Create the GUI and show it.  For thread safety,
    // this method should be invoked from the
    // event-dispatching thread.
    public void createAndShowGUI()
    {
 
        // Create and set up the window.
        JFrame frame = new JFrame("ColorChooserDemo");
 
        // set default close operation of the window.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        // Create and set up the content pane.
        JComponent newContentPane = new colorChooser();
 
        // content panes must be opaque
        newContentPane.setOpaque(true);
 
        // add content pane to the frame
        frame.setContentPane(newContentPane);
 
        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}