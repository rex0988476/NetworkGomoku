import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.sound.sampled.*;
import javax.swing.*;
import static java.lang.Thread.sleep;

public class GomokuClient extends JFrame implements ActionListener{
    private static final int FRAME_WIDTH    = 885;
    private static final int FRAME_HEIGHT   = 661;
    private static final int FRAME_X_ORIGIN = 10;
    private static final int FRAME_Y_ORIGIN = 10;

    //
    private String endS="leave__________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________";
    private JMenuItem clearChatItem;
    private JMenuItem clearMsgItem;
    private char[][] gameTable = new char[15][15];
    private ImageIcon tenIcon;
    private ImageIcon whiteIcon;
    private ImageIcon blackIcon;
    private ImageIcon myTurnIcon;
    private ImageIcon oppoIcon;
    private String mySign;
    private String oppoSign;
    private JButton[][] tableButton = new JButton[15][15];
    private JTextField inputLine; // The JTextField for the user to enter a text
    private JTextArea  chatArea; //The JTextArea for displaying the entered text
    private JScrollPane scrollChatArea;
    private JTextArea  msgArea;
    private JScrollPane scrollMsgArea;
    private JButton sendButton;
    private JButton resetGameButton;
    private JButton leaveGameButton;
    private JButton tieButton;
    private JButton surrenderButton;
    private static final String ADDRESS = "127.0.0.1";//ip
    private static final int PORT = 5487;//port
    private static final int CHATPORT = 5488;
    private Socket client;
    private Socket clientChat;
    private byte[] buffer;
    private byte[] chatBuffer;
    private byte[] oneBuffer;
    private byte[] iBuffer;
    private byte[] jBuffer;
    private String sbuffer;
    private InputStream in;
    private OutputStream out;
    private InputStream chatIn;
    private OutputStream chatOut;
    private boolean is_myturn;
    private int width=40,height=40;
    private Container contentPane;
    private GomokuClient GC;
    private boolean isReset;
    private boolean oppoReset;
    private boolean oppoLeave;
    private Dialog tieConfirmDialog = new Dialog(new JFrame(), "tie?");
    private JButton yesButton;
    private JButton noButton;
    private File soundClickFile = new File("sound/click.wav");
    private File soundBGMFile = new File("sound/bgm.wav");
    public GomokuClient(String title) {
      super(title);
      //set the frame properties
      this.setSize      (FRAME_WIDTH, FRAME_HEIGHT);
      this.setResizable (false);
      this.setLocation  (FRAME_X_ORIGIN, FRAME_Y_ORIGIN);
      GC=this;

      Container contentPane = getContentPane( );
      contentPane.setLayout(null);

      ImageIcon tIcon;
      tIcon = new ImageIcon("img/ten.png");
      tenIcon = tIcon;

      ImageIcon wIcon;
      wIcon = new ImageIcon("img/white.png");
      whiteIcon = wIcon;

      ImageIcon bIcon;
      bIcon = new ImageIcon("img/black.png");
      blackIcon = bIcon;

      chatArea = new JTextArea();
      chatArea.setBounds(600,0,270,280);
      chatArea.setEditable(false);
      scrollChatArea = new JScrollPane(chatArea);
      scrollChatArea.setBounds(600,0,270,280);
      contentPane.add(scrollChatArea);

		  inputLine = new JTextField();
		  inputLine.setBounds(600,280,200,height);
      inputLine.setEnabled(false);
		  contentPane.add(inputLine);

      sendButton = new JButton("send");
      sendButton.setBounds(800,280,70,height);
      sendButton.addActionListener(this);
      sendButton.setEnabled(false);
      contentPane.add(sendButton);

      msgArea = new JTextArea();
      msgArea.setBounds(600,320,270,200);
      msgArea.setEditable(false);
      scrollMsgArea = new JScrollPane(msgArea);
      scrollMsgArea.setBounds(600,320,270,200);
      contentPane.add(scrollMsgArea);

      surrenderButton = new JButton("surrender");
      surrenderButton.setBounds(600,520,135,height);
      surrenderButton.addActionListener(this);
      surrenderButton.setEnabled(false);
      contentPane.add(surrenderButton);

      tieButton = new JButton("tie");
      tieButton.setBounds(735,520,135,height);
      tieButton.addActionListener(this);
      tieButton.setEnabled(false);
      contentPane.add(tieButton);

      resetGameButton = new JButton("reset game");
      resetGameButton.setBounds(600,560,135,height);
      resetGameButton.addActionListener(this);
      resetGameButton.setEnabled(false);
      contentPane.add(resetGameButton);

      leaveGameButton = new JButton("leave game");
      leaveGameButton.setBounds(735,560,135,height);
      leaveGameButton.addActionListener(this);
      leaveGameButton.setEnabled(false);
      contentPane.add(leaveGameButton);

      for(int i=0;i<15;i++){
        for(int j=0;j<15;j++){
          tableButton[i][j] = new JButton();
          tableButton[i][j].setIcon(tenIcon);
          tableButton[i][j].setBounds(j*width,i*height,width,height);
          tableButton[i][j].addActionListener(this);
          contentPane.add(tableButton[i][j]);
        }
      }

      JMenuBar menuBar = new JMenuBar();
      this.setJMenuBar(menuBar);
      JMenu funcMenu = new JMenu("Menu");

      clearChatItem = new JMenuItem("clear chat room");
      clearChatItem.addActionListener(this);
      funcMenu.add(clearChatItem);
      menuBar.add(funcMenu);

      clearMsgItem = new JMenuItem("clear msg box");
      clearMsgItem.addActionListener(this);
      funcMenu.add(clearMsgItem);
      menuBar.add(funcMenu);

      this.setVisible(true);
      this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

      try{
        clientChat = new Socket(ADDRESS,CHATPORT);
        chatOut = clientChat.getOutputStream();
        chatIn = clientChat.getInputStream();
        client = new Socket(ADDRESS, PORT);
        out = client.getOutputStream();
        in = client.getInputStream();
        Thread chat_t = new MyClientThread(chatArea,clientChat);
        chat_t.start();
      }catch (Exception e){
        e.printStackTrace();
      }
      play();
      System.out.println("play fin");
    }

