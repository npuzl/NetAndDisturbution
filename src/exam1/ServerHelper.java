package exam1;

import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 * @author zl
 * @version 1.0
 * @date 2020/10/16
 */
public class ServerHelper implements Runnable {
    private final String CRLF = "\r\n";
    private Socket socket;
    private static String rootPath = "C:/ProxyTest/Server/"; // Root path
    /**
     * Output stream to the socket.
     */
    BufferedOutputStream ostream = null;

    /**
     * Input stream from the socket.
     */
    BufferedInputStream istream = null;

    public ServerHelper(Socket socket) {
        this.socket = socket;
    }

    /**
     * 初始化流
     */
    public void initStream() {
        try {
            ostream = new BufferedOutputStream(socket.getOutputStream());
            istream = new BufferedInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 主要处理函数
     */
    public void run() {
        try {
            initStream();
            StringBuffer header = new StringBuffer();
            int last = 0, c = 0;
            boolean inHeader = true; // loop control
            while (inHeader && ((c = istream.read()) != -1)) {
                switch (c) {
                    case '\r':
                        break;
                    case '\n':
                        if (c == last) {
                            inHeader = false;
                            break;
                        }
                        last = c;
                        header.append("\n");
                        break;
                    default:
                        last = c;
                        header.append((char) c);
                        //System.out.print((char) c);
                }
            }
            String head = new String(header);
            System.out.println("客户端请求为：\n" + head);
            String target = head.split("\n")[0];
            String filename = rootPath + target.split("/")[3].split(" ")[0];
            System.out.println(filename);
            File file = new File(filename);
            if (!file.exists()) {
                String response = "";
                response += "404 BadRequest" + CRLF + CRLF;
                ostream.write(response.getBytes(), 0, response.length());
                ostream.flush();
            } else {
                HtmlResponse(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void HtmlResponse(File file) {
        try {
            StringBuilder response = new StringBuilder();
            StringBuilder html = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getPath()));
            String str = bufferedReader.readLine();
            while (str != null) {
                html.append(str).append("\n");
                str = bufferedReader.readLine();
            }
            response.append("HTTP/1.1 200 OK" + CRLF);
            response.append("Date: ").append(new Date().toString()).append(CRLF);
            response.append("Content-Type: text/html;charset=ISO-8859-1" + CRLF);
            response.append("Content-Length: ").append(file.length()).append(CRLF);
            response.append(CRLF);
            response.append(html);
            String message = response.toString();
            byte[] buffer = message.getBytes();
            ostream.write(buffer, 0, message.length());
            ostream.flush();
            ostream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}