/**
 * --------------------------------------------------------
 * Class: CdArchive
 *
 * @author Jack Lowe
 * Developed: 2020
 *
 * Purpose: To manage a collection of CDs through the use of integration with a robot across a network
 *
 *
 *
 * ----------------------------------------------------------
 */

//<editor-fold desc="Imports">
package CdArchive;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
//</editor-fold>



public class CdArchive extends JFrame implements ActionListener, KeyListener {

    //<editor-fold desc="Variable Declarations">
    JTextField txtSearch, txtTitle, txtAuthor, txtSection, txtX, txtY, txtBarcode, txtSortSection;
    JLabel lblSearch, lblSort, lblDisplayBinaryTree, lblHashMap, lblTitle, lblAuthor, lblSection, lblX, lblY,
            lblBarcode, lblDescription, lblSortSection, lblActionRequest, lblLog, lblMessage;
    JButton btnSearch, btnSortByTitle, btnSortByAuthor, btnSortByBarcode, btnLog, btnPreorder, btnInOrder, btnPostOrder,
            btnGraphical, btnSave, btnDisplay, btnNewItem, btnSaveUpdate, btnRetrieve, btnRemove, btnReturn, btnAddToCollection,
            btnRandomSort, btnMostlySort, btnReverseSort, btnExit, btnConnect, btnClear;
    JCheckBox chkShowMsgLabels;
    JTable tblArchive;
    JPanel pnlTable, pnlLog, pnlInfo, pnlRobot;
    String[] columns;
    JTextArea areaLog, areaDescription;
    JScrollPane scrlLog, scrlDescription;
    MyModel wordModel;
    String dataFile = "Sample_CD_Archive_Data.txt";
    ArrayList<Object[]> dataValues = new ArrayList();
    AutomationConsole automationConsole = new AutomationConsole();
    HashMap hashMap;
    public DList dList = new DList("CD Archive DList");

    private Socket socket = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private CdArchiveThread client = null;
    private String serverName = "localhost";
    private int serverPort = 4444;


    //</editor-fold>

    //<editor-fold desc="Main and run functions">

    public static void main(String[] args)
    {
        CdArchive cdArchiveApplication = new CdArchive();
        cdArchiveApplication.run();

    }

    public void run()
    {
        setBounds(100, 50, 1260, 570);
        setTitle("Archive Console");
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
    }
    //</editor-fold>

    //<editor-fold desc="Display Gui Methods">

    /**
     * Call all display functions to draw GUI
     */
    private void displayGui()
    {
        SpringLayout springLayout = new SpringLayout();
        setLayout(springLayout);
        displayCheckbox(springLayout);
        displayLables(springLayout);
        displayButtons(springLayout);
        displayTextBoxes(springLayout);
        addTable(springLayout);
        displayAreas(springLayout);
    }

