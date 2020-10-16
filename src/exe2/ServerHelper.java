package exe2;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ServerHelper implements Runnable {
    /**
     * Output stream to the socket.
     */
    BufferedOutputStream ostream = null;

    /**
     * Input stream from the socket.
     */
    BufferedInputStream istream = null;
    public Socket socket;
    String path;
    int PACKET_SIZE = 8 * 1024;

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
     *
     * @param socket
     * @param path 为服务器目录
     */
    ServerHelper(Socket socket, String path) {
        this.socket = socket;
        this.path = path;
    }

    @Override
    public void run() {
        try {
            initStream();
            String info;
            String remote = socket.getInetAddress() + ":" + socket.getPort() + ">>";
            StringBuilder request= new StringBuilder();
            byte[] buffer=new byte[PACKET_SIZE];

            while (istream.read(buffer) != -1){
                request.append(new String(buffer, StandardCharsets.ISO_8859_1));
                buffer=new byte[PACKET_SIZE];
                if(istream.available()==0)
                    break;
            }
            info = request.toString();
            System.out.println(info);

                String[] req = info.split(" ", 2);
                switch (req[0]) {
                    case "GET": {

                    }
                    case "PUT": {

                    }
                    default: {
                        break;
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
