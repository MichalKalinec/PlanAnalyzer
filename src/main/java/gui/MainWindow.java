package gui;

import backend_core.OperationsMap;
import backend_core.OperationManager;
import backend_core.PlanChecker;
import secondary.*;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.slf4j.LoggerFactory;
import secondary.TableColumnManager;
import secondary.WideComboBox;

/**
 * Main UI class
 *
 * @author Michal Kalinec 444505
 */
public class MainWindow extends javax.swing.JFrame {

    private UniversalCellRenderer universalCellRenderer = new UniversalCellRenderer();
    private CurrentPlanTableModel opsTableModel = new CurrentPlanTableModel();
    private OverviewMatrixTableModel matrixTableModel = new OverviewMatrixTableModel();
    private OperationManager operationManager = new OperationManager();
    private static TableColumnManager tcm;
    private final Date MIN = java.sql.Date.valueOf(LocalDate.of(1900, 01, 01));
    private final Date MAX = java.sql.Date.valueOf(LocalDate.of(2200, 01, 01));
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MainWindow.class);

    /**
     * Creates new form MainWindow
     *
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     */
    public MainWindow() throws SQLException, IOException {

        initComponents();
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/logo.png")));
        setTitle("Sledovanie plánu");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        tcm = new TableColumnManager(opsTable);

        //Estabilish connection to DB.
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
        } catch (IOException ex) {
            String msg = "Chyba pri načítavaní config súboru.";
            LOG.error(msg, ex);
            JOptionPane.showMessageDialog(null, msg, "Chyba", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        try {
            PlanChecker.startBackend(prop.getProperty("URL"));
        } catch (SQLException ex) {
            logAndNotify(ex);
        }

        //Initial filling of combobox.
        loadingLabel.setVisible(true);
        new SwingWorker<OperationsMap, Void>() {
            @Override
            protected OperationsMap doInBackground() throws SQLException {
                return OperationsMap.showAllOps();
            }

            @Override
            protected void done() {
                try {
                    ordersComboBox.setModel(new SortedComboBoxModel<>(OrdersComboBoxItem.createAsList(this.get().getOperations().keySet())));
                    ordersComboBox.setSelectedIndex(-1);
                } catch (InterruptedException | ExecutionException ex) {
                    logAndNotify(ex);
                } finally {
                    loadingLabel.setVisible(false);
                }
            }
        }.execute();

        //MouseListener for searching with order after doubleclicking the first column.
        opsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                Point point = mouseEvent.getPoint();
                int column = opsTable.columnAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && column == 0 && opsTable.getModel() instanceof CurrentPlanTableModel) {
                    getOrderSeachSwingWorker(opsTableModel.getOpForRow(opsTable.rowAtPoint(point)).getOrderNo(), mouseEvent).execute();
                }
            }
        });

        ordersComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED || ordersComboBox.getSelectedItem() == null) {
                    return;
                }
                getOrderSeachSwingWorker(((OrdersComboBoxItem) ordersComboBox.getSelectedItem()).getOrderNo(), e).execute();
                ordersComboBox.setSelectedIndex(-1);
            }
        });

        workcenList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (super.isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                } else {
                    super.addSelectionInterval(index0, index1);
                }
            }
        });

        fromDatePicker.setDate(Date.from(LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        toDatePicker.setDate(Date.from(LocalDate.now().minusDays(0).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        delayInfoPanel = new javax.swing.JPanel();
        noteCategoryLabel = new javax.swing.JLabel();
        noteCategoryComboBox = new javax.swing.JComboBox<>();
        noteLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        noteTextArea = new javax.swing.JTextArea();
        noteInfoScrollPane = new javax.swing.JScrollPane();
        showNoteInfoTextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        opsTable = new javax.swing.JTable(){

            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(this.getColumnModel()) {

                    @Override
                    public String getToolTipText(MouseEvent e) {
                        if(!(opsTable.getModel() instanceof OverviewMatrixTableModel)){
                            return null;
                        }
                        Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        int realIndex = columnModel.getColumn(index).getModelIndex();
                        return NoteCategoryClass.getDescWithCat(realIndex);
                    }
                };
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column){
                TableColumn tableColumn = getColumnModel().getColumn(column);
                TableCellRenderer renderer = tableColumn.getCellRenderer();
                if (renderer == null) {
                    renderer = universalCellRenderer;
                }
                return renderer;
            }
        };
        controlPane = new javax.swing.JTabbedPane();
        workcenPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        workcenList = new javax.swing.JList<>();
        chooseWorkcensLabel = new javax.swing.JLabel();
        selectAllButton = new javax.swing.JButton();
        lateOnlyCheckBox = new javax.swing.JCheckBox();
        unfinishedOnlyCheckBox = new javax.swing.JCheckBox();
        fromDatePicker = new org.jdesktop.swingx.JXDatePicker();
        fromLabel = new javax.swing.JLabel();
        toLabel = new javax.swing.JLabel();
        toDatePicker = new org.jdesktop.swingx.JXDatePicker();
        allTimeCheckBox = new javax.swing.JCheckBox();
        filterCurrentOpsButton = new javax.swing.JButton();
        orderPanel = new javax.swing.JPanel();
        ordersComboBox = new WideComboBox();
        chooseOrderLabel = new javax.swing.JLabel();
        enterOrderNoLabel = new javax.swing.JLabel();
        orderNoTextField = new javax.swing.JTextField();
        searchWithOrderNoButton = new javax.swing.JButton();
        showAllOrdersButton = new javax.swing.JButton();
        rescheduledPanel = new javax.swing.JPanel();
        showRescheduledButton = new javax.swing.JButton();
        fromLabelR = new javax.swing.JLabel();
        toDatePickerR = new org.jdesktop.swingx.JXDatePicker();
        fromDatePickerR = new org.jdesktop.swingx.JXDatePicker();
        toLabelR = new javax.swing.JLabel();
        allTimeCheckBoxR = new javax.swing.JCheckBox();
        matrixPanel = new javax.swing.JPanel();
        notEmptyOnlyCheckBox = new javax.swing.JCheckBox();
        showMatrixButton = new javax.swing.JButton();
        addNewNoteButton = new javax.swing.JButton();
        ImageIcon loading = new ImageIcon("loading.gif");
        loadingLabel = new javax.swing.JLabel(loading);
        showNoteButton = new javax.swing.JButton();
        manualEndButton = new javax.swing.JButton();
        resultCountLabel = new javax.swing.JLabel();

        noteCategoryLabel.setText("Vyber druh poznámky");

        for(int i = 1; i <= 12; i++) {
            insertIntoCombo(noteCategoryComboBox, new secondary.NoteCategoryClass(i, NoteCategoryClass.getDescWithCat(i)));
        }

        noteLabel.setText("Poznámka");

        noteTextArea.setColumns(20);
        noteTextArea.setRows(5);
        jScrollPane3.setViewportView(noteTextArea);
        noteTextArea.setLineWrap(true);
        noteTextArea.setWrapStyleWord(true);

        javax.swing.GroupLayout delayInfoPanelLayout = new javax.swing.GroupLayout(delayInfoPanel);
        delayInfoPanel.setLayout(delayInfoPanelLayout);
        delayInfoPanelLayout.setHorizontalGroup(
            delayInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(delayInfoPanelLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(delayInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(delayInfoPanelLayout.createSequentialGroup()
                        .addComponent(noteCategoryLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(noteCategoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))
                    .addGroup(delayInfoPanelLayout.createSequentialGroup()
                        .addComponent(noteLabel)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 497, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        delayInfoPanelLayout.setVerticalGroup(
            delayInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(delayInfoPanelLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(delayInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(noteCategoryLabel)
                    .addComponent(noteCategoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(delayInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(noteLabel))
                .addGap(20, 20, 20))
        );

        noteInfoScrollPane.setAutoscrolls(true);
        noteInfoScrollPane.setPreferredSize(new java.awt.Dimension(500, 300));

        showNoteInfoTextArea.setColumns(20);
        showNoteInfoTextArea.setRows(5);
        showNoteInfoTextArea.setPreferredSize(null);
        noteInfoScrollPane.setViewportView(showNoteInfoTextArea);
        showNoteInfoTextArea.setLineWrap(true);
        showNoteInfoTextArea.setWrapStyleWord(true);
        showNoteInfoTextArea.setAutoscrolls(true);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        jScrollPane2.setMinimumSize(new Dimension(screenSize.width - 320, screenSize.height - 100));

        opsTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        opsTable.setToolTipText(null);
        opsTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        opsTable.setShowHorizontalLines(false);
        opsTable.setShowVerticalLines(false);
        opsTable.setSurrendersFocusOnKeystroke(true);
        jScrollPane2.setViewportView(opsTable);

        workcenPanel.setPreferredSize(new java.awt.Dimension(298, 431));

        workcenList.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        workcenList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "RMED", "VTR", "VDKO", "VMED", "VMINC", "VTLAC" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        workcenList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        workcenList.setToolTipText("");
        workcenList.setVisibleRowCount(6);
        jScrollPane1.setViewportView(workcenList);

        chooseWorkcensLabel.setText("Vyber strediská");

        selectAllButton.setText("Vybrať všetky");
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });

        lateOnlyCheckBox.setText("Iba meškajúce");

        unfinishedOnlyCheckBox.setText("Iba nedokončené");

        fromDatePicker.setDate(Date.from(LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        fromLabel.setText("Od");

        toLabel.setText("Do");

        toDatePicker.setDate(Date.from(LocalDate.now().minusDays(0).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        allTimeCheckBox.setText("Celá história");
        allTimeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allTimeCheckBoxActionPerformed(evt);
            }
        });

        filterCurrentOpsButton.setText("Zobraziť");
        filterCurrentOpsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCurrentOpsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout workcenPanelLayout = new javax.swing.GroupLayout(workcenPanel);
        workcenPanel.setLayout(workcenPanelLayout);
        workcenPanelLayout.setHorizontalGroup(
            workcenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(workcenPanelLayout.createSequentialGroup()
                .addGroup(workcenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(workcenPanelLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(selectAllButton))
                    .addGroup(workcenPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(workcenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(workcenPanelLayout.createSequentialGroup()
                                .addGroup(workcenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(toLabel)
                                    .addComponent(fromLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(workcenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(fromDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(toDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(allTimeCheckBox))
                            .addGroup(workcenPanelLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(workcenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lateOnlyCheckBox)
                                    .addComponent(unfinishedOnlyCheckBox))))))
                .addGap(0, 38, Short.MAX_VALUE))
            .addGroup(workcenPanelLayout.createSequentialGroup()
                .addGroup(workcenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(workcenPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chooseWorkcensLabel))
                    .addGroup(workcenPanelLayout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(filterCurrentOpsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        workcenPanelLayout.setVerticalGroup(
            workcenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(workcenPanelLayout.createSequentialGroup()
                .addComponent(chooseWorkcensLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(workcenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(workcenPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(workcenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(workcenPanelLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(workcenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(fromLabel)
                                    .addComponent(fromDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(workcenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(toLabel)
                                    .addComponent(toDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(workcenPanelLayout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(allTimeCheckBox)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                        .addComponent(lateOnlyCheckBox)
                        .addGap(12, 12, 12)
                        .addComponent(unfinishedOnlyCheckBox)
                        .addGap(48, 48, 48)
                        .addComponent(filterCurrentOpsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41))
                    .addGroup(workcenPanelLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(selectAllButton)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        controlPane.addTab("Podľa stredisiek", workcenPanel);

        ordersComboBox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        chooseOrderLabel.setText("Vyber zákazku");

        enterOrderNoLabel.setText("Zadaj číslo zákazky");

        orderNoTextField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        orderNoTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                orderNoTextFieldKeyPressed(evt);
            }
        });

        searchWithOrderNoButton.setText("Hľadať");
        searchWithOrderNoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchWithOrderNoButtonActionPerformed(evt);
            }
        });

        showAllOrdersButton.setText("Všetky zákazky");
        showAllOrdersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllOrdersButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout orderPanelLayout = new javax.swing.GroupLayout(orderPanel);
        orderPanel.setLayout(orderPanelLayout);
        orderPanelLayout.setHorizontalGroup(
            orderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderPanelLayout.createSequentialGroup()
                .addGroup(orderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ordersComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(orderPanelLayout.createSequentialGroup()
                        .addGroup(orderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(orderPanelLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(orderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chooseOrderLabel)
                                    .addComponent(enterOrderNoLabel)
                                    .addComponent(orderNoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(orderPanelLayout.createSequentialGroup()
                                .addGap(85, 85, 85)
                                .addComponent(searchWithOrderNoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 14, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(orderPanelLayout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addComponent(showAllOrdersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        orderPanelLayout.setVerticalGroup(
            orderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderPanelLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(chooseOrderLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ordersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(enterOrderNoLabel)
                .addGap(24, 24, 24)
                .addComponent(orderNoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(searchWithOrderNoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, Short.MAX_VALUE)
                .addComponent(showAllOrdersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70))
        );

        controlPane.addTab("Podľa zákaziek", orderPanel);

        showRescheduledButton.setText("Zobraz preplánované operácie");
        showRescheduledButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showRescheduledButtonActionPerformed(evt);
            }
        });

        fromLabelR.setText("Od");

        toDatePickerR.setDate(Date.from(LocalDate.now().minusDays(0).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        fromDatePickerR.setDate(Date.from(LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        toLabelR.setText("Do");

        allTimeCheckBoxR.setText("Celá história");
        allTimeCheckBoxR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allTimeCheckBoxRActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout rescheduledPanelLayout = new javax.swing.GroupLayout(rescheduledPanel);
        rescheduledPanel.setLayout(rescheduledPanelLayout);
        rescheduledPanelLayout.setHorizontalGroup(
            rescheduledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rescheduledPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rescheduledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fromLabelR)
                    .addComponent(toLabelR))
                .addGap(0, 31, Short.MAX_VALUE)
                .addGroup(rescheduledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(toDatePickerR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fromDatePickerR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(allTimeCheckBoxR)
                .addGap(13, 13, 13))
            .addGroup(rescheduledPanelLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(showRescheduledButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        rescheduledPanelLayout.setVerticalGroup(
            rescheduledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rescheduledPanelLayout.createSequentialGroup()
                .addGroup(rescheduledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rescheduledPanelLayout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addGroup(rescheduledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fromDatePickerR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fromLabelR))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                        .addGroup(rescheduledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(toDatePickerR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(toLabelR))
                        .addGap(77, 77, 77))
                    .addGroup(rescheduledPanelLayout.createSequentialGroup()
                        .addGap(115, 115, 115)
                        .addComponent(allTimeCheckBoxR)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(showRescheduledButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(196, Short.MAX_VALUE))
        );

        controlPane.addTab("Preplánované operácie", rescheduledPanel);

        notEmptyOnlyCheckBox.setText("Skryť prázdne");

        showMatrixButton.setText("Zobraziť maticu");
        showMatrixButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showMatrixButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout matrixPanelLayout = new javax.swing.GroupLayout(matrixPanel);
        matrixPanel.setLayout(matrixPanelLayout);
        matrixPanelLayout.setHorizontalGroup(
            matrixPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(matrixPanelLayout.createSequentialGroup()
                .addGroup(matrixPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(matrixPanelLayout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(showMatrixButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(matrixPanelLayout.createSequentialGroup()
                        .addGap(88, 88, 88)
                        .addComponent(notEmptyOnlyCheckBox)))
                .addContainerGap(85, Short.MAX_VALUE))
        );
        matrixPanelLayout.setVerticalGroup(
            matrixPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(matrixPanelLayout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addComponent(notEmptyOnlyCheckBox)
                .addGap(53, 53, 53)
                .addComponent(showMatrixButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(256, Short.MAX_VALUE))
        );

        controlPane.addTab("Matica problémov", matrixPanel);

        addNewNoteButton.setText("Pridať poznámku");
        addNewNoteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewNoteButtonActionPerformed(evt);
            }
        });

        loadingLabel.setBackground(new java.awt.Color(0, 255, 255));
        loadingLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/loading.gif"))); // NOI18N
        loadingLabel.setText("Načítavanie...");
        loadingLabel.setPreferredSize(new java.awt.Dimension(100, 100));

        showNoteButton.setText("Zobraziť poznámky");
        showNoteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showNoteButtonActionPerformed(evt);
            }
        });

        manualEndButton.setText("Ukončiť operáciu");
        manualEndButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualEndButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(resultCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(controlPane, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(84, 84, 84)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(addNewNoteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(showNoteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(loadingLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(manualEndButton, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1165, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(controlPane, javax.swing.GroupLayout.PREFERRED_SIZE, 521, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(addNewNoteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(showNoteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(manualEndButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(loadingLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(resultCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        loadingLabel.setVisible(false);
        loadingLabel.setHorizontalTextPosition(JLabel.CENTER);
        loadingLabel.setVerticalTextPosition(JLabel.BOTTOM);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void filterCurrentOpsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCurrentOpsButtonActionPerformed
        loadingLabel.setVisible(true);
        filterCurrentOpsButton.setEnabled(false);
        new SwingWorker<OperationsMap, Void>() {
            @Override
            protected OperationsMap doInBackground() throws SQLException {
                DatePair dates = new DatePair();
                if (!dates.checkDates(fromDatePicker.getDate(), toDatePicker.getDate(), allTimeCheckBox.isSelected())) {
                    JOptionPane.showMessageDialog(null, "Zle zadaný dátum.", "Upozornenie", JOptionPane.WARNING_MESSAGE);
                    this.cancel(true);
                }
                return OperationsMap.filterCurrentOps(workcenList.getSelectedValuesList(), dates.getFrom(), dates.getTo(),
                        unfinishedOnlyCheckBox.isSelected(), lateOnlyCheckBox.isSelected());
            }

            @Override
            protected void done() {
                try {
                    if (this.isCancelled()) {
                        return;
                    }
                    fillTableWithOperations(this.get(), evt);
                } catch (InterruptedException | ExecutionException ex) {
                    logAndNotify(ex);
                } finally {
                    loadingLabel.setVisible(false);
                    filterCurrentOpsButton.setEnabled(true);
                }
            }
        }.execute();
    }//GEN-LAST:event_filterCurrentOpsButtonActionPerformed

    private void addNewNoteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewNoteButtonActionPerformed
        if (!isOpSelected()) {
            return;
        }
        String[] options = {"Potvrdiť", "Zrušiť"};
        if (JOptionPane.showOptionDialog(null, delayInfoPanel, "Zadaj informácie o poznámke", 0, JOptionPane.INFORMATION_MESSAGE, null, options, null) == 0) {
            if (noteCategoryComboBox.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(null, "Vyber kategóriu poznámky.", "Upozornenie", JOptionPane.WARNING_MESSAGE);
                addNewNoteButtonActionPerformed(evt);
                return;
            }
            loadingLabel.setVisible(true);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws SQLException {
                    NoteCategoryClass selectedCat = (NoteCategoryClass) noteCategoryComboBox.getSelectedItem();
                    operationManager.insertNoteInfo(opsTableModel.getOpForRow(opsTable.convertRowIndexToModel(opsTable.getSelectedRow())),
                            selectedCat.getCategory(), noteTextArea.getText());
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(null, "Úspešne pridané.", "", JOptionPane.INFORMATION_MESSAGE);
                    } catch (InterruptedException | ExecutionException ex) {
                        logAndNotify(ex);
                    } finally {
                        clearNotePanel();
                        loadingLabel.setVisible(false);
                    }
                }
            }.execute();
        } else {
            clearNotePanel();
        }
    }//GEN-LAST:event_addNewNoteButtonActionPerformed

    private void searchWithOrderNoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchWithOrderNoButtonActionPerformed
        if (orderNoTextField.getText().equals("")) {
            return;
        }
        getOrderSeachSwingWorker(orderNoTextField.getText().toUpperCase(), evt).execute();
    }//GEN-LAST:event_searchWithOrderNoButtonActionPerformed

    private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        for (int i = 0; i <= workcenList.getLastVisibleIndex(); i++) {
            if (!workcenList.isSelectedIndex(i)) {
                workcenList.setSelectedIndex(i);
            }
        }
    }//GEN-LAST:event_selectAllButtonActionPerformed

    private void showMatrixButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showMatrixButtonActionPerformed
        showMatrixButton.setEnabled(false);
        loadingLabel.setVisible(true);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws SQLException {
                matrixTableModel.loadCounts(notEmptyOnlyCheckBox.isSelected());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    opsTable.setModel(matrixTableModel);
                    matrixTableModel.resize(opsTable);
                    opsTable.updateUI();
                } catch (ExecutionException | InterruptedException ex) {
                    logAndNotify(ex);
                } finally {
                    showMatrixButton.setEnabled(true);
                    loadingLabel.setVisible(false);
                    resultCountLabel.setText("");
                }
            }
        }.execute();
    }//GEN-LAST:event_showMatrixButtonActionPerformed

    private void showNoteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showNoteButtonActionPerformed
        if (!isOpSelected()) {
            return;
        }
        CurrentPlanTableModel model = (CurrentPlanTableModel) opsTable.getModel();
        if (model.getOps().getOperations().get(model.getOpForRow(opsTable.convertRowIndexToModel(opsTable.getSelectedRow()))) == null) {
            JOptionPane.showMessageDialog(null, "Vybraná operácia nemá žiadnu poznámku.", "", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        showNoteInfoTextArea.setText(opsTableModel.getNotesSummary(opsTable.convertRowIndexToModel(opsTable.getSelectedRow())));
        JOptionPane.showMessageDialog(null, noteInfoScrollPane, "Informácie o poznámke", JOptionPane.PLAIN_MESSAGE);
    }//GEN-LAST:event_showNoteButtonActionPerformed

    private void manualEndButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualEndButtonActionPerformed
        if (!isOpSelected()) {
            return;
        }
        String[] options = {"Áno", "Nie", "Zrušiť"};
        int result = JOptionPane.showOptionDialog(null, "Chcete pridať aj dôvod ukončenia?", null, 0, JOptionPane.PLAIN_MESSAGE, null, options, null);
        if (result == 2) {
            return;
        }
        if (result == 0) {
            noteCategoryComboBox.setSelectedIndex(0);
            noteCategoryComboBox.setEnabled(false);
            noteTextArea.setText(LocalDate.now().toString() + " - ");
            addNewNoteButtonActionPerformed(evt);
            noteCategoryComboBox.setEnabled(true);
        }
        loadingLabel.setVisible(true);
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws SQLException {
                int index = opsTable.convertRowIndexToModel(opsTable.getSelectedRow());
                operationManager.manuallyEndOp(opsTableModel.getOpForRow(index));
                return index;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(null, "Operácia úspešne ukončená.", "", JOptionPane.INFORMATION_MESSAGE);
                } catch (ExecutionException | InterruptedException ex) {
                    logAndNotify(ex);
                } finally {
                    loadingLabel.setVisible(false);
                }
            }
        }.execute();
    }//GEN-LAST:event_manualEndButtonActionPerformed

    private void allTimeCheckBoxRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allTimeCheckBoxRActionPerformed
        fromDatePickerR.setEnabled(!fromDatePickerR.isEnabled());
        toDatePickerR.setEnabled(!toDatePickerR.isEnabled());
    }//GEN-LAST:event_allTimeCheckBoxRActionPerformed

    private void showRescheduledButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showRescheduledButtonActionPerformed
        loadingLabel.setVisible(true);
        showRescheduledButton.setEnabled(false);
        new SwingWorker<OperationsMap, Void>() {
            @Override
            protected OperationsMap doInBackground() throws SQLException {
                DatePair dates = new DatePair();
                if (!dates.checkDates(fromDatePickerR.getDate(), toDatePickerR.getDate(), allTimeCheckBoxR.isSelected())) {
                    JOptionPane.showMessageDialog(null, "Zle zadaný dátum.", "Upozornenie", JOptionPane.WARNING_MESSAGE);
                    this.cancel(true);
                }
                return OperationsMap.showRescheduled(dates.getFrom(), dates.getTo());
            }

            @Override
            protected void done() {
                try {
                    if (this.isCancelled()) {
                        return;
                    }
                    fillTableWithOperations(this.get(), evt);
                } catch (InterruptedException | ExecutionException ex) {
                    logAndNotify(ex);
                } finally {
                    showRescheduledButton.setEnabled(true);
                    loadingLabel.setVisible(false);
                }
            }
        }.execute();
    }//GEN-LAST:event_showRescheduledButtonActionPerformed

    private void allTimeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allTimeCheckBoxActionPerformed
        fromDatePicker.setEnabled(!fromDatePicker.isEnabled());
        toDatePicker.setEnabled(!toDatePicker.isEnabled());

    }//GEN-LAST:event_allTimeCheckBoxActionPerformed

    private void orderNoTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_orderNoTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            getOrderSeachSwingWorker(orderNoTextField.getText().toUpperCase(), evt).execute();
        }
    }//GEN-LAST:event_orderNoTextFieldKeyPressed

    private void showAllOrdersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllOrdersButtonActionPerformed
        loadingLabel.setVisible(true);
        showAllOrdersButton.setEnabled(false);
        new SwingWorker<OperationsMap, Void>() {
            @Override
            protected OperationsMap doInBackground() throws SQLException {
                return OperationsMap.showAllOps();
            }

            @Override
            protected void done() {
                try {
                    fillTableWithOperations(this.get(), evt);
                } catch (InterruptedException | ExecutionException ex) {
                    logAndNotify(ex);
                } finally {
                    loadingLabel.setVisible(false);
                    showAllOrdersButton.setEnabled(true);
                }
            }
        }.execute();
    }//GEN-LAST:event_showAllOrdersButtonActionPerformed

    
    //Auxiliary methods
    
    
    //Returns new SwingWorker for searching for order.
    private SwingWorker getOrderSeachSwingWorker(String query, AWTEvent evt) {
        ordersComboBox.setEnabled(false);
        searchWithOrderNoButton.setEnabled(false);
        loadingLabel.setVisible(true);
        return new SwingWorker<OperationsMap, Void>() {
            @Override
            protected OperationsMap doInBackground() throws SQLException {
                for (int i = 0; i < ordersComboBox.getItemCount(); i++) {
                    if (ordersComboBox.getItemAt(i).getItemNo().startsWith(query)) {
                        return OperationsMap.searchWithItemNo(query);
                    }
                }
                return OperationsMap.searchWithOrderNo(query);
            }

            @Override
            protected void done() {
                try {
                    fillTableWithOperations(this.get(), evt);
                } catch (InterruptedException | ExecutionException ex) {
                    logAndNotify(ex);
                } finally {
                    loadingLabel.setVisible(false);
                    ordersComboBox.setEnabled(true);
                    searchWithOrderNoButton.setEnabled(true);
                }
            }
        };
    }

    private boolean isOpSelected() {
        if (opsTable.getSelectedRow() == -1 || !(opsTable.getModel() instanceof CurrentPlanTableModel)) {
            JOptionPane.showMessageDialog(null, "Vyber operáciu z tabuľky.", "Chyba", JOptionPane.OK_OPTION);
            return false;
        }
        return true;
    }

    private void fillTableWithOperations(OperationsMap opL, AWTEvent evt) {
        opsTableModel.setOps(opL);
        opsTable.setModel(opsTableModel);
        if (Arrays.asList(searchWithOrderNoButton, ordersComboBox, opsTable, orderNoTextField, showAllOrdersButton).contains(evt.getSource())) {
            tcm.hideColumn("Číslo položky");
            tcm.hideColumn("Zákazka");
            tcm.hideColumn("Pracovisko");
            tcm.showColumn("Číslo položky");
            universalCellRenderer.setThickLines(true);
        } else {
            tcm.hideColumn("Číslo položky");
            tcm.hideColumn("Zákazka");
            tcm.hideColumn("Pracovisko");
            tcm.showColumn("Zákazka");
            tcm.showColumn("Pracovisko");
            universalCellRenderer.setThickLines(false);
        }
        opsTableModel.resize(opsTable);
        opsTable.updateUI();
        resultCountLabel.setText(opL.getOperations().size() + " záznamov");
    }

    private void insertIntoCombo(JComboBox combo, Object item) {
        if (item == null) {
            return;
        }
        MutableComboBoxModel model = (MutableComboBoxModel) combo.getModel();
        model.addElement(item);
        model.setSelectedItem(null);
    }

    //Logs exception to debug.log file in the same folder as app and notifies user with dialog message.
    private void logAndNotify(Exception ex) {
        LOG.error(ex.getMessage(), ex);
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Chyba", JOptionPane.ERROR_MESSAGE);
    }

    private void clearNotePanel() {
        noteCategoryComboBox.setSelectedIndex(-1);
        noteTextArea.setText("");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel("com.alee.laf.WebLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new MainWindow().setVisible(true);
                } catch (SQLException | IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private class DatePair {

        private Date from;
        private Date to;

        public DatePair() {
        }

        //Sets from and to values accordingly either to parameters if valid dates are entered or to MIN & MAX if all time option is selected.
        //Returns true if valid dates have been set.
        private boolean checkDates(Date from, Date to, boolean allTime) {
            if (allTime) {
                this.from = MIN;
                this.to = MAX;
                return true;
            }
            if (from.before(MIN) || from.after(MAX) || to.before(MIN) || to.after(MAX) || to.before(from)) {
                return false;
            }
            this.from = from;
            this.to = to;
            return true;
        }

        public Date getFrom() {
            return from;
        }

        public Date getTo() {
            return to;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addNewNoteButton;
    private javax.swing.JCheckBox allTimeCheckBox;
    private javax.swing.JCheckBox allTimeCheckBoxR;
    private javax.swing.JLabel chooseOrderLabel;
    private javax.swing.JLabel chooseWorkcensLabel;
    private javax.swing.JTabbedPane controlPane;
    private javax.swing.JPanel delayInfoPanel;
    private javax.swing.JLabel enterOrderNoLabel;
    private javax.swing.JButton filterCurrentOpsButton;
    private org.jdesktop.swingx.JXDatePicker fromDatePicker;
    private org.jdesktop.swingx.JXDatePicker fromDatePickerR;
    private javax.swing.JLabel fromLabel;
    private javax.swing.JLabel fromLabelR;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JCheckBox lateOnlyCheckBox;
    private javax.swing.JLabel loadingLabel;
    private javax.swing.JButton manualEndButton;
    private javax.swing.JPanel matrixPanel;
    private javax.swing.JCheckBox notEmptyOnlyCheckBox;
    private javax.swing.JComboBox<String> noteCategoryComboBox;
    private javax.swing.JLabel noteCategoryLabel;
    private javax.swing.JScrollPane noteInfoScrollPane;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JTextArea noteTextArea;
    private javax.swing.JTable opsTable;
    private javax.swing.JTextField orderNoTextField;
    private javax.swing.JPanel orderPanel;
    private javax.swing.JComboBox<OrdersComboBoxItem> ordersComboBox;
    private javax.swing.JPanel rescheduledPanel;
    private javax.swing.JLabel resultCountLabel;
    private javax.swing.JButton searchWithOrderNoButton;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JButton showAllOrdersButton;
    private javax.swing.JButton showMatrixButton;
    private javax.swing.JButton showNoteButton;
    private javax.swing.JTextArea showNoteInfoTextArea;
    private javax.swing.JButton showRescheduledButton;
    private org.jdesktop.swingx.JXDatePicker toDatePicker;
    private org.jdesktop.swingx.JXDatePicker toDatePickerR;
    private javax.swing.JLabel toLabel;
    private javax.swing.JLabel toLabelR;
    private javax.swing.JCheckBox unfinishedOnlyCheckBox;
    private javax.swing.JList<String> workcenList;
    private javax.swing.JPanel workcenPanel;
    // End of variables declaration//GEN-END:variables
}