    /**
     * Instantiate all buttons with their parameters
     * @param layout Layout manager
     */
    private void displayButtons(SpringLayout layout)
    {
        btnSearch = LibraryComponents.LocateAJButton(this, this, layout, "Search", 220, 5, 80, 20);
        btnSortByTitle = LibraryComponents.LocateAJButton(this, this, layout, "By Title", 40, 220, 90, 20);
        btnSortByAuthor = LibraryComponents.LocateAJButton(this, this, layout, "By Author", 135, 220, 90, 20);
        btnSortByBarcode = LibraryComponents.LocateAJButton(this, this, layout, "By Barcode", 605, 220, 119, 20);
        btnLog  = LibraryComponents.LocateAJButton(this, this, layout, "Process Log", 735, 250, 110, 20);
        btnPreorder = LibraryComponents.LocateAJButton(this, this, layout, "Pre-Order", 130, 450, 100, 20);
        btnInOrder = LibraryComponents.LocateAJButton(this, this, layout, "In-Order", 235, 450, 100, 20);
        btnPostOrder = LibraryComponents.LocateAJButton(this, this, layout, "Post-Order", 340, 450, 100, 20);
        btnGraphical = LibraryComponents.LocateAJButton(this, this, layout, "Graphical", 445, 450, 100, 20);
        btnSave = LibraryComponents.LocateAJButton(this, this, layout, "Save", 130, 475, 100, 20);
        btnDisplay = LibraryComponents.LocateAJButton(this, this, layout, "Display", 235, 475, 100, 20);
        btnNewItem = LibraryComponents.LocateAJButton(this, this, layout, "New Item", 900, 200, 100, 20);
        btnClear = LibraryComponents.LocateAJButton(this, this, layout, "Clear", 1005, 200, 90, 20);
        btnSaveUpdate = LibraryComponents.LocateAJButton(this, this, layout, "Save/Update", 1100, 200, 110, 20);
        btnRetrieve = LibraryComponents.LocateAJButton(this, this, layout, "Retrieve", 920, 265, 130, 20);
        btnRemove = LibraryComponents.LocateAJButton(this, this, layout, "Remove", 1055, 265, 130, 20);
        btnReturn = LibraryComponents.LocateAJButton(this, this, layout, "Return", 920, 290, 130, 20);
        btnAddToCollection = LibraryComponents.LocateAJButton(this, this, layout, "Add to Collection", 1055, 290, 130, 20);
        btnRandomSort = LibraryComponents.LocateAJButton(this, this, layout, "Random Collection Sort", 1000, 350, 170, 20);
        btnMostlySort = LibraryComponents.LocateAJButton(this, this, layout, "Mostly Sorted Sort", 1000, 375, 170, 20);
        btnReverseSort = LibraryComponents.LocateAJButton(this, this, layout, "Reverse Order Sort", 1000, 400, 170, 20);
        btnExit = LibraryComponents.LocateAJButton(this, this, layout, "Exit", 1110, 500, 100, 20);
        btnConnect = LibraryComponents.LocateAJButton(this, this, layout, "Connect",1000, 500, 100, 20);
    }

    /**
     * Instantiate all Lables with their parameters
     * @param layout Layout manager
     */
    private void displayLables(SpringLayout layout)
    {
        lblSearch = LibraryComponents.LocateAJLabel(this, layout, "Search String:", 10, 5);
        lblSort = LibraryComponents.LocateAJLabel(this, layout, "Sort:", 10, 220);
        lblLog = LibraryComponents.LocateAJLabel(this, layout, "Process Log:", 10, 250);
        lblDisplayBinaryTree = LibraryComponents.LocateAJLabel(this, layout, "Display Binary Tree:", 10, 450);
        lblHashMap = LibraryComponents.LocateAJLabel(this, layout, "HashMap / Set:", 37, 475);
        lblTitle= LibraryComponents.LocateAJLabel(this, layout, "Title:", 900, 5);
        lblAuthor= LibraryComponents.LocateAJLabel(this, layout, "Author:", 900, 30);
        lblSection= LibraryComponents.LocateAJLabel(this, layout, "Section:", 900, 55);
        lblX= LibraryComponents.LocateAJLabel(this, layout, "X:", 900, 80);
        lblY= LibraryComponents.LocateAJLabel(this, layout, "Y:", 900, 105);
        lblBarcode= LibraryComponents.LocateAJLabel(this, layout, "Barcode:", 900, 130);
        lblDescription= LibraryComponents.LocateAJLabel(this, layout, "Description:", 900, 155);
        lblActionRequest = LibraryComponents.LocateAJLabel(this, layout, "Automation Action Request for the item above:", 900, 235);
        lblSortSection = LibraryComponents.LocateAJLabel(this, layout, "Sort Section:", 920, 320);
        lblMessage = LibraryComponents.LocateAJLabel(this, layout, "", 250, 505);
    }

    /**
     * Instantiate all TextBoxes with their parameters
     * @param layout Layout manager
     */
    private void displayTextBoxes(SpringLayout layout)
    {
        txtSearch = LibraryComponents.LocateAJTextField(this, this, layout, 10, 100, 5);
        txtTitle = LibraryComponents.LocateAJTextField(this, this, layout, 15, 990, 5);
        txtAuthor = LibraryComponents.LocateAJTextField(this, this, layout, 15, 990, 30);
        txtSection = LibraryComponents.LocateAJTextField(this, this, layout, 15, 990, 55);
        txtX = LibraryComponents.LocateAJTextField(this, this, layout, 5, 990, 80);
        txtY = LibraryComponents.LocateAJTextField(this, this, layout, 5, 990, 105);
        txtBarcode = LibraryComponents.LocateAJTextField(this, this, layout, 15, 990, 130);
        txtSortSection = LibraryComponents.LocateAJTextField(this, this, layout, 10, 1000, 320);
    }

