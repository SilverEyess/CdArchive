package CdArchive;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.net.*;
import java.io.*;

public class CdArchive extends JFrame implements ActionListener, KeyListener {

    //<editor-fold desc="Variable Declarations">

    JTextField txtSearch, txtTitle, txtAuthor, txtSection, txtX, txtY, txtBarcode, txtSortSection;
    JLabel lblSearch, lblSort, lblDisplayBinaryTree, lblHashMap, lblTitle, lblAuthor, lblSection, lblX, lblY,
            lblBarcode, lblDescription, lblSortSection, lblActionRequest, lblLog, lblMessage;
    JButton btnSearch, btnSortByTitle, btnSortByAuthor, btnSortByBarcode, btnLog, btnPreorder, btnInOrder, btnPostOrder,
            btnGraphical, btnSave, btnDisplay, btnNewItem, btnSaveUpdate, btnRetrieve, btnRemove, btnReturn, btnAddToCollection,
            btnRandomSort, btnMostlySort, btnReverseSort, btnExit;
    JCheckBox chkShowMsgLabels;
    JTable tblArchive;
    JPanel pnlTable, pnlLog, pnlInfo, pnlRobot;
    String[] columns;
    JTextArea areaLog, areaDescription;
    MyModel wordModel;
    String dataFile = "Sample_CD_Archive_Data.txt";
    ArrayList<Object[]> dataValues = new ArrayList();

    private Socket socket = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private CdArchiveThread client = null;
    private String serverName = "localhost";
    private int serverPort = 4444;


    //</editor-fold>

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

    //<editor-fold desc="Display Gui Methods">

    private void displayGui()
    {
        SpringLayout springLayout = new SpringLayout();
        setLayout(springLayout);
        displayCheckbox(springLayout);
        displayLables(springLayout);
        displayButtons(springLayout);
        displayTextBoxes(springLayout);
        addTable(springLayout);
    }

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
        btnSaveUpdate = LibraryComponents.LocateAJButton(this, this, layout, "Save/Update", 1100, 200, 110, 20);
        btnRetrieve = LibraryComponents.LocateAJButton(this, this, layout, "Retrieve", 920, 265, 130, 20);
        btnRemove = LibraryComponents.LocateAJButton(this, this, layout, "Remove", 1055, 265, 130, 20);
        btnReturn = LibraryComponents.LocateAJButton(this, this, layout, "Return", 920, 290, 130, 20);
        btnAddToCollection = LibraryComponents.LocateAJButton(this, this, layout, "Add to Collection", 1055, 290, 130, 20);
        btnRandomSort = LibraryComponents.LocateAJButton(this, this, layout, "Random Collection Sort", 1000, 350, 170, 20);
        btnMostlySort = LibraryComponents.LocateAJButton(this, this, layout, "Mostly Sorted Sort", 1000, 375, 170, 20);
        btnReverseSort = LibraryComponents.LocateAJButton(this, this, layout, "Reverse Order Sort", 1000, 400, 170, 20);
        btnExit = LibraryComponents.LocateAJButton(this, this, layout, "Exit", 900, 500, 300, 20);
    }

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

    private void displayTextBoxes(SpringLayout layout)
    {
        areaLog = LibraryComponents.LocateAJTextArea(this, layout, 10, 275, 76, 8);
        txtSearch = LibraryComponents.LocateAJTextField(this, this, layout, 10, 100, 5);
        txtTitle = LibraryComponents.LocateAJTextField(this, this, layout, 15, 990, 5);
        txtAuthor = LibraryComponents.LocateAJTextField(this, this, layout, 15, 990, 30);
        txtSection = LibraryComponents.LocateAJTextField(this, this, layout, 15, 990, 55);
        txtX = LibraryComponents.LocateAJTextField(this, this, layout, 5, 990, 80);
        txtY = LibraryComponents.LocateAJTextField(this, this, layout, 5, 990, 105);
        txtBarcode = LibraryComponents.LocateAJTextField(this, this, layout, 15, 990, 130);
        areaDescription = LibraryComponents.LocateAJTextArea(this, layout, 990, 155, 20, 2);
        txtSortSection = LibraryComponents.LocateAJTextField(this, this, layout, 10, 1000, 320);
    }

    private void displayCheckbox(SpringLayout layout)
    {
        chkShowMsgLabels = LibraryComponents.LocateAJCheckBox(this, this, layout, "Show Message Labels:", 10, 500);
    }
    //</editor-fold>

    //<editor-fold desc="Table setup">
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

    //<editor-fold desc="Sorting Algorithms">
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

    //<editor-fold desc="Binary Tree tings">
    public void PostOrderBinaryTree(ArrayList<Object[]> arr)
    {
        BinaryTree bTree = new BinaryTree();
        for (int i = 0; i < arr.size(); i++) {
            bTree.addNode(Integer.parseInt(arr.get(i)[6].toString()), arr.get(i)[1].toString());
        }
        bTree.log = "";
        areaLog.setText(bTree.postOrderTraverseTree(bTree.root));

    }

    public void PreOrderBinaryTree(ArrayList<Object[]> arr)
    {
        BinaryTree bTree = new BinaryTree();
        for (Object[] objects : arr) {
            bTree.addNode(Integer.parseInt(objects[6].toString()), objects[1].toString());
        }
        bTree.log = "";
        areaLog.setText(bTree.preorderTraverseTree(bTree.root));

    }

    //</editor-fold>

    //<editor-fold desc="Process Log">
    public void ProcessLog()
    {
        String log1 = "Process log1";
        String[] logs = {"Process log2", "Process log3", "Process log4"};

        DList dList = new DList(log1);
        for(int i = 0; i < 3; i++)
        {
            DLNode lastNode = dList.get(i);
            DLNode logNode = new DLNode(logs[i]);
            lastNode.append(logNode);
        }
        String text = dList.toString();
        areaLog.setText(text);
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

    private void send()
    {
        try
        {
            //streamOut.writeUTF(txtWord1.getText());
            streamOut.flush();
            //txtWord1.setText("");
        }
        catch (IOException ioe)
        {
            println("Sending error: " + ioe.getMessage());
            close();
        }
    }

    public void handle(String msg)
    {
        if (msg.equals(".bye"))
        {
            println("Good bye. Press EXIT button to exit ...");
            close();
        }
        else
        {
            println(msg);

            // NEW -----------------------------------

            //currentAssocWord++;
            //wordList[currentAssocWord] = new AssocData(msg);
            //for (int i = 0; i < currentAssocWord; i++)
            {
                //System.out.println("Handle Method: " + i + " - " + wordList[i].words);
            }

            //----------------------------------------

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

    //<editor-fold desc="Action and Key Listeners">
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnSortByTitle)
        {
            bubbleSort(dataValues);
        }
        if(e.getSource() == btnSortByAuthor)
        {
            SelectionSort(dataValues);
        }
        if(e.getSource() == btnSortByBarcode)
        {
            InsertionSort(dataValues);
        }
        if(e.getSource() == btnLog)
        {
            ProcessLog();
        }
        if(e.getSource() == btnPostOrder)
        {
            PostOrderBinaryTree(dataValues);
        }
        if(e.getSource() == btnPreorder)
            PreOrderBinaryTree(dataValues);

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
