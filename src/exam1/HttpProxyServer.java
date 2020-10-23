package exam1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpProxyServer {
    private static final String HOST = "127.0.0.1"; //服务器的IP
    private static final int PORT = 8000; //开放的端口号
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private static final int POOL_SIZE = 4;

    /**
     * @throws IOException
     */
    public HttpProxyServer() throws IOException {
        serverSocket = new ServerSocket(PORT, 2);
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
                .availableProcessors() * POOL_SIZE);
        System.out.println("The proxy server start successfully!");
    }

    /**
     *
     */
    public void service() {
        Socket socket;
        while (true) {
            try {
                socket = serverSocket.accept();
                executorService.execute(new HttpProxyHelper(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        new HttpProxyServer().service();
    }
}