    /**
     * Instantiate CheckBox with their parameters
     * @param layout Layout manager
     */
    private void displayCheckbox(SpringLayout layout)
    {
        chkShowMsgLabels = LibraryComponents.LocateAJCheckBox(this, this, layout, "Show Message Labels:", 10, 500);
    }

    /**
     * Instantiate all TextAreas with their parameters
     * @param myLayout Layout manager
     */
    private void displayAreas(SpringLayout myLayout)
    {
        areaLog = new JTextArea(10, 80);
        scrlLog = new JScrollPane(areaLog);
        this.add(scrlLog);
        myLayout.putConstraint(SpringLayout.WEST, scrlLog, 10, SpringLayout.WEST, this);
        myLayout.putConstraint(SpringLayout.NORTH, scrlLog, 275, SpringLayout.NORTH, this);

        areaDescription = new JTextArea(2, 20);
        scrlDescription = new JScrollPane(areaDescription);
        this.add(scrlDescription);
        myLayout.putConstraint(SpringLayout.WEST, scrlDescription, 990, SpringLayout.WEST, this);
        myLayout.putConstraint(SpringLayout.NORTH, scrlDescription, 155, SpringLayout.NORTH, this);

    }

    //</editor-fold>

    //<editor-fold desc="Table setup">

    /**
     * This function is for displaying the table on the form. It reads from the datafile, reading the first line
     * for the headers of the table then all subsequent lines as data entries for the table
     * @param layout Layout Manager
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
            while ((line = br.readLine()) != null)
            {
                temp = line.split(";");
                if (temp[8].equalsIgnoreCase("no"))
                {
                    onLoan = false;
                }
                dataValues.add(new Object[] {temp[0], temp[1], temp[2], temp[3], temp[4], temp[5], temp[6], temp[7], onLoan});
            }
            br.close();
        } catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
        }
        wordModel = new MyModel(dataValues, columns);

        tblArchive = new JTable(wordModel);
        ListSelectionModel selectionModel = tblArchive.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblArchive.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                String data = getRowData();
                String[] rowData = data.split(";");
                txtTitle.setText(rowData[1]);
                txtAuthor.setText(rowData[2]);
                txtSection.setText(rowData[3]);
                txtX.setText(rowData[4]);
                txtY.setText(rowData[5]);
                txtBarcode.setText(rowData[6]);
                areaDescription.setText(rowData[7]);
                txtSortSection.setText(rowData[3]);
            }

        });

        tblArchive.isForegroundSet();
        tblArchive.setShowHorizontalLines(false);
        tblArchive.setRowSelectionAllowed(true);
        tblArchive.setColumnSelectionAllowed(true);
        tblArchive.setAutoCreateRowSorter(true);
        add(tblArchive);

        tblArchive.setSelectionForeground(Color.white);
        tblArchive.setSelectionBackground(Color.blue);
        JScrollPane scrollPane = tblArchive.createScrollPaneForTable(tblArchive);
        pnlTable.add(scrollPane, BorderLayout.CENTER);
        pnlTable.setPreferredSize(new Dimension(850,170));
        layout.putConstraint(SpringLayout.WEST, pnlTable, 10, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, pnlTable, 50, SpringLayout.NORTH, this);
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

    /**
     * This function gets the data from the selected row of the table and returns it in a String Array
     * @return returns string array with data from selected table row
     */
    public String getRowData()
    {
        int columnCount = 9;
        String[] temp = new String[columnCount];
        String temp2 = "";
        if (!tblArchive.getSelectionModel().isSelectionEmpty())
        {
            int selectedRow = tblArchive.getSelectedRow();
            for (int i = 0; i < columnCount; i++)
            {
                temp2 += tblArchive.getValueAt(selectedRow, i).toString() + ";";
                temp[i] = tblArchive.getValueAt(selectedRow, i).toString();
            }
        }
        return temp2;
    }
    //</editor-fold>

