/**
 * Created by haozewang on 17/2/1.
 */
import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class processor  extends  Thread{
    private InputStream input;
    private PrintStream output;

    private String root = "./www";
    public processor(Socket socket){
        try{
            input = socket.getInputStream();
            output = new PrintStream(socket.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void run(){
        try {
            parse(input);

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void parse(InputStream input) throws  IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String inputcontent = in.readLine();                   // get the header of the HTTP message
        if(inputcontent.length() == 0 || inputcontent == null){   //determine whether this header is qualified
            sendError(403,"Invalid request");
        }
        String request[] = inputcontent.split(" ");
        if(request.length != 3) {                          //determine whether this header is qualified
            sendError(403, "Invalid request");
        }
        else if((request[1].equals("redirect.defs"))){
            sendError(404,"File not find");
        }
        String  redirect_path  = redirect(request[1]);
        if(redirect_path!=null){
            output.print(request[2] + " 301 Move Permanently\r\n");
            output.print("Location: ");
            output.print(redirect_path);
            output.print("\r\n\r\n");
        }
         if(request[0] .equals("GET") ){               // If the request is GET
            File file = new File(root + request[1]);
                if(!file.exists()){                    // If do not have this file. return 404
                sendError(404,"File not find");
            }
            else{                                        // return 200 + data
                InputStream filein = new FileInputStream(file);
                byte content[] = new byte[(int)(file.length())];
                filein.read(content);
                output.print(request[2] + " 200 OK\r\n");
                Date date = new Date();
                output.print("Data: " + date + "\r\n");
                output.print("Server: " + request[2] + "\r\n");
                output.print("Content length: " + content.length +"\r\n" );
                output.print("Content-type: " + getcontenttype(request[1]) + "\r\n\r\n");
                output.write(content);
                output.flush();
                output.close();
                filein.close();
            }
        }
        else if(request[0].equals("HEAD")){            // if the request is HEAD
            File file = new File(root + request[1]);
            if(!file.exists()){                       // If do not have this file. return 404
                sendError(404,"File not find");
            }
            else{                                     // return 200 + data
                InputStream filein = new FileInputStream(file);
                byte content[] = new byte[(int)(file.length())];
                filein.read(content);
                output.print(request[2] + " 200 OK\r\n");
                Date date = new Date();
                output.print("Data: " + date + "\r\n");
                output.print("Server: " + request[2] + "\r\n");
                output.print("Content length: " + content.length +"\r\n" );
                output.print("Content-type: " + getcontenttype(request[1]) + "\r\n\r\n");
                output.close();
                output.flush();
                filein.close();
            }
        }
        else {                                     // invalid request return 403
            sendError(403,"Invalid request");
        }

    }
    public void sendError(int errnum, String errmsg){             // a method used to send error message
        output.print("HTTP/1.1 "+ errnum + " " + errmsg+"\r\n");
        Date data =new Date();
        output.print("Data: " + data + "\r\n");
        output.print("Server: HTTP/1.1\r\n");
        output.print("Content-type: text/html\r\n");
        output.print("<html>\r\n");
        output.print("<head><title>Error" + errnum + "--" + errmsg +"</title></head>\r\n");
       output.print("<h1>" + errnum + " " + errmsg + "<h1>\r\n");
        output.print("</html\r\n");
        output.print("\r\n");
        output.flush();
        output.close();

    }
    public String getcontenttype(String filename){              // get the file type
        if(filename.endsWith(".html") || filename.endsWith(".htm")){
            return "text/html";
        }
        else if(filename.endsWith(".png")){
            return "image/png";
        }
        else if(filename.endsWith(".jpeg")){
            return "image/jpeg";
        }
        else if(filename.endsWith(".pdf")){
            return "application/pdf";
        }
        else {
            return "text/plain";
        }
    }
    public String redirect(String path){                         //determine the redirect path
        try{
            File defs = new File(root + "/redirect.defs");
            Scanner scanner =new Scanner(defs);
            while (scanner.hasNextLine()){
                String temp = scanner.nextLine();
                if(temp.split(" ")[0].equals(path)){
                    return temp.split(" ")[1];
                }
            }
        }catch (IOException e){
             return null;
        }
        return null;
    }
}
