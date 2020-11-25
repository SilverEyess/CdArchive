/**
 * --------------------------------------------------------
 * Class: AutomationConsole
 *
 * @author Jack Lowe
 * Developed: 2020
 *
 * Purpose: For the Automation of a robot to manage a collection of CDs
 *
 *
 *
 * ----------------------------------------------------------
 */

package CdArchive;

//<editor-fold desc="Imports">
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.net.*;
import java.io.*;
//</editor-fold>

public class AutomationConsole extends JFrame implements ActionListener, KeyListener {

    //<editor-fold desc="Variable Declaration">
    JLabel lblRequestedAction, lblBarCode, lblSection, lblArchiveCDs, lblMessage;
    JButton btnProcess, btnExit, btnAdd, btnConnect;
    JTextField txtBarCode, txtSection;
    JComboBox cmbRequestedAction;
    String[] dummyData = {"Retrieve", "Remove", "Return", "Add to Collection", "Random Collection Sort", "Mostly Sorted Sort", "Reverse Order Sort"};
    JPanel pnlTable;
    JTable tblAutomation;
    String[] columns;
    String dataFile = "Sample_CD_Archive_Data.txt";
    ArrayList<Object[]> dataValues = new ArrayList();
    MyModel wordModel;
    String current = "";

    private Socket socket = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private AutomationConsoleThread client2 = null;
    private String serverName = "localhost";
    private int serverPort = 4444;

    //</editor-fold>

    //<editor-fold desc="Main and run functions">
    public static void main(String[] args)
    {
        AutomationConsole automationConsole = new AutomationConsole();
        automationConsole.run();

    }

    public void run()
    {
        setBounds(100, 50, 900, 400);
        setTitle("Automation Console");
        setBackground(Color.blue);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        displayGui();
        setVisible(true);
        setResizable(false);
        connect(serverName, serverPort);
    }
    //</editor-fold>

    //<editor-fold desc="Display GUI Methods">
    public void displayGui()
    {
        SpringLayout springLayout = new SpringLayout();
        setLayout(springLayout);
        displayButtons(springLayout);
        displayLables(springLayout);
        displayTextBoxes(springLayout);
        addTable(springLayout);
    }

    private void displayButtons(SpringLayout layout)
    {
        btnProcess = LibraryComponents.LocateAJButton(this, this, layout, "Process", 610, 30, 100, 20);
        btnAdd = LibraryComponents.LocateAJButton(this, this, layout, "Add item", 610, 55, 100, 20);
        btnExit = LibraryComponents.LocateAJButton(this, this, layout, "Exit", 690, 320, 80, 20);
        btnConnect = LibraryComponents.LocateAJButton(this, this, layout, "Connect", 600, 320, 80, 20);
    }

    private void displayLables(SpringLayout layout)
    {
        lblRequestedAction = LibraryComponents.LocateAJLabel(this, layout, "Current Requested Action:", 50, 30);
        lblBarCode = LibraryComponents.LocateAJLabel(this, layout, "Bar Code of Selected Item:", 50, 55);
        lblSection = LibraryComponents.LocateAJLabel(this, layout, "Section:", 400, 55);
        lblArchiveCDs = LibraryComponents.LocateAJLabel(this, layout, "Archive CDs", 380, 90);
        lblMessage = LibraryComponents.LocateAJLabel(this, layout, "", 50, 330);
    }
    private void displayTextBoxes(SpringLayout layout)
    {
        txtBarCode = LibraryComponents.LocateAJTextField(this, this, layout, 15, 210, 55);
        txtSection = LibraryComponents.LocateAJTextField(this, this, layout, 5, 460, 55);
        cmbRequestedAction = LibraryComponents.LocateAJComboBox(this, this, layout, dummyData, 210, 28, 150, 20);
    }

    private void updateTextBoxValues(String[] data)
    {
        txtBarCode.setText(data[7]);
        txtSection.setText(data[4]);
    }

    public void updateTable(String[] inputData)
    {

        boolean onLoan = true;
        if (inputData[9].equalsIgnoreCase("false"))
        {
            onLoan = false;
        }
        dataValues.add(new Object[] {inputData[1], inputData[2], inputData[3], inputData[4], inputData[5], inputData[6], inputData[7], inputData[8], onLoan});
        wordModel.fireTableDataChanged();
    }

    //</editor-fold>

    //<editor-fold desc="Table Setup">

