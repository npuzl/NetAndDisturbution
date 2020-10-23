package exam1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author zl
 * @version 1.0
 * @date 2020/10/16
 * 客户端功能类
 *
 */
public class HttpClientHelper {

    private static final int PORT = 8000;
    private static final int BUFFER_SIZE = 8192;
    private byte[] buffer;
    String host = "127.0.0.1";
    Socket socket = null;
    BufferedOutputStream ostream = null;
    BufferedInputStream istream = null;

    private StringBuffer header;
    private StringBuffer response;
    static private final String CRLF = "\r\n";


    public HttpClientHelper() {
        buffer = new byte[BUFFER_SIZE];
        header = new StringBuffer();
        response = new StringBuffer();
    }


    public void connect(String host) throws Exception {
        this.host = host;
        socket = new Socket(host, PORT);
        ostream = new BufferedOutputStream(socket.getOutputStream());
        istream = new BufferedInputStream(socket.getInputStream());
    }

    public void connect(String host, int port) throws Exception {

        socket = new Socket(host, port);
        ostream = new BufferedOutputStream(socket.getOutputStream());
        istream = new BufferedInputStream(socket.getInputStream());
    }

    /**
     * @param request
     * @throws Exception
     */
    public void processGetRequest(String request) throws Exception {
        if (!request.contains("Host:")) {
            request += CRLF;
            request += "Host: " + processHost(request) + CRLF;
            request += "From: 127.0.0.1" + CRLF;
            request += "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36" + CRLF;
            request += CRLF + CRLF;
        }
        buffer = request.getBytes();
        ostream.write(buffer, 0, request.length());
        ostream.flush();
        processResponse();
    }

    /**
     * @param request
     * @return
     */
    public String processHost(String request) {
        String target = request.split(" ")[1];
        if (target.startsWith("/")) {
            return host;
        } else {
            return target.split("/")[2];
        }
    }

    /**
     * @throws Exception
     */
    public void processResponse() throws Exception {
        int last = 0, c = 0;
        boolean inHeader = true;
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
            }
        }


        while (istream.read(buffer) != -1) {
            response.append(new String(buffer, StandardCharsets.ISO_8859_1));
            buffer = new byte[BUFFER_SIZE];
            //System.out.println(response.toString());
            if (istream.available() == 0)
                break;
        }
    }

    /**
     * @return
     */
    public String getHeader() {
        return header.toString();
    }


    public String getResponse() {
        return response.toString();
    }


    public void close() throws Exception {
        socket.close();
        istream.close();
        ostream.close();
    }
}
