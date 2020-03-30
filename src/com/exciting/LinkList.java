package com.exciting;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;

public class LinkList{


    TimerManager timerManager =new TimerManager();
    private boolean paused;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    public int T=60;
    public int perTime=1000;
    private class Node{

        private Node previous;
        private String MAC;
        private int Port,CountDownTime;
        private Node next;

        private Node(String MAC,int Port,int CountDownTime){

            this.MAC=MAC;
            this.Port=Port;
            this.CountDownTime=CountDownTime;

            /*ScheduledExecutorService pool= Executors.newScheduledThreadPool(1);
            pool.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    //super.run();
                    if(Node.this.CountDownTime!=0){
                        Node.this.CountDownTime--;
                    }else{
                        timer.cancel();
                        //Node.this.previous.next=Node.this.next;
                        if(Node.this.previous==null&Node.this.next==null){
                            Node.this.clear();
                        }else if(Node.this.previous==null&Node.this.next!=null){
                            Node.this.removeRoot();
                        }else if(Node.this.previous!=null&Node.this.next==null){
                            Node.this.removeTail();
                        }else if(Node.this.previous!=null&Node.this.next!=null){
                            Node.this.previous.next=Node.this.next;
                            Node.this.next.previous=Node.this.previous;
                            Node.this.normalremove();
                        }
                        if(Node.this.CountDownTime==0){
                            System.out.println("ERROR DETECTED");
                        }
                    }
                    System.out.println("Port "+Node.this.Port+" is "+Node.this.CountDownTime);
                }
            },0,1, TimeUnit.SECONDS);*/




            Timer timer= timerManager.createTimer();


            //Timer timer=new Timer();
            timer.schedule(new TimerManager.Task(){
                @Override
                public void run() {
                    //super.run();
                    //boolean isPaused=paused;
                    if(!paused){
                        if(Node.this.CountDownTime!=0){
                            Node.this.CountDownTime--;
                        }else{
                            timer.cancel();
                            removeNode(Node.this);

                            /*
                            //Node.this.previous.next=Node.this.next;
                            if(Node.this.previous==null&Node.this.next==null){
                                deleteMessaging(Node.this.MAC);
                                Node.this.clear();
                            }else if(Node.this.previous==null&Node.this.next!=null){
                                deleteMessaging(Node.this.MAC);
                                Node.this.removeRoot();
                            }else if(Node.this.previous!=null&Node.this.next==null){
                                deleteMessaging(Node.this.MAC);
                                Node.this.removeTail();
                            }else if(Node.this.previous!=null&Node.this.next!=null){
                                deleteMessaging(Node.this.MAC);
                                Node.this.previous.next=Node.this.next;
                                Node.this.next.previous=Node.this.previous;
                                Node.this.updateListLength();
                            }
                            if(Node.this.CountDownTime==0){
                                System.out.println("ERROR DETECTED");
                            }
                            */

                        }
                        //System.out.println("Port "+Node.this.Port+" is "+Node.this.CountDownTime);

                    }


                }
            },0,perTime);


        }

        private void createFromHead(Node N){
            synchronized(LinkList.this){
                if(root==null){
                    root=N;
                }else{
                    N.next=root;
                    root.previous=N;
                    root=N;
                }
                ++length;
            }
        }

        private void createFromTail(Node N){
            synchronized(LinkList.this){
                if(root==null){
                    root=N;
                }else{
                    Node current=root;
                    int count=1;
                    while(count<length){
                        current=current.next;
                        ++count;
                    }
                    N.next=null;
                    N.previous=current;
                    current.next=N;
                }
                ++length;
            }
        }

        private boolean containsMAC(String MAC){
            synchronized(LinkList.this){
                if(root==null){
                    return false;
                }else{
                    Node current=root;
                    while(!current.MAC.equals(MAC)){
                        if(current.next==null){
                            return false;
                        }
                        current=current.next;
                    }
                    return true;
                }
            }
            /*if(MAC.equals(current.MAC)){
                return true;
            }else{
                if(current.next!=null){
                    return current.next.containsMAC(MAC);
                }else{
                    return false;
                }
            }*/
        }

        private boolean portEquals(int Port){
            if(Port==this.Port){
                return true;
            }else{
                return false;
            }
        }

        private boolean switching(String MAC){
            if(MAC.equals(this.MAC)){
                this.CountDownTime=T;
                return true;
            }else{
                if(this.next!=null){
                    return this.next.switching(MAC);
                }else{
                    return false;
                }
            }
        }