    /**
     * This method adds a table to the form. The data for the table is taken from the incoming messages sent from the
     * CdArchive
     * @param layout
     */
    private void addTable(SpringLayout layout)
    {
        pnlTable = new JPanel();
        pnlTable.setLayout(new BorderLayout());
        add(pnlTable);

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            String[] temp;
            String line;
            boolean onLoan = true;
            for (int i = 0; i <1; i++)
            {
                columns = br.readLine().split(";");
                //columns = Arrays.copyOfRange(temp, 1, temp.length-1);
            }

            br.close();
        } catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
        }
        boolean onLoan = true;

        wordModel = new MyModel(dataValues, columns);

        tblAutomation = new JTable(wordModel);

        tblAutomation.isForegroundSet();
        tblAutomation.setShowHorizontalLines(false);
        tblAutomation.setRowSelectionAllowed(true);
        tblAutomation.setColumnSelectionAllowed(true);
        tblAutomation.setAutoCreateRowSorter(true);
        add(tblAutomation);

        tblAutomation.setSelectionForeground(Color.white);
        tblAutomation.setSelectionBackground(Color.blue);
        JScrollPane scrollPane = tblAutomation.createScrollPaneForTable(tblAutomation);
        pnlTable.add(scrollPane, BorderLayout.CENTER);
        pnlTable.setPreferredSize(new Dimension(850,170));
        layout.putConstraint(SpringLayout.WEST, pnlTable, 10, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, pnlTable, 105, SpringLayout.NORTH, this);
    }

    /**
     * Table Model
     */
    static class MyModel extends AbstractTableModel
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
            col = this.findColumn("OnLoan");
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
        void add(String title, String author, String section, String x, String y, Integer barcode, String description, boolean onLoan)
        {
            // make it an array[3] as this is the way it is stored in the ArrayList
            // (not best design but we want simplicity)
            Object[] item = new Object[8];
            item[0] = title;
            item[1] = author;
            item[2] = section;
            item[3] = x;
            item[4] = y;
            item[5] = barcode;
            item[6] = description;
            item[7] = onLoan;
            al.add(item);
            // inform the GUI that I have change
            fireTableDataChanged();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Network">
    public void connect(String serverName, int serverPort)
    {
        println("Establishing connection. Please wait ...");
        try
        {
            socket = new Socket(serverName, serverPort);
            println("Connected: " + socket);
            open();
        }
        catch (UnknownHostException uhe)
        {
            println("Host unknown: " + uhe.getMessage());
        }
        catch (IOException ioe)
        {
            println("Unexpected exception: " + ioe.getMessage());
        }
    }

    /**
     * This method is used to send messages to the CdArchive
     * @param msg message to send
     */
    private void send(String msg)
    {
        try
        {
            streamOut.writeUTF(msg);
            streamOut.flush();
            //txtWord1.setText("");
        }
        catch (IOException ioe)
        {
            println("Sending error: " + ioe.getMessage());
            close();
        }
    }

    /**
     * this method is used to handle any incoming messages. Incoming messages come in sections separated by ";"s,
     * it then splits that and reads the first section which is a code, to know what to do with the rest of the
     * incoming data
     * @param msg Incoming message
     */
    public void handle(String msg)
    {
        current = msg;
        if (msg.startsWith("RTV"))
        {
            String[] rowData = msg.split(";");
            dataValues.clear();
            updateTable(rowData);
            updateTextBoxValues(rowData);
            cmbRequestedAction.setSelectedIndex(0);

        }
        else if (msg.startsWith("REM"))
        {
            String[] rowData = msg.split(";");
            dataValues.clear();
            updateTable(rowData);
            updateTextBoxValues(rowData);
            cmbRequestedAction.setSelectedIndex(1);

        }
        else if (msg.startsWith("RET"))
        {
            String[] rowData = msg.split(";");
            dataValues.clear();
            updateTable(rowData);
            updateTextBoxValues(rowData);
            cmbRequestedAction.setSelectedIndex(2);
        }
        else if (msg.startsWith("RND"))
        {
            String[] data = msg.split(";");
            cmbRequestedAction.setSelectedIndex(4);
            txtSection.setText(data[1]);
        }
        else if (msg.startsWith("MST"))
        {
            String[] data = msg.split(";");
            cmbRequestedAction.setSelectedIndex(5);
            txtSection.setText(data[1]);
        }
        else if (msg.startsWith("RVS"))
        {
            String[] data = msg.split(";");
            cmbRequestedAction.setSelectedIndex(6);
            txtSection.setText(data[1]);
        }
        else
        {
            System.out.println("Handle: " + msg);
            println(msg);
        }
    }

    public void open()
    {
        try
        {
            streamOut = new DataOutputStream(socket.getOutputStream());
            client2 = new AutomationConsoleThread(this, socket);
        }
        catch (IOException ioe)
        {
            println("Error opening output stream: " + ioe);
        }
    }

    public void close()
    {
        try
        {
            if (streamOut != null)
            {
                streamOut.close();
            }
            if (socket != null)
            {
                socket.close();
            }
        }
        catch (IOException ioe)
        {
            println("Error closing ...");
        }
        client2.close();
        client2.stop();
    }

    void println(String msg)
    {
        //display.appendText(msg + "\n");
        lblMessage.setText(msg);
    }

    public void getParameters()
    {
//        serverName = getParameter("host");
//        serverPort = Integer.parseInt(getParameter("port"));

        serverName = "localhost";
        serverPort = 4444;
    }
    //</editor-fold>

    //<editor-fold desc="Helper Methods">
    /**
     * This method is for processing the request from CdArchive. It takes the current combo box selection, the text from
     * the barcode text field and the text from the section text field and bundles them together with ";" separating each
     * part then sends that message back to the CdArchive
     */
    public void process()
    {
        String msg = cmbRequestedAction.getSelectedItem() + ";" + txtBarCode.getText() + ";" + txtSection.getText();
        send(msg);
    }
    //</editor-fold>

    //<editor-fold desc="Action and Key Listeners">
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnConnect)
            connect(serverName, serverPort);
        if(e.getSource() == btnProcess)
            process();
        if(e.getSource() == btnExit)
            System.exit(0);

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    //</editor-fold>
}
