package exe2;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 服务器功能实现类
 * @version 3.0
 * @author zl
 * @date 2020/10/22
 */
public class ServerHelper implements Runnable {
    private static final String CRLF = "\r\n";
    private static final int BUFFER_SIZE = 8 * 1024;
    /**
     * Output stream to the socket.
     */
    BufferedOutputStream ostream = null;

    /**
     * Input stream from the socket.
     */
    BufferedInputStream istream = null;
    public Socket socket;
    /**
     * 服务器的根目录地址 C:/../Desktop
     */
    String path;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String remote;
    String Remote;

    public void initStream() {
        try {
            /**
             * Create the output stream. 发送
             */
            ostream = new BufferedOutputStream(socket.getOutputStream());

            /**
             * Create the input stream.  接收
             */
            istream = new BufferedInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param socket
     * @param path   为服务器目录
     */
    ServerHelper(Socket socket, String path) {
        this.socket = socket;
        this.path = path;

        remote = socket.getInetAddress() + ":" + socket.getPort() + ">>";
        Remote = socket.getInetAddress() + ":" + socket.getPort() + " ";
    }

    @Override
    public void run() {
        try {
            initStream();
            String info;
            info = SenderAndReceiver.receiveHeader(istream);
            assert info != null;
            String[] req = info.split(" ", 2);
            switch (req[0]) {
                case "GET" -> {
                    System.out.println(df.format(new Date()) + "|| " + remote + info.split("\n")[0]);
                    ProcessGetRequest(info);
                }
                case "PUT" -> {
                    System.out.println(df.format(new Date()) + "|| " + remote + info.split("\n")[0]);
                    ProcessPutRequest(info);
                }
                default -> {
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ProcessGetRequest(String request) {

        String requestPath = request.split(" ", 3)[1];
        //如果不是以/开头，则加上/   为了避免GET a.txt HTTP/1.1
        if (!requestPath.startsWith("/"))
            requestPath = "/" + requestPath;
        //请求的文件的路径
        String filePath = path + requestPath;

        File file = new File(filePath);

        try {


            //当请求400
            if (!request.split(" ", 4)[2].contains("HTTP/1.1") &&
                    !request.split(" ", 4)[2].contains("HTTP/1.0")) {
                try {
                    String response = "HTTP/1.1 400 Bad Request" + CRLF;
                    response += "Content-Length: 183" + CRLF;
                    response += "Content-Type: text/html; charset=iso-8859-1" + CRLF;
                    response += "Data: " + new Date().toString() + CRLF;
                    response += CRLF;

                    ostream.write(response.getBytes(), 0, response.length());
                    ostream.flush();
                    File file400 = new File("C:\\Users\\zl\\Desktop\\400.html");
                    if (SenderAndReceiver.sendFile(ostream, file400)) {
                        System.out.println(df.format(new Date()) + "|| " + "400 Page send to " + Remote + "success!");
                    }
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //当404
            if (!file.exists()) {
                try {
                    String response = "HTTP/1.1 404 Not Found" + CRLF;
                    response += "Content-Length: 199" + CRLF;
                    response += "Content-Type: text/html; charset=iso-8859-1" + CRLF;
                    response += "Data: " + new Date().toString() + CRLF;
                    response += CRLF;

                    ostream.write(response.getBytes(), 0, response.length());
                    ostream.flush();
                    File file404 = new File("C:\\Users\\zl\\Desktop\\404.html");
                    if (SenderAndReceiver.sendFile(ostream, file404)) {
                        System.out.println(df.format(new Date()) + "|| " + "404Page send to " + Remote + "success!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //正常

                String fileType = SenderAndReceiver.getContentType(requestPath);
                String response = "";
                response += "HTTP/1.1 200 OK" + CRLF;
                response += "Data: " + new Date().toString() + CRLF;
                response += "Content-Type: " + fileType + ";charset=ISO-8859-1" + CRLF;
                response += "Content-Length: " + file.length() + CRLF;
                response += CRLF;
                ostream.write(response.getBytes(), 0, response.length());
                ostream.flush();

                if (SenderAndReceiver.sendFile(ostream, file)) {
                    System.out.println(df.format(new Date()) + "|| " + file.getPath() + " send to " + Remote + "success!");
                }
                run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ProcessPutRequest(String request) {
        try {

            if (!request.contains("HTTP/1.1") && !request.contains("HTTP/1.0")) {

                String response = "HTTP/1.1 400 Bad Request" + CRLF;
                response += "Content-Length: 183" + CRLF;
                response += "Content-Type: text/html; charset=iso-8859-1" + CRLF;
                response += "Data: " + new Date().toString() + CRLF;
                response += CRLF;
                try {
                    ostream.write(response.getBytes(), 0, response.length());
                    //发送400
                    ostream.flush();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //fileName 长这样 /test/asd/ss.txt
            String fileName = request.split(" ", 3)[1];
            //tempFile初始和path一样
            StringBuilder tempPath = new StringBuilder(path);
            String[] filePath = fileName.split("/");
            //到达底层之前
            for (int i = 0; i < filePath.length - 1; i++) {
                File f = new File(tempPath.toString() + "/" + filePath[i]);
                tempPath.append("/").append(filePath[i]);
                boolean b = f.mkdirs();
            }
            //创建文件
            File file = new File(tempPath.toString() + "/" + filePath[filePath.length - 1]);
            boolean b = file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            //System.out.println(request);
            ArrayList<byte[]> fileBytes = SenderAndReceiver.receiveFile(istream, request);
            assert fileBytes != null;

            for (byte[] f : fileBytes) {
                fos.write(f);
                fos.flush();
            }

            System.out.println(df.format(new Date()) + "|| " + "file from " + Remote + " received success!" + "And saved in "
                    + tempPath.toString() + "/" + filePath[filePath.length - 1]);

            String response = "HTTP/1.1 200 OK"+CRLF+CRLF;
            ostream.write(response.getBytes(), 0, response.length());
            ostream.flush();

            run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
