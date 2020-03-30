package com.exciting;

import javax.swing.*;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class GUI{

    LinkList linkList =LinkList.getInstance();
    public JTextArea area=new JTextArea(15,45);
    public String[][] tableString;

    PrintStream ps = new PrintStream(System.out) {
        public void println(String infoText) {
            area.append(infoText +"\n");
            //area.setCaretPosition(area.getDocument().getLength());
        }
    };



    int RefreshPeriod=100;
    TimerManager timerManager =new TimerManager();
    Timer timer= timerManager.createTimer();
    JMenuBar menu=new JMenuBar();
    JMenu file=new JMenu("文件");
    JMenu control=new JMenu("控制");
    JMenu help=new JMenu("帮助");
    JMenuItem openList=new JMenuItem("打开(O)...");
    JMenuItem outputList=new JMenuItem("保存地址表(S)...");
    JMenuItem outputLog=new JMenuItem("导出日志(L)...");
    JMenuItem settings=new JMenuItem("设置(U)...");
    JMenuItem exit=new JMenuItem("退出(X)");
    JRadioButtonMenuItem startButton=new JRadioButtonMenuItem("开始(I)");
    JRadioButtonMenuItem pauseButton=new JRadioButtonMenuItem("暂停(P)");
    ButtonGroup controlSet=new ButtonGroup();
    JMenuItem emptyButton=new JMenuItem("清空(E)");
    JMenuItem getHelp=new JMenuItem("获取帮助(H)");
    JMenuItem about=new JMenuItem("关于(A)");
    JPopupMenu tableMenu=new JPopupMenu();
    JMenuItem copyTableValue=new JMenuItem("复制");
    JMenuItem deleteThisRecord=new JMenuItem("删除此条");
    JPopupMenu fakeMenu=new JPopupMenu();
    JMenuItem dontTouch=new JMenuItem("暂停后才可复制哦");
    JTabbedPane controlPanel=new JTabbedPane();
    JLabel macInputLabel=new JLabel("mac地址:");
    JLabel portInputLabel=new JLabel("端口:");
    JLabel countdowntimeInputLabel=new JLabel("倒计时:");
    JTextField macInputField=new JTextField(10);
    JTextField portInputField=new JTextField(3);
    JTextField countdowntimeInputField=new JTextField(3);
    JLabel srcMACInputLabel=new JLabel("源mac地址:");
    JTextField srcMACInputField=new JTextField(10);
    JLabel dstMACInputLabel=new JLabel("目标mac地址:");
    JTextField dstMACInputField=new JTextField(10);
    JLabel dstPortInputLabel=new JLabel("端口:");
    JTextField dstPortInputField=new JTextField(3);
    JButton panelEditButton=new JButton("修改");
    JButton panelDeleteButton=new JButton("删除该条");
    JButton panelAddButton=new JButton("添加新项");
    JButton panelSendButton=new JButton("发送数据包");
    JLabel panelContentLabel=new JLabel("数据包内容:");
    JTextField panelContentField=new JTextField(44);

    Font msyh=new Font("微软雅黑", Font.PLAIN,12);
    JFrame frame=new JFrame();
    JPanel addressControl=new JPanel();
    JPanel packetEmu=new JPanel();
    JButton buttonSaveLog=new JButton("保存日志");
    JButton buttonFeelingLucky=new JButton("随机生成两条记录");

    //JTextArea area=new JTextArea(10,20);
    JScrollPane scroller=new JScrollPane(area);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    //Vector vData=new Vector<String>(Arrays.asList(a));
    //Vector vName=new Vector<String>(Arrays.asList(a));
    //Vector vRow=new Vector<String>(Arrays.asList(a));

    final String[][] DefaultBlank={{"","当前表为空",""}};
    final String[] Names={"MAC Address","Port","CountDownTime"};

    DefaultTableModel defaultTableModel =new DefaultTableModel(tableString,Names);

    //JTable table=new JTable(f,Names);
    JTable table=new JTable(defaultTableModel);
    JScrollPane pane=new JScrollPane(table);

    FileNameExtensionFilter typeTxt=new FileNameExtensionFilter("文本文档(*.txt)","txt");
    FileNameExtensionFilter typeCsv=new FileNameExtensionFilter("CSV (逗号分隔)(*.csv)","csv");

    public void messaging(String S){
        StringBuilder s=new StringBuilder(simpleDateFormat.format(new Date()));
        s.append(" - ");
        s.append(S);
        System.out.println(s.toString());
        //System.out.println(df.format(new Date())+" - "+S);
    }

    public void openListFromFile() throws IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File((".")));
        chooser.setFileFilter(typeCsv);
        chooser.addChoosableFileFilter(typeTxt);
        if(chooser.showOpenDialog(frame)!=JFileChooser.APPROVE_OPTION){
            return;
        }
        File f=chooser.getSelectedFile();
        FileInputStream fis=new FileInputStream(f);
        InputStreamReader isr=new InputStreamReader(fis);
        BufferedReader br=new BufferedReader(isr);
        List<String> tempList=new ArrayList<String>();
        String line=null;

        linkList.emptyList();
        br.readLine();
        while ((line=br.readLine())!=null){
            String items[]=line.split(",");
            linkList.add(items[0],Integer.parseInt(items[1]),Integer.parseInt(items[2]));
        }

        br.close();
        isr.close();
        //startButton.setSelected(true);
    }

    public void saveList(){

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File((".")));
        chooser.setFileFilter(typeCsv);
        chooser.addChoosableFileFilter(typeTxt);
        if(chooser.showSaveDialog(frame)!=JFileChooser.APPROVE_OPTION){
            return;
        }
        File file=chooser.getSelectedFile();

        if(chooser.getFileFilter().equals(typeCsv)&!chooser.getName(file).contains(".csv")&!chooser.getName(file).contains(".CSV")){
            file=new File(chooser.getCurrentDirectory(),chooser.getName(file)+".csv");
        }else if(chooser.getFileFilter().equals(typeTxt)&!chooser.getName(file).contains(".txt")&!chooser.getName(file).contains(".TXT")){
            file=new File(chooser.getCurrentDirectory(),chooser.getName(file)+".txt");
        }

        if(file.exists()){
            if(JOptionPane.showConfirmDialog(frame,file.getName()+" 已存在。\n要替换它吗?","确认",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.YES_OPTION){
                return;
            }
        }

        try{
            PrintWriter out=new PrintWriter(new FileOutputStream(file),true);
            out.println("MAC Address,Port,CountDownTime");
            if(!linkList.isEmpty()){
                for(int i = 0; i< tableString.length; ++i){
                    for(int j=0;j<3;++j){
                        out.print(tableString[i][j]);
                        if(j!=2){
                            out.print(",");
                        }
                    }
                    out.println();
                }
            }

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public void saveLogTotxt(){
        //JFileChooser chooser=new JFileChooser();

        JFileChooser chooser = new JFileChooser();
        //chooser.showOpenDialog(null);
        chooser.setCurrentDirectory(new File((".")));
        chooser.setFileFilter(new FileNameExtensionFilter("文本文档(*.txt)","txt"));
                /*chooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.getName().toLowerCase().endsWith(".txt");
                    }
                    @Override
                    public String getDescription() {
                        return "文本文档(*.txt)";
                    }
                });*/
        if(chooser.showSaveDialog(frame)!=JFileChooser.APPROVE_OPTION){
            return;
        }
        File file=chooser.getSelectedFile();

        if(file.exists()&JOptionPane.showConfirmDialog(frame,file.getName()+" 已存在。\n要替换它吗?","确认",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION){
            if(!chooser.getName(file).contains(".txt")&!chooser.getName(file).contains(".TXT")){
                file=new File(chooser.getCurrentDirectory(),chooser.getName(file)+".txt");
            }
            String outputLog=area.getText();
            String[] lines=outputLog.trim().split("\n");
            try{
                PrintWriter out=new PrintWriter(new FileOutputStream(file),true);
                //OutputStreamWriter osw=new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
                for(String line:lines){
                    out.println(line);
                    //osw.write(line);
                }
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }

    }

    public void settings(){
        JDialog settingsFrame=new JDialog();
        JLabel perTimeLabel=new JLabel("单位时间(s):");
        JLabel TLabel=new JLabel("重置倒计时时间T(s):");
        JTextField perTimeField=new JTextField(4);
        JTextField TField=new JTextField(4);
        JButton OK=new JButton("确认");
        JButton Cancel=new JButton("取消");
        JButton modifyRefreshPeriod=new JButton("修改表格刷新间隔");





        OK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    int perTimeset=Integer.parseInt(perTimeField.getText())*1000;
                    int Tset=Integer.parseInt(TField.getText());
                    settingsConfirm(perTimeset,Tset);
                }catch (NumberFormatException x){

                }finally {
                    settingsFrame.dispose();
                }

            }
        });

        Cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settingsFrame.dispose();
            }
        });

        modifyRefreshPeriod.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int newRefreshPeriod=Integer.parseInt(JOptionPane.showInputDialog(settingsFrame,"请输入新的刷新间隔(s):","修改",JOptionPane.PLAIN_MESSAGE));
                    RefreshPeriod=newRefreshPeriod;
                    timerManager.stopAllTimer();
                    timer= timerManager.createTimer();
                    initRefreshTimer();
                }catch (NumberFormatException ex){

                }

            }
        });

        settingsFrame.setModal(true);
        settingsFrame.setTitle("设置");
        settingsFrame.setLayout(new FlowLayout(FlowLayout.LEFT,20,40));

        perTimeField.setText(String.valueOf(linkList.perTime/1000));
        TField.setText(String.valueOf(linkList.T));


        settingsFrame.getContentPane().add(perTimeLabel);
        settingsFrame.getContentPane().add(perTimeField);
        settingsFrame.getContentPane().add(TLabel);
        settingsFrame.getContentPane().add(TField);
        settingsFrame.getContentPane().add(OK);
        settingsFrame.getContentPane().add(Cancel);
        settingsFrame.getContentPane().add(modifyRefreshPeriod);
        settingsFrame.setSize(370,200);
        settingsFrame.setResizable(false);
        settingsFrame.setVisible(true);
    }

    public void settingsConfirm(int perTimeset,int Tset){
        linkList.perTime=perTimeset;
        linkList.T=Tset;
    }

    public void exit(){
        System.exit(0);
    }

    public void startButton(){
        linkList.startAllTimer();
        table.setEnabled(false);
    }

    public void pauseButton(){
        linkList.pauseAllTimer();
        table.setEnabled(true);
    }

    public void emptyButton(){
        linkList.emptyList();
        refreshTable();
        table.setEnabled(false);
    }

    public void about(){
        JOptionPane.showMessageDialog(frame,"本程序使用Java Swing构建","关于",JOptionPane.PLAIN_MESSAGE);
    }

    public void copyTableValue(){
        if(!linkList.isEmpty()){
            try {
                Object x= defaultTableModel.getValueAt(table.getSelectedRow(),table.getSelectedColumn());
                Clipboard sysClipBoard=Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable clipText=new StringSelection(x.toString());
                sysClipBoard.setContents(clipText,null);
                //System.out.println(x);
            }catch (ArrayIndexOutOfBoundsException ex){
                popupMenuExceptionTip();
            }
        }
    }

    public void editThisRecord(){
        if(!linkList.isEmpty()){
            try {
                linkList.editList(table.getSelectedRow(),macInputField.getText(),Integer.parseInt(portInputField.getText()),Integer.parseInt(countdowntimeInputField.getText()));
                refreshTable();
            }catch (NumberFormatException e){
                editItemExceptionTip();
            }
        }

    }

    public void deleteThisRecord(){
        if(!linkList.isEmpty()){
            try {
                Object x= defaultTableModel.getValueAt(table.getSelectedRow(),0);
                linkList.removeAddressFromList(x.toString());
                refreshTable();
            }catch (ArrayIndexOutOfBoundsException ex){
                popupMenuExceptionTip();
            }
        }
    }

    public void addNewRecord(){
        if(!linkList.findMAC(macInputField.getText())){
            linkList.add(macInputField.getText(),Integer.parseInt(portInputField.getText()),Integer.parseInt(countdowntimeInputField.getText()));
            refreshTable();
        }else{
            duplicateItemExceptionTip();
        }

    }

    public void sendPacketButton(){
        linkList.sendPacket(srcMACInputField.getText(),dstMACInputField.getText(),Integer.parseInt(dstPortInputField.getText()));
        panelContentField.setText("");
        refreshTable();
    }

    public void initRefreshTimer(){
        timer.schedule(new TimerManager.Task(){
            @Override
            public void run() {

                if(!linkList.isPaused()){
                    refreshTable();
                }


                //tm.getDataVector().clear();

                //tm.fireTableDataChanged();
                //table.setPreferredScrollableViewportSize(new Dimension(350,200));

                //table.validate();
                //table.updateUI();
                //area.validate();
                //area.updateUI();
                //System.out.println("Length:"+l.getLength());
            }
        },0,RefreshPeriod);

    }

    public void initGlobalFont(){
        //UIManager.put("MenuBar.font",msyh);
        UIManager.put("Menu.font",msyh); //works for MenuBar
        UIManager.put("MenuItem.font",msyh);
        UIManager.put("Button.font",msyh);
        UIManager.put("DesktopIcon.font",msyh);
        UIManager.put("RadioButtonMenuItem.font",msyh);
        UIManager.put("Label.font",msyh);
        UIManager.put("TextArea.font",msyh);
        UIManager.put("TextField.font",msyh);
        UIManager.put("ComboBox.font",msyh);
        UIManager.put("ToggleButton.font",msyh);//...
        UIManager.put("Table.font",msyh);
        UIManager.put("TableHeader.font",msyh);

        UIManager.put("JControlPane.font",msyh);
        UIManager.put("Panel.font",msyh);
        UIManager.put("Pane.font",msyh);

        area.updateUI();
        table.updateUI();

        menu.updateUI();
        file.updateUI();
        openList.updateUI();
        outputList.updateUI();
        outputLog.updateUI();
        settings.updateUI();
        exit.updateUI();
        control.updateUI();
        startButton.updateUI();
        pauseButton.updateUI();
        emptyButton.updateUI();
        help.updateUI();
        getHelp.updateUI();
        about.updateUI();
        tableMenu.updateUI();
        copyTableValue.updateUI();
        deleteThisRecord.updateUI();
        fakeMenu.updateUI();
        dontTouch.updateUI();

        buttonSaveLog.updateUI();
        buttonFeelingLucky.updateUI();

        controlPanel.updateUI();
        //addressControl.updateUI();
        macInputLabel.updateUI();
        portInputLabel.updateUI();
        countdowntimeInputLabel.updateUI();
        macInputField.updateUI();
        portInputField.updateUI();
        countdowntimeInputField.updateUI();
        panelEditButton.updateUI();
        panelDeleteButton.updateUI();
        panelAddButton.updateUI();

        //packetEmu.updateUI();
        srcMACInputLabel.updateUI();
        srcMACInputField.updateUI();
        dstMACInputLabel.updateUI();
        dstMACInputField.updateUI();
        dstPortInputLabel.updateUI();
        dstPortInputField.updateUI();
        panelSendButton.updateUI();
        panelContentLabel.updateUI();
        panelContentField.updateUI();


        pane.updateUI();


        /*
        FontUIResource fontRes=new FontUIResource(msyh);
        for(Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();){
            Object key=keys.nextElement();
            Object value=UIManager.get(key);
            if(value instanceof FontUIResource){
                UIManager.put(key,fontRes);
            }
        }
        */


    }

    public void refreshTable(){
        tableString = linkList.genTableArray();
        if(tableString !=null){
            defaultTableModel.setDataVector(tableString,Names);
        }else{
            defaultTableModel.setDataVector(DefaultBlank,Names);
        }
    }

    public void popupMenuExceptionTip(){
        JOptionPane.showMessageDialog(frame,"请先用左键单击选中表格","HAHA",JOptionPane.INFORMATION_MESSAGE);
    }


    public void duplicateItemExceptionTip(){
        JOptionPane.showMessageDialog(frame,"路由表中已存在该地址。\n若要修改对应端口和倒计时，请点击“修改”。","HAHA",JOptionPane.INFORMATION_MESSAGE);
    }

    public void editItemExceptionTip(){
        JOptionPane.showMessageDialog(frame,"输入的待修改数据格式不正确。\n","HAHA",JOptionPane.INFORMATION_MESSAGE);
    }


    public GUI(){

        System.setOut(ps);



        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        }));


        initGlobalFont();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
        }

                /*ScheduledExecutorService pool= Executors.newScheduledThreadPool(1);
                pool.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        f=l.genTableArray();
                        tm.setDataVector(f,Names);
                        System.out.println("Length:"+l.getLength());
                    }
                }, 0, 1, TimeUnit.SECONDS);*/

        menu.add(file);
        menu.add(control);
        menu.add(help);
        file.add(openList);
        file.add(outputList);
        file.add(outputLog);
        file.add(settings);
        file.add(exit);
        controlSet.add(startButton);
        controlSet.add(pauseButton);
        control.add(startButton);
        control.add(pauseButton);
        control.add(emptyButton);
        help.add(getHelp);
        help.add(about);
        tableMenu.add(copyTableValue);
        tableMenu.add(deleteThisRecord);
        fakeMenu.add(dontTouch);

        //Timer timer=new Timer();

        initRefreshTimer();

        table.setModel(defaultTableModel);
        table.setEnabled(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        //table.setSize(200,50);
        //tm.setColumnIdentifiers(Names);
        //tm.addRow(vData);
        //tm.addRow(vName);

        area.setLineWrap(true);
        area.setText("程序启动!\n");
        area.setEditable(false);
        //panel.add(scroller);
        //panel.add(pane);
        table.setPreferredScrollableViewportSize(new Dimension(350,200));
        //table.getColumnModel().getColumn(0).setPreferredWidth(100);
        //table.setGridColor(Color.BLACK);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        buttonFeelingLucky.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                linkList.randomInit();
                linkList.randomPacketEmu();
                refreshTable();
                //area.append(df.format(new Date())+" - 随机生成了一条路由表记录!\n");
                //System.out.println(df.format(new Date())+" - 随机生成了一条路由表记录!");
                messaging("随机生成了一条路由表记录!");
                //startButton.setSelected(true);
            }
        });
        buttonSaveLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //saveList();
                saveLogTotxt();
                //l.stopAllTimer();
            }
        });

        file.addMenuKeyListener(new MenuKeyListener() {
            @Override
            public void menuKeyTyped(MenuKeyEvent e) {
                switch (e.getKeyChar()){
                    case 'o':
                    case 'O': {
                        try {
                            openListFromFile();
                        }catch (IOException x){

                        }
                        break;
                    }
                    case 's':
                    case 'S':{
                        saveList();
                        break;
                    }
                    case 'l':
                    case 'L':{
                        saveLogTotxt();
                        break;
                    }
                    case 'u':
                    case 'U':{
                        settings();
                        break;
                    }
                    case 'x':
                    case 'X':{
                        exit();
                        break;
                    }
                }
            }

            @Override
            public void menuKeyPressed(MenuKeyEvent e) {

            }

            @Override
            public void menuKeyReleased(MenuKeyEvent e) {

            }
        });

        control.addMenuKeyListener(new MenuKeyListener() {
            @Override
            public void menuKeyTyped(MenuKeyEvent e) {
                switch (e.getKeyChar()){
                    case 'i':
                    case 'I':{
                        startButton();
                        break;
                    }
                    case 'p':
                    case 'P':{
                        pauseButton();
                        break;
                    }
                    case 'e':
                    case 'E':{
                        emptyButton();
                        break;
                    }
                }
            }

            @Override
            public void menuKeyPressed(MenuKeyEvent e) {

            }

            @Override
            public void menuKeyReleased(MenuKeyEvent e) {

            }
        });

        help.addMenuKeyListener(new MenuKeyListener() {
            @Override
            public void menuKeyTyped(MenuKeyEvent e) {
                switch (e.getKeyChar()){
                    case 'h':
                    case 'H':{
                        break;
                    }
                    case 'a':
                    case 'A':{
                        about();
                        break;
                    }
                }
            }

            @Override
            public void menuKeyPressed(MenuKeyEvent e) {

            }

            @Override
            public void menuKeyReleased(MenuKeyEvent e) {

            }
        });

        openList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openListFromFile();
                    refreshTable();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        outputList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveList();
            }
        });

        outputLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    saveLogTotxt();
                }catch (ArrayIndexOutOfBoundsException ex){

                }

            }
        });

        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings();
            }
        });

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseButton();
            }
        });

        emptyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emptyButton();
            }
        });

        getHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                about();
            }
        });

        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton()==MouseEvent.BUTTON3&!linkList.isPaused()&!linkList.isEmpty()){
                    fakeMenu.show(pane,e.getX(),e.getY());
                }else if(e.getButton()==MouseEvent.BUTTON3){
                    tableMenu.show(pane,e.getX(),e.getY());
                }
                macInputField.setText((String) defaultTableModel.getValueAt(table.getSelectedRow(),0));
                portInputField.setText((String) defaultTableModel.getValueAt(table.getSelectedRow(),1));
                countdowntimeInputField.setText((String) defaultTableModel.getValueAt(table.getSelectedRow(),2));

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        copyTableValue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyTableValue();
            }
        });

        deleteThisRecord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteThisRecord();
            }
        });

        panelEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editThisRecord();
            }
        });

        panelDeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteThisRecord();
            }
        });

        panelAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewRecord();
            }
        });

        panelSendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendPacketButton();
            }
        });



        startButton.setSelected(true);
        frame.setJMenuBar(menu);
        frame.setTitle("交换机数据帧转发机制仿真 - 测试");
        frame.setLayout(new FlowLayout(FlowLayout.LEFT,20,20));
        //frame.setLayout(new GridLayout(4, 3));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.getContentPane().add(panel);
        frame.getContentPane().add(pane);
        frame.getContentPane().add(scroller);
        addressControl.setPreferredSize(new Dimension(600, 200));
        packetEmu.setPreferredSize(new Dimension(600, 200));
        controlPanel.addTab("地址表修改", addressControl);
        controlPanel.addTab("转发机制模拟", packetEmu);

        frame.getContentPane().add(controlPanel);

        addressControl.add(macInputLabel);
        addressControl.add(macInputField);
        addressControl.add(portInputLabel);
        addressControl.add(portInputField);
        addressControl.add(countdowntimeInputLabel);
        addressControl.add(countdowntimeInputField);
        addressControl.add(panelEditButton);
        addressControl.add(panelDeleteButton);
        addressControl.add(panelAddButton);

        packetEmu.add(srcMACInputLabel);
        packetEmu.add(srcMACInputField);
        packetEmu.add(dstMACInputLabel);
        packetEmu.add(dstMACInputField);
        packetEmu.add(dstPortInputLabel);
        packetEmu.add(dstPortInputField);
        packetEmu.add(panelSendButton);
        packetEmu.add(panelContentLabel);
        packetEmu.add(panelContentField);

        /*
        frame.getContentPane().add(macInputLabel);
        frame.getContentPane().add(macInputField);
        frame.getContentPane().add(portInputLabel);
        frame.getContentPane().add(portInputField);
        frame.getContentPane().add(countdowntimeInputLabel);
        frame.getContentPane().add(countdowntimeInputField);
        */

        frame.getContentPane().add(buttonSaveLog);
        frame.getContentPane().add(buttonFeelingLucky);



        //frame.setResizable(true);
        //frame.setMinimumSize(new Dimension(960,540));
        //frame.setMaximumSize(new Dimension(960,540));


        frame.setSize(945,600);
        //frame.setResizable(false);
        //frame.pack();

        frame.setVisible(true);
    }

}