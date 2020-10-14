package exe2;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static java.lang.Thread.sleep;

/**
 * Class <em>HttpClient</em> is a class representing a simple HTTP client.
 *
 * @author wben, zl
 * @version 1.0
 * @date 2020/10/09
 */

public class HttpClient {

    /**
     * default HTTP port is port 80
     */
    private static final int port = 80;

    /**
     * Allow a maximum buffer size of 8192 bytes
     */
    private static final int buffer_size = 8192;

    /**
     * Response is stored in a byte array.
     */
    private byte[] buffer;

    /**
     * My socket to the world.
     */
    Socket socket = null;

    /**
     * Default port is 80.
     */
    private static final int PORT = 80;

    /**
     * Output stream to the socket.
     */
    BufferedOutputStream ostream = null;

    /**
     * Input stream from the socket.
     */
    BufferedInputStream istream = null;

    /**
     * StringBuffer storing the header
     */
    private StringBuffer header = null;

    /**
     * StringBuffer storing the response.
     */
    private StringBuffer response = null;
    BufferedReader br;
    /**
     * String to represent the Carriage Return and Line Feed character sequence.
     */
    static private final String CRLF = "\r\n";

    /**
     * HttpClient constructor;
     */
    public HttpClient() {
        buffer = new byte[buffer_size];
        header = new StringBuffer();
        response = new StringBuffer();
    }

    /**
     * <em>connect</em> connects to the input host on the default http port --
     * port 80. This function opens the socket and creates the input and output
     * streams used for communication.
     */
    public void connect(String host) throws Exception {

        /**
         * Open my socket to the specified host at the default port.
         */
        socket = new Socket(host, PORT);

        /**
         * Create the output stream. 发送
         */
        ostream = new BufferedOutputStream(socket.getOutputStream());

        /**
         * Create the input stream.  接收
         */
        istream = new BufferedInputStream(socket.getInputStream());


        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * <em>processGetRequest</em> process the input GET request.
     */
    public void processGetRequest(String request) throws Exception {
        /**
         * Send the request to the server.
         */
        request += " HTTP/1.1" + CRLF;
        request += "Host: www.nwpu.edu.cn" + CRLF;
        request += "Connection: keep-alive" + CRLF;
        request += "Upgrade-Insecure-Requests: 1" + CRLF;
        request += "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36" + CRLF;
        request += "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9" + CRLF;
        //request += "Accept-Encoding: gzip, deflate, br" + CRLF;
        //request += "Accept-Language: zh,zh-CN;q=0.9,en-US;q=0.8,en;q=0.7,zh-TW;q=0.6" + CRLF;
        request += CRLF;
        //System.out.println(request);
        buffer = request.getBytes();
        ostream.write(buffer, 0, request.length());
        ostream.flush();
        /**
         * waiting for the response.
         */
        processResponse();
    }

    /**
     * <em>processPutRequest</em> process the input PUT request.
     */
    public void processPutRequest(String request) throws Exception {
        //=======start your job here============//


        //=======end of your job============//
    }

    /**
     * <em>processResponse</em> process the server response.
     */
    public void processResponse() throws Exception {
        int last = 0, c = 0;
        /**
         * Process the header and add it to the header StringBuffer.
         */
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
        //如果200OK了
        String head=new String(header);
        if(head.contains("200 OK")) {
            /**
             * Read the contents and add it to the response StringBuffer.
             */
/*
        while (istream.read(buffer) != -1) {

            response.append(new String(buffer, StandardCharsets.ISO_8859_1));
        }
        System.out.println(response);*/

            while (istream.read(buffer) != -1) {
            /*
            for(byte b:buffer){
                System.out.print((char)b);
                response.append((char)b);
            }*/
                String temp = new String(buffer, StandardCharsets.ISO_8859_1);
                if (temp.contains("</BODY></HTML>")) {
                    response.append(temp.substring(0, temp.lastIndexOf("</BODY></HTML>") + "</BODY></HTML>".length()));
                    break;
                } else {
                    response.append(temp);
                }
            }
        }

    }

    /**
     * Get the response header.
     */
    public String getHeader() {
        return header.toString();
    }

    /**
     * Get the server's response.
     */
    public String getResponse() {
        return response.toString();
    }

    /**
     * Close all open connections -- sockets and streams.
     */
    public void close() throws Exception {
        socket.close();
        istream.close();
        ostream.close();
    }
}