    //<editor-fold desc="Sorting Algorithms">

    /**
     * Bubble sort algorithm
     * @param arr Data array, same array used to populate table data
     */
    public void bubbleSort(ArrayList<Object[]> arr)
    {

        for(int j=0; j<arr.size(); j++)
        {
            for(int i=j+1; i<arr.size(); i++)
            {
                if((arr.get(i)[1]).toString().compareToIgnoreCase(arr.get(j)[1].toString())<0)
                {
                    Object[] words = arr.get(j);
                    arr.set(j, arr.get(i));
                    arr.set(i, words);
                }
            }
        }
        dataValues = arr;
        wordModel.fireTableDataChanged();
    }

    /**
     * Selection sort algorithm
     * @param arr Data array, same array used to populate table data
     */
    public void SelectionSort(ArrayList<Object[]> arr)
    {
        int first;
        Object[] temp;
        for(int i = arr.size() - 1; i > 0; i--)
        {
            first = 0;
            for(int j= 1; j<=i; j++)
            {
                if(arr.get(j)[2].toString().compareToIgnoreCase(arr.get(first)[2].toString())<0)
                {
                    first = j;
                }
                temp = arr.get(first);
                arr.set(first, arr.get(i));
                arr.set(i, temp);
            }
        }
        wordModel.fireTableDataChanged();
    }

    /**
     * Insertion sort algorithm
     * @param arr Data array, same array used to populate table data
     */
    public void InsertionSort(ArrayList<Object[]> arr)
    {
        Object[] key;
        for(int j = 1; j <arr.size(); j++)
        {
            int i;
            key = arr.get(j);
            for(i = j-1; (i>=0) && (arr.get(i)[6].toString().compareToIgnoreCase(key[6].toString())<0); i--)
            {
                arr.set(i + 1, arr.get(i));
            }
            arr.set(i + 1, key);
        }
        wordModel.fireTableDataChanged();
    }

    //</editor-fold>

    //<editor-fold desc="Binary Tree">

    /**
     * This function creates a new Binary tree consisting of the barcodes and titles of each entry of data
     * and traverses it in PostOrder, then displays that data in the Process Log
     * @param arr The data array for the table
     */
    public void PostOrderBinaryTree(ArrayList<Object[]> arr)
    {
        BinaryTree bTree = new BinaryTree();
        for (Object[] objects : arr) {
            bTree.addNode(Integer.parseInt(objects[6].toString()), objects[1].toString());
        }
        bTree.log = "";
        areaLog.setText(bTree.postOrderTraverseTree(bTree.root));

    }
    /**
     * This function creates a new Binary tree consisting of the barcodes and titles of each entry of data
     * and traverses it in PreOrder, then displays that data in the Process Log
     * @param arr The data array for the table
     */
    public void PreOrderBinaryTree(ArrayList<Object[]> arr)
    {
        BinaryTree bTree = new BinaryTree();
        for (Object[] objects : arr) {
            bTree.addNode(Integer.parseInt(objects[6].toString()), objects[1].toString());
        }
        bTree.log = "";
        areaLog.setText(bTree.preorderTraverseTree(bTree.root));

    }
    /**
     * This function creates a new Binary tree consisting of the barcodes and titles of each entry of data
     * and traverses it InOrder, then displays that data in the Process Log
     * @param arr The data array for the table
     */
    public void InOrderBinaryTree(ArrayList<Object[]> arr)
    {
        BinaryTree bTree = new BinaryTree();
        for (Object[] objects : arr) {
            bTree.addNode(Integer.parseInt(objects[6].toString()), objects[1].toString());
        }
        bTree.log = "";
        areaLog.setText(bTree.inOrderTraverseTree(bTree.root));
    }

    //</editor-fold>

    //<editor-fold desc="Process Log">

