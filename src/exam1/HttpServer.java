package exam1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务器客户端
 *
 * @author zl
 * @version 1.0
 * @date 2020/10/16
 */
public class HttpServer {

    private static final String host = "127.0.0.1"; //本机地址
    private static final int port = 999; // 开放的端口

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private static final int POOL_MULTIPLE = 4;

    /**
     * 启动
     *
     * @throws IOException
     */
    public HttpServer() throws IOException {
        serverSocket = new ServerSocket(port, 2);
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
                .availableProcessors() * POOL_MULTIPLE);
        System.out.println("The server start successfully!");
    }

    /**
     * 执行方法
     */
    public void service() {
        Socket socket;
        while (true) {
            try {
                socket = serverSocket.accept();
                executorService.execute(new ServerHelper(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new HttpServer().service();
    }
}

