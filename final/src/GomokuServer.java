import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
public class GomokuServer {
  public static void main(String[] args) {
      int portNumber = 5487;
      int chatPortNumber = 5488;
      int table_num=1;
      Thread t;
      OutputStream out2;
      try {
        ServerSocket serverSocket = new ServerSocket(portNumber);
        ServerSocket serverChatSocket = new ServerSocket(chatPortNumber);
        while(true) {

          System.out.println("Waiting for client1 connection......");
          Socket chatSocket1 = serverChatSocket.accept();
          Socket socket1 = serverSocket.accept();

          System.out.println("Waiting for client2 connection......");
          Socket chatSocket2 = serverChatSocket.accept();
          Socket socket2 = serverSocket.accept();

          try {
            socket1.sendUrgentData(1);
            System.out.println("creating table"+table_num+"......");

            t = new MyServerThread(socket1,socket2);
            Thread chat1_t = new MyServerChatThread(chatSocket1,chatSocket2,table_num);
            Thread chat2_t = new MyServerChatThread(chatSocket2,chatSocket1,table_num);
            t.start();
            chat1_t.start();
            chat2_t.start();

            table_num++;
          }catch (Exception e){
            out2=socket2.getOutputStream();
            out2.write("2".getBytes());
            out2.flush();
          }

        }
      } catch(IOException e) {
        e.printStackTrace();
      }
  }
}