    /**
     * This function is for Retrieving a CD. It calls the getRowData function to grab the data of the selected row
     * of the table, then splits it to grab the barcode to add to the DList. It takes the full row data and sends
     * a message with a code to the Automation console to handle and process the request
     */
    public void RetrieveRecord()
    {
        String data = getRowData();
        String[] rowData = getRowData().split(";");
        DLNode lastNode = dList.get(0);
        DLNode newNode = new DLNode("SENT - Retrieving item: " + rowData[6]);
        lastNode.append(newNode);
        send("RTV;" + data);
        String log = dList.toString();
        areaLog.setText(log);
    }
    /**
     * This function is for Removing a CD. It calls the getRowData function to grab the data of the selected row
     * of the table, then splits it to grab the barcode to add to the DList. It takes the full row data and sends
     * a message with a code to the Automation console to handle and process the request
     */

    public void RemoveRecord()
    {
        String data = getRowData();
        String[] rowData = getRowData().split(";");
        DLNode lastNode = dList.get(0);
        DLNode newNode = new DLNode("SENT - Removing item: " + rowData[6]);
        lastNode.append(newNode);
        send("REM;" + data);
        String log = dList.toString();
        areaLog.setText(log);
    }

    /**
     * This function is for Returning a CD. It calls the getRowData function to grab the data of the selected row
     * of the table, then splits it to grab the barcode to add to the DList. It takes the full row data and sends
     * a message with a code to the Automation console to handle and process the request
     */
    public void ReturnRecord()
    {
        String data = getRowData();
        String[] rowData = getRowData().split(";");
        DLNode lastNode = dList.get(0);
        DLNode newNode = new DLNode("SENT - Returning item: " + rowData[6]);
        lastNode.append(newNode);
        send("RET;" + data);
        String log = dList.toString();
        areaLog.setText(log);
    }

    /**
     * This function is for Adding a CD to collection. It calls the getRowData function to grab the data of the selected row
     * of the table, then splits it to grab the barcode to add to the DList. It takes the full row data and sends
     * a message with a code to the Automation console to handle and process the request
     */
    public void AddToCollection()
    {
        String data = getRowData();
        String[] rowData = getRowData().split(";");
        DLNode lastNode = dList.get(0);
        DLNode newNode = new DLNode("SENT - Item adding to collection: " + rowData[6]);
        lastNode.append(newNode);
        send("ADD;" + data);
        String log = dList.toString();
        areaLog.setText(log);
    }

    /**
     * This method simply prints the DList to the process log, clearing anything that was in there beforehand.
     */
    public void ProcessLog()
    {
        String log = dList.toString();
        areaLog.setText(log);
    }

    /**
     * This method takes the section specified in the txtSortSection and bundles all data entries for that section
     * and sends it to the automation console to handle the sort.
     */
    public void RandomOrderSort()
    {
        if(txtSortSection.getText().equalsIgnoreCase(""))
            areaLog.setText("Enter section to sort!");
        else
        {
            DLNode lastNode = dList.get(0);
            DLNode newNode = new DLNode("SENT - Random Collection Sort: " + txtSortSection.getText());
            lastNode.append(newNode);
            send("RND;" + txtSortSection.getText());
            String log = dList.toString();
            areaLog.setText(log);
        }
    }

    /**
     * This method takes the section specified in the txtSortSection and bundles all data entries for that section
     * and sends it to the automation console to handle the sort.
     */
    public void MostlySortedSort()
    {
        if(txtSortSection.getText().equalsIgnoreCase(""))
            areaLog.setText("Enter section to sort!");
        else
        {
            DLNode lastNode = dList.get(0);
            DLNode newNode = new DLNode("SENT - Mostly Sorted Sort: " + txtSortSection.getText());
            lastNode.append(newNode);
            send("MST;" + txtSortSection.getText());
            String log = dList.toString();
            areaLog.setText(log);
        }
    }