        private boolean switching(String MAC,int Port){
            if(MAC.equals(this.MAC)){
                this.Port=Port;
                return true;
            }else{
                if(this.next!=null){
                    return this.next.switching(MAC,Port);
                }else{
                    return false;
                }
            }
        }

        private boolean broadcastingPorts(String SrcMAC,String DstMAC,int Port){

            while(this.next!=null){
                if(Port!=this.Port){
                    forwarding(SrcMAC,DstMAC,this.Port);
                    return this.next.broadcastingPorts(SrcMAC,DstMAC,Port);
                }else{
                    return this.next.broadcastingPorts(SrcMAC,DstMAC,Port);
                }
            }
            return true;
        }

        private String getNode(int i){
            if(LinkList.this.index++==i){
                return this.MAC;
            }else{
                return this.next.getNode(i);
            }
        }

        private void setNode(int i,String MAC){
            if(LinkList.this.index++==i){
                this.MAC=MAC;
            }else{
                this.next.setNode(i,MAC);
            }
        }

        private void setNode(int i,int Port){
            if(LinkList.this.index++==i){
                this.Port=Port;
            }else{
                this.next.setNode(i,Port);
            }
        }

        private void setCountDownTime(int i,int CountDownTime){
            if(LinkList.this.index++==i){
                this.CountDownTime=CountDownTime;
            }else{
                this.next.setCountDownTime(i,CountDownTime);
            }
        }

        /*private void removeNode(Node P, String D){
            if(D.equals(this.MAC)){
                P.next=this.next;
            }else{
                this.next.removeNode(this,D);
            }
        }*/

        private void removeRoot(){
            synchronized(LinkList.this){
                if(root==null){
                    return;
                }else{
                    Node current=root;
                    root=current.next;
                    root.previous=null;
                    --length;
                }
            }
        }

        private void removeTail(){
            synchronized(LinkList.this){
                if(root==null){
                    return;
                }else{
                    Node pre=root;
                    int count=1;
                    while(count<length-1){
                        pre=pre.next;
                        ++count;
                    }
                    pre.next=null;
                    --length;
                }
            }
        }

        private void updateListLength(){
            //Node.this.previous.next=Node.this.next;
            //Node.this.next.previous=Node.this.previous;
            --length;
        }

        private void macToArrayNode(){
            LinkList.this.ArrayMAC[LinkList.this.index++]=this.MAC;
            if(this.next!=null){
                this.next.macToArrayNode();
            }
        }

        private void portToArrayNode(){
            LinkList.this.ArrayPort[LinkList.this.index++]=String.valueOf(this.Port);
            if(this.next!=null){
                this.next.portToArrayNode();
            }
        }

        private void countdowntimeToArrayNode(){
            LinkList.this.ArrayCountDownTime[LinkList.this.index++]=String.valueOf(this.CountDownTime);
            if(this.next!=null){
                this.next.countdowntimeToArrayNode();
            }
        }

        private void allToArrayNode(){
            synchronized(LinkList.this){
                int index=LinkList.this.index++;
                try{
                    LinkList.this.TableArray[index][0]=this.MAC;
                    LinkList.this.TableArray[index][1]=String.valueOf(this.Port);
                    LinkList.this.TableArray[index][2]=String.valueOf(this.CountDownTime);
                }catch (ArrayIndexOutOfBoundsException e){

                }
                if(this.next!=null){
                    this.next.allToArrayNode();
                }
            }
        }

        private void clear(){
            synchronized(LinkList.this){
                root.previous=null;
                root.next=null;
                root=null;
                length=0;
                index=0;
            }
        }

        private void stopAllTimer(){
            timerManager.stopAllTimer();
        }

        private void pauseAllTimer(){
            paused=true;
        }

        private void startAllTimer(){
            paused=false;
        }

        private void editByIndex(int itemIndex,String MAC,int Port,int CountDownTime){
            int k=0;
            synchronized(LinkList.this){
                Node current=root;
                while(k!=itemIndex){
                    if(current.next==null){
                        return;
                    }
                    current=current.next;
                    ++k;
                }
                current.MAC=MAC;
                current.Port=Port;
                current.CountDownTime=CountDownTime;
            }
        }

        private void removeNode(Node current){
            synchronized(LinkList.this){
                if(current.previous==null&current.next==null){
                    deleteMessaging(current.MAC);
                    current.clear();
                }else if(current.previous==null&current.next!=null){
                    deleteMessaging(current.MAC);
                    current.removeRoot();
                }else if(current.previous!=null&current.next==null){
                    deleteMessaging(Node.this.MAC);
                    current.removeTail();
                }else if(current.previous!=null&current.next!=null){
                    deleteMessaging(Node.this.MAC);
                    current.previous.next=current.next;
                    current.next.previous=current.previous;
                    current.updateListLength();
                }
            }

        }

