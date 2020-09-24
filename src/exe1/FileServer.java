package exe1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 文件服务程序，服务端
 * @author zl
 * @version 1.5
 * @date 2020/09/24
 */
public class FileServer {
    /**
     * ServerSocket用于服务器与客户端通讯
     */
    ServerSocket serverSocket;
    /**
     * TCP端口号
     */
    private final int TCP_PORT = 2020;

    /**
     * 根目录
     */
    private static File rootFile;
    /**
     * 多线超执行
     */
    ExecutorService executorService;
    /**
     * 线程池大小
     */
    final int POOL_SIZE = 4;
    /**
     * 连接中客户端列表
     */
    ArrayList<String> IN_LINK_CLIENT = new ArrayList<>();

    /**
     * 构造函数
     * 先开启TCP的端口，并设置等待队列的最大连接数为2
     * 然后启动多线程，创建线程池
     * 提示服务器启动成功
     *
     * @throws IOException 读写
     */
    public FileServer() throws IOException {
        //backlog 等待队列的最大连接数
        serverSocket = new ServerSocket(TCP_PORT, 2);
        //多线程启动
        executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors() * POOL_SIZE);
        System.out.println("The server started successfully!");
    }

    /**
     * 启动服务方法
     * 先接收来自tcp端口的命令
     * 然后启动多线程处理接收到的命令
     */

    public void service(String path) {
        Socket socket = null;
        while (true) {
            try {

                socket = serverSocket.accept();
                String client = socket.getInetAddress() + ":" + socket.getPort();
                if (!IN_LINK_CLIENT.contains(client)) {
                    System.out.println("new connection from " + client);
                    IN_LINK_CLIENT.add(client);
                }
                executorService.execute(new Handler(socket, path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 主函数
     * 参数通过args传入，
     *
     * @param args 根目录
     */
    public static void main(String[] args) {
        while (true) {

            //当传入多个参数或者没传入参数，都重新输入
            if (args.length != 1) {
                System.out.println("You did not input the right number of parameter,Please enter again:");
                Scanner in = new Scanner(System.in);
                String s = in.nextLine();
                args = s.split(" ");
                continue;
            }

            rootFile = new File(args[0]);
            //文件夹不存在或者不是一个目录
            if (!rootFile.exists() || !rootFile.isDirectory()) {
                System.out.println(rootFile.getAbsolutePath() + " is not a legal path,please enter again:");
                Scanner in = new Scanner(System.in);
                String s = in.nextLine();
                args = s.split(" ");
                continue;
            }
            break;
        }

        System.out.println("Current file path is : " + rootFile.getAbsolutePath());
        try {
            new FileServer().service(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