    /**
     * This method takes the section specified in the txtSortSection and bundles all data entries for that section
     * and sends it to the automation console to handle the sort.
     */
    public void ReverseSort()
    {
        if(txtSortSection.getText().equalsIgnoreCase(""))
            areaLog.setText("Enter section to sort!");
        else
        {
            DLNode lastNode = dList.get(0);
            DLNode newNode = new DLNode("SENT - Reverse Order Sort: " + txtSortSection.getText());
            lastNode.append(newNode);
            send("RVS;" + txtSortSection.getText());
            String log = dList.toString();
            areaLog.setText(log);
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
     * This method is used to send messages to the Automation console
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
        String current[] = msg.split(";");
        if (current[0].equalsIgnoreCase("remove")) {
            DLNode lastNode = dList.get(0);
            DLNode newNode = new DLNode("RCVD - Removed item: " + current[1]);
            lastNode.append(newNode);
            areaLog.setText(dList.toString());
        }
        else if (current[0].equalsIgnoreCase("retrieve")) {
            DLNode lastNode = dList.get(0);
            DLNode newNode = new DLNode("RCVD - Retrieved item: " + current[1]);
            lastNode.append(newNode);
            areaLog.setText(dList.toString());
        }
        else if (current[0].equalsIgnoreCase("return")) {
            DLNode lastNode = dList.get(0);
            DLNode newNode = new DLNode("RCVD - Returned item: " + current[1]);
            lastNode.append(newNode);
            areaLog.setText(dList.toString());
        }
        else if (current[0].equalsIgnoreCase("add to collection")) {
            DLNode lastNode = dList.get(0);
            DLNode newNode = new DLNode("RCVD - Item added to collection: " + current[1]);
            lastNode.append(newNode);
            areaLog.setText(dList.toString());
        }
        else if (current[0].equalsIgnoreCase("random collection sort")) {
            DLNode lastNode = dList.get(0);
            DLNode newNode = new DLNode("RCVD - Random Collection Sort: " + current[2]);
            lastNode.append(newNode);
            areaLog.setText(dList.toString());
        }
        else if (current[0].equalsIgnoreCase("mostly sorted sort")) {
            DLNode lastNode = dList.get(0);
            DLNode newNode = new DLNode("RCVD - Mostly Sorted Sort: " + current[2]);
            lastNode.append(newNode);
            areaLog.setText(dList.toString());
        }
        else if (current[0].equalsIgnoreCase("reverse order sort")) {
            DLNode lastNode = dList.get(0);
            DLNode newNode = new DLNode("RCVD - Reverse Order Sort: " + current[2]);
            lastNode.append(newNode);
            areaLog.setText(dList.toString());
        }
    }

    public void open()
    {
        try
        {
            streamOut = new DataOutputStream(socket.getOutputStream());
            client = new CdArchiveThread(this, socket);
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
        client.close();
        client.stop();
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

    //<editor-fold desc="Data Manipulation">

    /**
     * This method simply reads from the textboxes for creating a new entry, then appends that entry to the dataset for
     * the table and updates the table
     */
    public void AddNewEntry()
    {
        String newEntry = "";
        newEntry += txtTitle.getText() + ";";
        newEntry += txtAuthor.getText() + ";";
        newEntry += txtSection.getText() + ";";
        newEntry += txtX.getText() + ";";
        newEntry += txtY.getText() + ";";
        newEntry += txtBarcode.getText() + ";";
        newEntry += areaDescription.getText() + ";";

        String[] temp = newEntry.split(";");

        dataValues.add(new Object[] {"99", temp[0], temp[1], temp[2], temp[3], temp[4], temp[5], temp[6], false});
        wordModel.fireTableDataChanged();
    }

    /**
     * This method uses a buffered reader to first reads the table headers and writes those to a temporary string with
     * ";" separating each header.
     * Then it iterates over the data set for the table and writes those to the same string, separating each field with
     * ";"s as well. It then Writes the string to a datafile and closes the buffered reader.
     */
    public void SaveUpdate()
    {
        try {
            String data = "";
            BufferedWriter outFile = new BufferedWriter(new FileWriter("Sample_CD_Archive_Data_new.txt"));
            for(int h = 0; h < tblArchive.getColumnCount() ; h++)
            {
                System.out.println(data);
                data += tblArchive.getColumnName(h) + ";";
            }
            StringUtils.removeEnd(data, ";");
            data += "\n";
            for (int i = 0; i < dataValues.size(); i++) {
                Object[] line = dataValues.get(i);
                //String data = "";
                for(int d = 0; d < line.length; d++)
                {
                    data += line[d].toString() + ";";
                }
                data += "\n";

                System.out.println(data);
            }
            outFile.write(data);
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method simply clears the input text boxes for creating a new entry
     */
    public void ClearEntry()
    {
        txtTitle.setText("");
        txtAuthor.setText("");
        txtSection.setText("");
        txtX.setText("");
        txtY.setText("");
        txtBarcode.setText("");
        areaDescription.setText("");
    }
    //</editor-fold>

    //<editor-fold desc="Hash Map Functions">

    /**
     * This method creates a new Hashmap then iterates over the data set, creating a new entry for each set consisting
     * of the barcode as the key and title as the value
     */
    public void HashMap()
    {
        hashMap = new HashMap<String, String>();

        for (Object[] dataValue : dataValues) {
            hashMap.put(dataValue[7].toString(), dataValue[1].toString() + "\n");
        }
    }

    /**
     * This method iterates over the hashmap and appends each entries data to the process log text area.
     */
    public void DisplayHash()
    {
        Iterator keySetIterator = hashMap.keySet().iterator();
        areaLog.setText(null);
        while (keySetIterator.hasNext())
        {
            String key = keySetIterator.next().toString();
            areaLog.append("Key: " + key + "   ---   Value: " + hashMap.get(key).toString());
        }
    }
    //</editor-fold>

    //<editor-fold desc="Search">

    /**
     * This function takes the input of the search text field and iterates over the dataset checking if the search
     * text is in any of the fields, if it is then that row is selected and the data is shown in the entry text boxes.
     */
    public void search()
    {
        for(int i =0; i < dataValues.size(); i++)
        {
            for(int v = 0; v < dataValues.get(i).length; v++)
            {
                Object[] line = dataValues.get(i);
                if(StringUtils.isEmpty(txtSearch.getText()))
                {
                    JOptionPane.showMessageDialog(this, "Enter a search string!");
                    return;
                }
                else if(StringUtils.contains(line[v].toString().toLowerCase(), txtSearch.getText().toLowerCase()))
                //else if(line[v].toString().toLowerCase().startsWith(txtSearch.getText().toLowerCase()))
                {
                    tblArchive.setRowSelectionInterval(0, i);
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Action and Key Listeners">
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSortByTitle)
            bubbleSort(dataValues);
        if (e.getSource() == btnSortByAuthor)
            SelectionSort(dataValues);
        if (e.getSource() == btnSortByBarcode)
            InsertionSort(dataValues);
        if (e.getSource() == btnLog)
            ProcessLog();
        if (e.getSource() == btnPostOrder)
            PostOrderBinaryTree(dataValues);
        if(e.getSource() == btnInOrder)
            InOrderBinaryTree(dataValues);
        if (e.getSource() == btnPreorder)
            PreOrderBinaryTree(dataValues);
        if (e.getSource() == btnConnect) {
            connect(serverName, serverPort);
            automationConsole.run();
        }
        if (e.getSource() == btnRetrieve)
            RetrieveRecord();
        if (e.getSource() == btnRemove)
            RemoveRecord();
        if(e.getSource() == btnNewItem)
            AddNewEntry();
        if(e.getSource() == btnSaveUpdate)
            SaveUpdate();
        if(e.getSource() == btnReturn)
            ReturnRecord();
        if(e.getSource() == btnAddToCollection)
            AddToCollection();
        if(e.getSource() == btnExit)
            System.exit(0);
        if(e.getSource() == btnSave)
            HashMap();
        if(e.getSource() == btnDisplay)
            DisplayHash();
        if(e.getSource() == btnRandomSort)
            RandomOrderSort();
        if(e.getSource() == btnMostlySort)
            MostlySortedSort();
        if(e.getSource() == btnReverseSort)
            ReverseSort();
        if(e.getSource() == btnSearch)
            search();
        if(e.getSource() == btnClear)
            ClearEntry();
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
