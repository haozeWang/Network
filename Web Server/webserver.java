/**
 * Created by haozewang on 17/2/1.
 */
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class webserver extends Thread {
    private  int port ;
    private ServerSocket serverSocket;
    public webserver(int port){
       this.port = port;
    }
    public void startServer(){
        try {
            serverSocket = new ServerSocket(port);
             System.out.print("Web server start on " + port);
            while (true){
                Socket socket =  serverSocket.accept();
                new processor(socket).start();           // start a new thread
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String args[]){
        String []port = args[0].split("=");          //get the port number
        webserver server =new webserver(Integer.parseInt(port[1]));
        server.startServer();
    }

}