        private void removeNode(String MAC){
            synchronized(LinkList.this){
                Node current=root;
                while(!current.MAC.equals(MAC)){
                    if(current.next==null){
                        return;
                    }
                    current=current.next;
                }
                if(current.previous==null&current.next==null){
                    deleteMessaging(current.MAC);
                    current.clear();
                }else if(current.previous==null&current.next!=null){
                    deleteMessaging(current.MAC);
                    current.removeRoot();
                }else if(current.previous!=null&current.next==null){
                    deleteMessaging(Node.this.MAC);
                    current.removeTail();
                }else if(current.previous!=null&current.next!=null){
                    deleteMessaging(Node.this.MAC);
                    current.previous.next=current.next;
                    current.next.previous=current.previous;
                    current.updateListLength();
                }
                //return;
            }

        }
    }

    private Node root=null;
    private int length=0,index=0;
    private String[] ArrayMAC;
    private String[] ArrayPort;
    private String[] ArrayCountDownTime;
    private String[][] TableArray;


    private final static LinkList instance=new LinkList();
    public static LinkList getInstance(){
        return instance;
    }


    public String generateRandomMAC(){
        Random random=new Random();
        String[] MAC={
                //String.format("%02x",0x52),
                //String.format("%02x",0x54),
                //String.format("%02x",0x00),
                String.format("%02x",random.nextInt(0xff)),
                String.format("%02x",random.nextInt(0xff)),
                String.format("%02x",random.nextInt(0xff)),
                String.format("%02x",random.nextInt(0xff)),
                String.format("%02x",random.nextInt(0xff)),
                String.format("%02x",random.nextInt(0xff))
        };
        return String.join(":",MAC);
    }

    public int generateRandomPort(){
        Random random=new Random();
        return random.nextInt(50)+1;
    }

    public int generateRandomCountTime(){
        Random random=new Random();
        return random.nextInt(10)+1;
    }

    public void randomInit(){
        add(generateRandomMAC(),generateRandomPort(),generateRandomCountTime());
    }

    public void add(String MAC,int Port,int EndTime){
        Node N=new Node(MAC,Port,EndTime);
        if(this.root==null){
            N.next=null;
            N.previous=null;
            this.root=N;
            this.length++;
        }else{
            this.root.createFromTail(N);
        }
        //this.length++;
    }

    public int getLength(){
        return length;
    }

    public boolean isEmpty(){
        return this.length==0;
    }

    public boolean findMAC(String MAC){
        return this.root.containsMAC(MAC);
        /*
        if(root==null){
            return false;
        }else{
            return this.root.containsMAC(MAC);
            /*
            Node current=root;
            while(!current.MAC.equals(MAC)){
                if(current.next==null){
                    return false;
                }
                current=current.next;
            }
            return true;

        }
         */
    }

    public int findMACsPorts(String MAC){
        Node current=root;
        while(!current.MAC.equals(MAC)){
            current=current.next;
        }
        return current.Port;
    }

    public boolean findPort(int Port){
        return this.root.portEquals(Port);
    }

    public void removeAddressFromList(String MAC){
        this.root.removeNode(MAC);
    }

    public String get(int i){
        if(i>this.length){
            return null;
        }
        this.index=0;
        return this.root.getNode(i);
    }

    public void set(int i,String D){
        if(i>this.length||i<1){
            return;
        }
        this.index=0;
        this.root.setNode(i,D);
    }

    public void setPort(int i,int Port){
        if(i>this.length||i<1){
            return;
        }
        this.index=0;
        this.root.setNode(i,Port);
    }

    /*public void remove(String MAC){
        if(this.findMAC(MAC)){
            if(MAC.equals(this.root.MAC)){
                this.root=this.root.next;
            }else{
                this.root.removeNode(this.root,MAC);
            }
            this.length--;
        }
    }*/

    public String[] macToArray(){
        if(this.root==null){
            return null;
        }
        this.index=0;
        this.ArrayMAC=new String[this.length];
        this.root.macToArrayNode();
        return this.ArrayMAC;
    }

    public String[] portToArray(){
        if(this.root==null){
            return null;
        }
        this.index=0;
        this.ArrayPort=new String[this.length];
        this.root.portToArrayNode();
        return this.ArrayPort;
    }

