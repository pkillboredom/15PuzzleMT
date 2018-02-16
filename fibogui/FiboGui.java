/* 
 * This code adopted for CSE451 by Andrew Whitaker.  Original source:
 * Sun's Swing Tutorial: 
 * http://java.sun.com/docs/books/tutorial/uiswing/components/examples/TextDemo.java
 */

import java.util.List;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class FiboGui extends JPanel implements ActionListener {
    protected JTextField textField;
    protected JTextArea textArea;
    private final static String newline = "\n";

    public FiboGui() {
        super(new BorderLayout());

        textField = new JTextField(20);
        textField.addActionListener(this);

        
        textArea = new JTextArea(5, 60);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea,
                                       JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JButton button = new JButton("Cancel");
        button.addActionListener(new ActionListener() {
         // this is invoked when the cancel button is pushed
         public void actionPerformed(ActionEvent arg0) {
            textArea.append("IMPLEMENT ME!" + newline);            
         }           
        });

        //Add Components to this panel.

	JPanel north = new JPanel(new FlowLayout());
	north.add(textField);
	north.add(button);
	
	this.add(north,BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    // this is invoked when the user enters a value at the text field
    public void actionPerformed(ActionEvent evt) {
        String text = textField.getText();
        
        try {
           Integer I = Integer.parseInt(text);
           int i = I.intValue();
           if (i < 0) {
              textArea.append("ERROR: Must give non-negative input\n");
           }
           else {
              List<Long> list = Fibonacci.fibSeries(i);
              for (Long l : list) {
                 textArea.append(l + " ");
              }
              textArea.append(newline);
           }
        }
        
        catch (NumberFormatException nfe) {
           textArea.append("ERROR: Bad input" + newline);
           
        }

        textField.selectAll();

        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Fibonacci Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new FiboGui();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