    public void play(){

      while(true) {
        init();
        while (true) {
          try {
            buffer = new byte[1];
            int indexI = 0, indexJ = 0;
            msgArea.append("Waiting another player...\n");
            //turn
            try {
              in.read(buffer);
            }catch (Exception e){
              msgArea.append("Server error, click leave game button to leave...\n");
              for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                  tableButton[i][j].setEnabled(false);
                }
              }
              inputLine.setEditable(false);
              sendButton.setEnabled(false);
              surrenderButton.setEnabled(false);
              tieButton.setEnabled(false);
              resetGameButton.setEnabled(false);
              leaveGameButton.setEnabled(true);
              sleep(Integer.MAX_VALUE);
              System.exit(1);
            }

            sbuffer = new String(buffer);
            System.out.println(sbuffer);
            if (sbuffer.equals("0")) {
              is_myturn = true;
              surrenderButton.setEnabled(true);
              tieButton.setEnabled(true);
              msgArea.append("Game Start, You First(Black)\n");
              myTurnIcon = blackIcon;
              oppoIcon = whiteIcon;
              mySign = "B";
              oppoSign = "W";
            } else if (sbuffer.equals("1")) {
              is_myturn = false;
              surrenderButton.setEnabled(false);
              tieButton.setEnabled(false);
              msgArea.append("Game Start, You After(White)\n");
              myTurnIcon = whiteIcon;
              oppoIcon = blackIcon;
              mySign = "W";
              oppoSign = "B";
            }
            else if(sbuffer.equals("2")){//server error
              msgArea.append("Player1 leave, click leave game button to leave...\n");
              leaveGameButton.setEnabled(true);
              sleep(Integer.MAX_VALUE);
              System.exit(1);
            }
            Thread bgmSoundT = new MyClientSoundThread(soundBGMFile,true,10000);
            bgmSoundT.start();
            inputLine.setEnabled(true);
            sendButton.setEnabled(true);

            //read
            buffer = new byte[4];
            chatBuffer = new byte[255];
            oneBuffer = new byte[1];
            System.out.println("start play");
            while (true) {
              in.read(buffer);
              sbuffer = new String(buffer);
              buffer = new byte[4];
              System.out.println("recv: " + sbuffer);
              if(sbuffer.equals("LT__")){//live three
                msgArea.append("Live three warning!\n");
              }
              if(sbuffer.equals("DF__")){//dead four
                msgArea.append("Dead four warning!\n");
              }
              if(sbuffer.equals("DLT_")){//double live three
                msgArea.append("Double live three warning!\n");
              }
              if(sbuffer.equals("surr")){
                msgArea.append("Opponent surrender.\n");
                msgArea.append("You Win !!!\n");
                msgArea.append("Game Over.\n");
                break;
              }
              if(sbuffer.equals("SURR")){
                msgArea.append("You surrender.\n");
                msgArea.append("You Lose...\n");
                msgArea.append("Game Over.\n");
                break;
              }
              if(sbuffer.equals("ctie")){
                openTieDialog();
              }
              if(sbuffer.equals("no__")){
                for (int i = 0; i < 15; i++) {
                  for (int j = 0; j < 15; j++) {
                    tableButton[i][j].setEnabled(true);
                  }
                }
                surrenderButton.setEnabled(true);
                msgArea.append("Opponent say no, Is Your Turn.\n");
              }
              if(sbuffer.equals("plaE")){
                msgArea.append("Opponent crash, click leave game button to leave.");
                for (int i = 0; i < 15; i++) {
                  for (int j = 0; j < 15; j++) {
                    tableButton[i][j].setEnabled(false);
                  }
                }
                inputLine.setEditable(false);
                sendButton.setEnabled(false);
                surrenderButton.setEnabled(false);
                tieButton.setEnabled(false);
                resetGameButton.setEnabled(false);
                leaveGameButton.setEnabled(true);
                sleep(Integer.MAX_VALUE);
                System.exit(1);
              }
              //play
              if (sbuffer.equals("play")) {
                //i
                in.read(oneBuffer);
                sbuffer = new String(oneBuffer);
                int lenI = Integer.parseInt(sbuffer);
                if (lenI == 1) {
                  iBuffer = new byte[1];
                } else if (lenI == 2) {
                  iBuffer = new byte[2];
                }
                in.read(iBuffer);
                sbuffer = new String(iBuffer);
                indexI = Integer.parseInt(sbuffer);

                //j
                in.read(oneBuffer);
                sbuffer = new String(oneBuffer);
                int lenJ = Integer.parseInt(sbuffer);
                if (lenJ == 1) {
                  jBuffer = new byte[1];
                } else if (lenJ == 2) {
                  jBuffer = new byte[2];
                }
                in.read(jBuffer);
                sbuffer = new String(jBuffer);
                indexJ = Integer.parseInt(sbuffer);

                //add chess
                tableButton[indexI][indexJ].setIcon(oppoIcon);
                //tableButton[indexI][indexJ].setText(oppoSign);
                gameTable[indexI][indexJ] = oppoSign.toCharArray()[0];

                System.out.println(indexI);
                System.out.println(indexJ);
                System.out.println();
                is_myturn = true;
                surrenderButton.setEnabled(true);
                tieButton.setEnabled(true);
                msgArea.append("Is Your Turn.\n");
              } else if (sbuffer.equals("YWIN")) {
                msgArea.append("You Win !!!\n");
                msgArea.append("Game Over.\n");
                break;
              } else if (sbuffer.equals("OWIN")) {
                msgArea.append("You Lose...\n");
                msgArea.append("Game Over.\n");
                break;
              } else if (sbuffer.equals("TIE_")) {
                msgArea.append("Tie.\n");
                msgArea.append("Game Over.\n");
                break;
              }
            }
            is_myturn = false;
            for (int i = 0; i < 15; i++) {
              for (int j = 0; j < 15; j++) {
                tableButton[i][j].setEnabled(false);
              }
            }
            msgArea.append("Click reset game button to play again,\n");
            msgArea.append("or click leave game button to leave.\n");
            resetGameButton.setEnabled(true);
            leaveGameButton.setEnabled(true);
            surrenderButton.setEnabled(false);
            tieButton.setEnabled(false);
            //reset game
            System.out.println("end game start");
            buffer = new byte[5];
            in.read(buffer);
            sbuffer = new String(buffer);
            System.out.println("reset game recv: "+sbuffer);
            if(sbuffer.equals("endE_")){
              msgArea.append("Opponent crash, click leave game button to leave.");
              for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                  tableButton[i][j].setEnabled(false);
                }
              }
              inputLine.setEditable(false);
              sendButton.setEnabled(false);
              surrenderButton.setEnabled(false);
              tieButton.setEnabled(false);
              resetGameButton.setEnabled(false);
              leaveGameButton.setEnabled(true);
              sleep(Integer.MAX_VALUE);
              System.exit(1);
            }
            if (sbuffer.equals("leave")) {
              resetGameButton.setEnabled(false);
              inputLine.setEnabled(false);
              sendButton.setEnabled(false);
              msgArea.append("Opponent leave, click leave button to leave.\n");
              oppoLeave = true;
              //recv leave
              System.out.println("wait LEAVE");
              buffer = new byte[5];
              in.read(buffer);
              sbuffer = new String(buffer);
              System.out.println("reset game recv: "+sbuffer);
              if(sbuffer.equals("LEAVE")){
                in.close();
                out.close();
                client.close();
                chatIn.close();
                chatOut.close();
                clientChat.close();

                System.exit(1);
              }
              if(sbuffer.equals("endE_")){
                msgArea.append("Opponent crash, click leave game button to leave.");
                for (int i = 0; i < 15; i++) {
                  for (int j = 0; j < 15; j++) {
                    tableButton[i][j].setEnabled(false);
                  }
                }
                inputLine.setEditable(false);
                sendButton.setEnabled(false);
                surrenderButton.setEnabled(false);
                tieButton.setEnabled(false);
                resetGameButton.setEnabled(false);
                leaveGameButton.setEnabled(true);
                sleep(Integer.MAX_VALUE);
                System.exit(1);
              }
            }
            else if(sbuffer.equals("LEAVE")){//self
              in.close();
              out.close();
              client.close();
              chatIn.close();
              chatOut.close();
              clientChat.close();

              System.exit(1);
            }
            else if (sbuffer.equals("reset")) {
              System.out.println("reset in");
              oppoReset = true;
              msgArea.append("Opponent reset, click reset button to play again.\n");
              msgArea.append("or click leave game button to leave.\n");
              //recv reset,leave
              System.out.println("wait RESET or LEAVE");
              buffer = new byte[5];
              in.read(buffer);
              sbuffer = new String(buffer);
              System.out.println("reset game recv: "+sbuffer);
              if(sbuffer.equals("LEAVE")){
                in.close();
                out.close();
                client.close();
                chatIn.close();
                chatOut.close();
                clientChat.close();

                System.exit(1);
              }
              else if(sbuffer.equals("RESET")){
                System.out.println("recv RESET in");
              }
              if(sbuffer.equals("endE_")){
                msgArea.append("Opponent crash, click leave game button to leave.");
                for (int i = 0; i < 15; i++) {
                  for (int j = 0; j < 15; j++) {
                    tableButton[i][j].setEnabled(false);
                  }
                }
                inputLine.setEditable(false);
                sendButton.setEnabled(false);
                surrenderButton.setEnabled(false);
                tieButton.setEnabled(false);
                resetGameButton.setEnabled(false);
                leaveGameButton.setEnabled(true);
                sleep(Integer.MAX_VALUE);
                System.exit(1);
              }
            }
            else if(sbuffer.equals("RESET")){

              System.out.println("recv RESET out");
              //recv reset,leave
              System.out.println("wait reset or leave");
              buffer = new byte[5];
              in.read(buffer);
              sbuffer = new String(buffer);
              System.out.println("reset game recv: "+sbuffer);
              if(sbuffer.equals("leave")){
                resetGameButton.setEnabled(false);
                inputLine.setEnabled(false);
                sendButton.setEnabled(false);
                msgArea.append("Opponent leave, click leave button to leave.\n");
                System.out.println("Opponent leave, click leave button to leave.");
                oppoLeave = true;
                /*
                in.close();
                out.close();
                client.close();
                chatIn.close();
                chatOut.close();
                clientChat.close();

                System.exit(1);
                 */
              }
              else if(sbuffer.equals("reset")){
                System.out.println("recv RESET in");
                oppoReset = true;
              }
              if(sbuffer.equals("endE_")){
                msgArea.append("Opponent crash, click leave game button to leave.");
                for (int i = 0; i < 15; i++) {
                  for (int j = 0; j < 15; j++) {
                    tableButton[i][j].setEnabled(false);
                  }
                }
                inputLine.setEditable(false);
                sendButton.setEnabled(false);
                surrenderButton.setEnabled(false);
                tieButton.setEnabled(false);
                resetGameButton.setEnabled(false);
                leaveGameButton.setEnabled(true);
                sleep(Integer.MAX_VALUE);
                System.exit(1);
              }
            }
            System.out.println(isReset);
            System.out.println(oppoReset);
            break;
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        if(oppoLeave){
          break;
        }
        System.out.println("while isReset in");
        while(!isReset || !oppoReset){//poo
          //sleep(1000);
        }
        System.out.println("while isReset out");
      }
    }
    public void init(){
      System.out.println("init start");
      chatArea.setText("Chat Room:\n");
      msgArea.setText("Message Box:\n");
      isReset=false;
      oppoReset=false;
      oppoLeave=false;
      for(int i=0;i<15;i++){
        for(int j=0;j<15;j++){
          gameTable[i][j]=' ';
          tableButton[i][j].setEnabled(true);
          tableButton[i][j].setText("");
          tableButton[i][j].setIcon(tenIcon);
        }
      }
      resetGameButton.setEnabled(false);
      leaveGameButton.setEnabled(false);
      System.out.println("init fin");
    }

    public static void main(String arg[]) {
      new GomokuClient("D0957782_lab_fianl");
    }

    public void actionPerformed(ActionEvent event) {
      System.out.println(event.getSource());

      //send button
      if(event.getSource() == sendButton && !inputLine.getText().equals("")){
        if(inputLine.getText().matches("[a-zA-Z0-9]*")) {
          chatArea.append("Me: " + inputLine.getText() + "\n");
          try {
            chatOut.write(inputLine.getText().getBytes());
            chatOut.flush();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        else {
          msgArea.append("Invalid input.\n");
        }
        inputLine.setText("");
      }
      //table button
      for(int i=0;i<15;i++){
        for(int j=0;j<15;j++){
          if(event.getSource() == tableButton[i][j]){
            System.out.println(getX()+","+getY());
            MyClientSoundThread clickSoundT = new MyClientSoundThread(soundClickFile,false,200);
            clickSoundT.start();
            if(is_myturn) {
              if(gameTable[i][j]==' '){
                tableButton[i][j].setIcon(myTurnIcon);
                gameTable[i][j]=mySign.toCharArray()[0];
                try {//send
                  out.write("play".getBytes());
                  out.flush();
                  sleep(100);
                  if(i<10) {
                    out.write("1".getBytes());
                    out.flush();
                    chatOut.flush();
                  }
                  else{
                    out.write("2".getBytes());
                    out.flush();
                    chatOut.flush();
                  }
                  sleep(100);
                  out.write(Integer.toString(i).getBytes());
                  out.flush();
                  chatOut.flush();
                  sleep(100);
                  if(j<10){
                    out.write("1".getBytes());
                    out.flush();
                    chatOut.flush();
                  }
                  else {
                    out.write("2".getBytes());
                    out.flush();
                    chatOut.flush();
                  }
                  sleep(100);
                  out.write(Integer.toString(j).getBytes());
                  out.flush();
                  chatOut.flush();
                }catch (Exception e){
                  e.printStackTrace();
                }
                is_myturn=false;
                surrenderButton.setEnabled(false);
                tieButton.setEnabled(false);
                msgArea.append("Opponent's Turn, waiting...\n");
              }
              else{
                msgArea.append("Can't add chess to this position !\n");
              }
            }
            else {
             msgArea.append("Not your turn !\n");
            }
          }
        }
      }
      if(event.getSource() == clearChatItem){
        chatArea.setText("Chat Room:\n");
      }
      if(event.getSource() == clearMsgItem){
        msgArea.setText("Message Box:\n");
      }
      if(event.getSource()==surrenderButton){
        surrenderButton.setEnabled(false);
        try {
          out.write("surr".getBytes());
          out.flush();
        }catch (Exception e){
          e.printStackTrace();
        }
      }
      if(event.getSource()==resetGameButton){
        isReset=true;
        resetGameButton.setEnabled(false);
        msgArea.append("You reset, waiting opponent reply...\n");
        try {
          out.write("reset".getBytes());
          out.flush();
          System.out.println("resetGameButton write reset");
        }catch (Exception e){
          e.printStackTrace();
        }
      }
      if(event.getSource()==leaveGameButton){
        try {
          //if(!oppoLeave) {
            out.write("leave".getBytes());
            out.flush();
            chatOut.write(endS.getBytes());
            chatOut.flush();
          //}
          in.close();
          out.close();
          client.close();
          chatIn.close();
          chatOut.close();
          clientChat.close();

          System.exit(1);
        }catch (Exception e){
          e.printStackTrace();
          System.exit(1);
        }
      }
      if(event.getSource()==tieButton){
        tieButton.setEnabled(false);
        try {
          out.write("tie_".getBytes());
          out.flush();
        }catch (Exception e){
          e.printStackTrace();
        }
          for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
              tableButton[i][j].setEnabled(false);
            }
          }
          surrenderButton.setEnabled(false);
        msgArea.append("You choose tie, wait opponent reply...\n");
      }
      if(event.getSource()==yesButton){
        try {
          out.write("yes_".getBytes());
          out.flush();
        }catch (Exception e){
          e.printStackTrace();
        }
        tieConfirmDialog.setVisible(false);
      }
      if(event.getSource()==noButton){
        try {
          out.write("no__".getBytes());
          out.flush();
        }catch (Exception e){
          e.printStackTrace();
        }
        tieConfirmDialog.setVisible(false);
      }
      /////////////////////
    }
    public void openTieDialog(){
      tieConfirmDialog = new Dialog(new JFrame(), "tie?");
      tieConfirmDialog.setSize(330, 200);
      tieConfirmDialog.setResizable (false);
      int nowX=getX(),nowY=getY();
      int start_x=nowX+(FRAME_WIDTH-330)/2,start_y=nowY+(FRAME_HEIGHT-200)/2;
      if(start_x<=1000 && start_y<=600) {
        tieConfirmDialog.setLocation(start_x, start_y);
      }else{
        tieConfirmDialog.setLocation(FRAME_WIDTH, FRAME_HEIGHT);
      }
      tieConfirmDialog.setLayout(null);

      JLabel title = new JLabel("Opponent choose tie, you tie ?");
      title.setBounds(85,50,180,30);
      tieConfirmDialog.add(title);

      yesButton = new JButton("yes");
      yesButton.setBounds(50,120,100,30);
      yesButton.addActionListener(this);
      tieConfirmDialog.add(yesButton);

      noButton = new JButton("no");
      noButton.setBounds(180,120,100,30);
      noButton.addActionListener(this);
      tieConfirmDialog.add(noButton);

      //tieConfirmDialog.setUndecorated(true);
      tieConfirmDialog.setVisible(true);
    }
  }