    public String[] countdowntimeToArray(){
        if(this.root==null){
            return null;
        }
        this.index=0;
        this.ArrayCountDownTime=new String[this.length];
        this.root.countdowntimeToArrayNode();
        return this.ArrayCountDownTime;
    }

    public String[][] genTableArray(){

        if(!this.isEmpty()){
            this.index=0;
            this.TableArray=new String[this.length][3];
            //System.out.println("Creating Array of "+this.length);
            this.root.allToArrayNode();
            return this.TableArray;
        }else{
            return null;
        }
        /*
        if(this.root==null){
            return null;
        }
        */

    }

    public void randomPacketEmu(){
        packetEmu(generateRandomMAC(),generateRandomMAC(),generateRandomPort());
        //receiveFrom("xxx",generateRandomMAC(),generateRandomPort());
    }

    public void sendPacket(String SrcMAC,String DstMAC,int DstPort){
        packetEmu(SrcMAC,DstMAC,DstPort);
    }

    public void packetEmu(String SrcMAC,String DstMAC,int DstPort) {
        boolean findSrcMAC = findMAC(SrcMAC);
        boolean findDstMAC = findMAC(DstMAC);
        //boolean findDstPort = this.findPort(DstPort);
        if(findSrcMAC==false){
            add(SrcMAC,DstPort,T);
            messaging("已添加来自"+SrcMAC+"的新记录!");
            //System.out.println(df.format(new Date())+" - 已添加来自"+SrcMAC+"的记录!");
        }else if(findSrcMAC==true){
            int MACsPort=findMACsPorts(SrcMAC);
            if(MACsPort!=DstPort){
                this.root.switching(SrcMAC,DstPort);
                messaging("已将记录"+SrcMAC+"的端口号修改为"+DstPort+"!");
                messaging("已将记录"+SrcMAC+"的倒计时时间重置为"+T+"s(T)!");
            }else if(MACsPort==DstPort){
                this.root.switching(SrcMAC);
                messaging("已将记录"+SrcMAC+"的倒计时时间重置为"+T+"s(T)!");
            }
        }

        if(findDstMAC==false){
            broadcasting(SrcMAC,DstMAC,DstPort);
        }else if(findDstMAC==true){
            int MACsPort=findMACsPorts(SrcMAC);
            if(MACsPort!=DstPort){
                forwarding(SrcMAC,DstMAC,DstPort);
            }else if(MACsPort==DstPort){
                messaging("表中存在"+SrcMAC+":"+DstPort+"对应的记录!");
                messaging("已将来自"+SrcMAC+":"+DstPort+"的数据帧丢弃!");
            }
        }
    }

    public void forwarding(String SrcMAC,String DstMAC,int Port){
        //System.out.println(df.format(new Date())+" - 已将来自"+MAC+"的数据帧从"+Port+"转发出去!");
        messaging("已将来自"+SrcMAC+"的数据帧从"+DstMAC+"的端口"+Port+" 转发出去!");
    }

    public void broadcasting(String SrcMAC,String DstMAC,int Port){
        messaging("当前表中找不到对应地址，准备将该数据帧从除端口"+Port+"以外的端口广播出去!");
        this.root.broadcastingPorts(SrcMAC,DstMAC,Port);
    }

    public void messaging(String S){
        StringBuilder s=new StringBuilder(simpleDateFormat.format(new Date()));
        s.append(" - ");
        s.append(S);
        System.out.println(s.toString());
        //System.out.println(df.format(new Date())+" - "+S);
    }

    public void deleteMessaging(String MAC){
        StringBuilder s=new StringBuilder(simpleDateFormat.format(new Date()));
        s.append(" - ");
        s.append("路由表中mac地址为");
        s.append(MAC);
        s.append("的记录倒计时已结束，已删除该条记录！");
        System.out.println(s.toString());
    }

    public void stopAllTimer(){
        if(!isEmpty()){
            this.root.stopAllTimer();
        }

    }

    public void startAllTimer(){
        if(!isEmpty()){
            this.root.startAllTimer();
        }
    }

    public void pauseAllTimer(){
        paused=true;
        if(!isEmpty()){
            this.root.pauseAllTimer();
        }
    }

    public void emptyList(){
        if(!isEmpty()){
            this.root.stopAllTimer();
            this.root.clear();
        }
    }

    public boolean isPaused(){
        return paused;
    }

    public void editList(int itemIndex,String MAC,int Port,int CountDownTime){
        this.root.editByIndex(itemIndex,MAC,Port,CountDownTime);
    }

}