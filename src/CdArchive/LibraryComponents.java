/**
 * --------------------------------------------------------
 * Class: LibraryComponents
 *
 * @author Mark O'Reilly
 * Developed: 2016-2017
 *
 * Purpose: To contain a library of utility methods that can be accessed from other Java applications
 *
 * Currently:
 *  - LocateAJLabel - for positioning a JLabel using the layout manager: SpringLayout
 *  - LocateAJTextField - for positioning a JTextField using SpringLayout
 *  - LocateAJButton - for positioning a JButton using SpringLayout
 *  - LocateAJTextArea - for positioning a JTextArea using SpringLayout
 *
 * ----------------------------------------------------------
 */


package CdArchive;


import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;


public class LibraryComponents
{

    /** --------------------------------------------------------
     * Purpose: Locate a single JLabel within a JFrame.
     * param   JFrame, Layout_manager, JLabel_Caption, Width, X_position, Y_Position
     * returns The JLabel.
     * ----------------------------------------------------------
     */
    public static JLabel LocateAJLabel(JFrame myJFrame, SpringLayout myJLabelLayout, String JLabelCaption, int x, int y)
    {
        // Declare and Instantiate the JLabel
        JLabel myJLabel = new JLabel(JLabelCaption);
        // Add the JLabel to the screen
        myJFrame.add(myJLabel);
        // Set the position of the JLabel (From left hand side of the JFrame (West), and from top of JFrame (North))
        myJLabelLayout.putConstraint(SpringLayout.WEST, myJLabel, x, SpringLayout.WEST, myJFrame);
        myJLabelLayout.putConstraint(SpringLayout.NORTH, myJLabel, y, SpringLayout.NORTH, myJFrame);
        // Return the label to the calling method
        return myJLabel;
    }


    /** --------------------------------------------------------
     * Purpose: Locate a single JTextField within a JFrame.
     * param   JFrame, KeyListener, Layout_manager, Width, X_position, Y_Position
     * returns The JTextField.
     * ----------------------------------------------------------
     */
    public static JTextField LocateAJTextField(JFrame myJFrame, KeyListener myKeyLstnr, SpringLayout myJTextFieldLayout, int width, int x, int y)
    {
        JTextField myJTextField = new JTextField(width);
        myJFrame.add(myJTextField);
        myJTextField.addKeyListener(myKeyLstnr);
        myJTextFieldLayout.putConstraint(SpringLayout.WEST, myJTextField, x, SpringLayout.WEST, myJFrame);
        myJTextFieldLayout.putConstraint(SpringLayout.NORTH, myJTextField, y, SpringLayout.NORTH, myJFrame);
        return myJTextField;
    }


    /** --------------------------------------------------------
     * Purpose: Locate a single JButton within a JFrame.
     * param   JFrame, ActionListener, Layout_manager, JButton_name, JButton_caption, X_position, Y_Position, Width, Height
     * returns The JButton.
     * ----------------------------------------------------------
     */
    public static JButton LocateAJButton(JFrame myJFrame, ActionListener myActnLstnr, SpringLayout myJButtonLayout, String  JButtonCaption, int x, int y, int w, int h) {
        JButton myJButton = new JButton(JButtonCaption);
        myJFrame.add(myJButton);
        myJButton.addActionListener(myActnLstnr);
        myJButtonLayout.putConstraint(SpringLayout.WEST, myJButton, x, SpringLayout.WEST, myJFrame);
        myJButtonLayout.putConstraint(SpringLayout.NORTH, myJButton, y, SpringLayout.NORTH, myJFrame);
        myJButton.setPreferredSize(new Dimension(w, h));
        return myJButton;
    }

    public static JTable LocateAJTable(JFrame myJFrame, String[] columns, ArrayList<Object[]> data, SpringLayout myJTableLayout, int x, int y, int w, int h)
    {

        JTable myJTable = new JTable();
        myJFrame.add(myJTable);
        myJTableLayout.putConstraint(SpringLayout.WEST, myJTable, x, SpringLayout.WEST, myJFrame);
        myJTableLayout.putConstraint(SpringLayout.NORTH, myJTable, y, SpringLayout.NORTH, myJFrame);
        myJTable.setPreferredSize(new Dimension(w, h));
        return myJTable;
    }


    /** --------------------------------------------------------
     * Purpose: Locate a single JTextArea within a JFrame.
     * param   JFrame, Layout_manager, X_position, Y_Position, Width, Height
     * returns The JTextArea.
     * ----------------------------------------------------------
     */
    public static JTextArea LocateAJTextArea(JFrame myJFrame, SpringLayout myLayout, int x, int y, int w, int h)
    {
        JTextArea myJTextArea = new JTextArea(h,w);
        myJFrame.add(myJTextArea);
        myLayout.putConstraint(SpringLayout.WEST, myJTextArea, x, SpringLayout.WEST, myJFrame);
        myLayout.putConstraint(SpringLayout.NORTH, myJTextArea, y, SpringLayout.NORTH, myJFrame);
        return myJTextArea;
    }

    public static JCheckBox LocateAJCheckBox(JFrame myJFrame, ActionListener myActnLstnr, SpringLayout myLayout, String text, int x, int y)
    {
        JCheckBox myJCheckBox = new JCheckBox(text);
        myJFrame.add(myJCheckBox);
        myLayout.putConstraint(SpringLayout.WEST, myJCheckBox, x, SpringLayout.WEST, myJFrame);
        myLayout.putConstraint(SpringLayout.NORTH, myJCheckBox, y, SpringLayout.NORTH, myJFrame);
        myJCheckBox.addActionListener(myActnLstnr);
        return myJCheckBox;

    }

    class MyModel extends AbstractTableModel
    {
        ArrayList<Object[]> al;

        // the headers
        String[] header;

        // to hold the column index for the Sent column
        int col;

        // constructor
        MyModel(ArrayList<Object[]> obj, String[] header)
        {
            // save the header
            this.header = header;
            // and the data
            al = obj;
            // get the column index for the Sent column
            col = this.findColumn("Sent");
        }

        // method that needs to be overload. The row count is the size of the ArrayList

        public int getRowCount()
        {
            return al.size();
        }

        // method that needs to be overload. The column count is the size of our header
        public int getColumnCount()
        {
            return header.length;
        }

        // method that needs to be overload. The object is in the arrayList at rowIndex
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            return al.get(rowIndex)[columnIndex];
        }

        // a method to return the column name
        public String getColumnName(int index)
        {
            return header[index];
        }

        public Class getColumnClass(int columnIndex)
        {
            if (columnIndex == col)
            {
                return Boolean.class; // For every cell in column 7, set its class to Boolean.class
            }
            return super.getColumnClass(columnIndex); // Otherwise, set it to the default class
        }

        // a method to add a new line to the table
        void add(String word1, String word2, boolean sent)
        {
            // make it an array[3] as this is the way it is stored in the ArrayList
            // (not best design but we want simplicity)
            Object[] item = new Object[3];
            item[0] = word1;
            item[1] = word2;
            item[2] = sent;
            al.add(item);
            // inform the GUI that I have change
            fireTableDataChanged();
        }
    }


}
