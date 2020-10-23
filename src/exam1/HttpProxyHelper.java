package exam1;

import java.io.*;
import java.net.Socket;

/**
 * @author zl
 * @version 1.0
 * @date 2020/10/16
 */
public class HttpProxyHelper implements Runnable {

    /**
     * Output stream to the socket.
     */
    BufferedOutputStream ostream = null;

    /**
     * Input stream from the socket.
     */
    BufferedInputStream istream = null;


    private Socket socket;
    private static String rootPath = "C:/ProxyTest/proxyServer";

    private static final int BUFFER_SIZE = 8 * 1024;
    private static final String CRLF = "\r\n";
    static PrintWriter screen = new PrintWriter(System.out, true);


    public HttpProxyHelper(Socket socket) {
        this.socket = socket;
    }

    public void initStream() {
        try {
            ostream = new BufferedOutputStream(socket.getOutputStream());
            istream = new BufferedInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 执行
     */
    public void run() {
        try {
            System.out.println("Socket address and port is:" + socket.getInetAddress() + ":" + socket.getPort());

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
            String[] heads = head.split("\n");
            String newHead = "";

            System.out.println("Client request：\n" + head);
            newHead = heads[0] + CRLF + heads[1] + CRLF + "From: " + socket.getInetAddress() + ":"
                    + socket.getPort() + CRLF + heads[3] + CRLF + CRLF;
            System.out.println("New head：\n" + newHead);

            int targetPort;
            String requestURL = head.split("\n")[0].split(" ")[1];//http://localhost/asdasd
            if (requestURL.startsWith("/")) {
                targetPort = 80;
            } else {
                String[] temp = requestURL.split("/")[2].split(":");
                if (temp.length == 1)
                    targetPort = 80;
                else {
                    targetPort = Integer.parseInt(temp[1]);
                }
            }

            HttpClientHelper httpClientHelper = new HttpClientHelper();
            httpClientHelper.connect(head.split("\n")[1].split(" ")[1].split(":")[0], targetPort);
            System.out.println("connect " + head.split("\n")[1].split(" ")[1].split(":")[0]
                    + ":" + targetPort + " success!");
            httpClientHelper.processGetRequest(newHead);
            System.out.println(httpClientHelper.getResponse());
            ostream.write(httpClientHelper.getResponse().getBytes(), 0, httpClientHelper.getResponse().length());
            ostream.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                istream.close();
                ostream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
