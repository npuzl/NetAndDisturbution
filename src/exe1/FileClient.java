package exe1;


import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * 文件服务程序，客户端
 *
 * @author zl
 * @version 2.0
 * @date 2020/09/24
 */
public class FileClient {
    /**
     * TCP 端口号
     */
    static final int TCP_PORT = 2020;
    /**
     * UDP端口号
     */
    static final int UDP_PORT = 2021;
    /**
     * 服务器IP地址
     */
    static final String REMOTE_HOST = "127.0.0.1";
    /**
     * TCP 的 socket
     */
    Socket socket = new Socket();
    /**
     * UDP 的 socket
     */
    DatagramSocket datagramSocket = new DatagramSocket();
    /**
     * 本机IP地址
     */
    public String LOCAL_IP_ADDRESS;

    /**
     * 本机端口
     */
    public int LOCAL_PORT;
    /**
     * 当前服务器路径
     */
    String serverPath;
    /**
     * path为下载文件的保存目录 ，默认路径为C:\Users\zl\Desktop\test
     */
    public String PATH = "C:\\Users\\zl\\Desktop\\test";

    /**
     * 每个包的大小
     */
    int PACKET_SIZE = 8 * 1024;
    /**
     * 输入 用于发送数据
     */
    public BufferedWriter bw;
    /**
     * 输出  用于接收数据
     */
    public BufferedReader br;
    /**
     * 装饰后的输入，用于发送数据
     */
    public PrintWriter pw;
    /**
     * 本机输入数据
     */
    public Scanner in;

    /**
     * 初始化输入输出流
     */
    public void initStream() {
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            pw = new PrintWriter(bw, true);
            in = new Scanner(System.in);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 构造函数 检测保存文件的目录是否存在，不存在的话创建一个
     * 并且与服务器建立TCP连接
     *
     * @throws IOException
     * @throws UnknownHostException
     */
    public FileClient() throws IOException, UnknownHostException {
        //TCP连接的地址打包
        InetSocketAddress socketAddress = new InetSocketAddress(REMOTE_HOST, TCP_PORT);
        //如果下载文件夹不存在，创建一个  默认路径为C:\Users\zl\Desktop\test
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //连接到服务器，10s没连接上就结束
        socket.connect(socketAddress, 10000);
        //初始化本地IP地址
        InetAddress ia = InetAddress.getLocalHost();
        LOCAL_IP_ADDRESS = ia.getHostAddress();

        initStream();

    }

    /**
     * 客户端的主要实现方法
     *
     * @throws IOException
     */
    public void send() throws IOException {
        try {

            //连接成功后,先发送一个获取客户端端口号的请求
            pw.println("PortRequest");
            LOCAL_PORT = Integer.parseInt(br.readLine());
            //再发送一个请求服务器默认路径的请求
            pw.println("CurrentPath");
            serverPath = br.readLine();
            //msg为收到的信息
            String msg;

            System.out.println("Local IP address： " + LOCAL_IP_ADDRESS + "  Port number：" + LOCAL_PORT + "    Connect Success！");
            System.out.println("*******************The list of available orders**********************");
            System.out.println("[1] ls\t\t\t\tThe server will return the list of the current directory");
            System.out.println("[2] cd <dir> \t\tEnter the specified directory");
            System.out.println("[3] get <file> \t\tDownload the specified file from server");
            System.out.println("[4] setting\t\t\tSet the directory of the download file");
            System.out.println("[5] bye\t\t\t\tDisconnect with the server");
            System.out.println("*********************************************************************");

            System.out.print(serverPath + ">>");
            while ((msg = in.nextLine()) != null) {

                //发送命令
                String order = msg.split(" ", 2)[0];

                switch (order) {

                    case "ls": {
                        lsOrder(serverPath);
                        break;
                    }
                    case "cd": {
                        cdOrder(msg.split(" ", 2)[1]);
                        break;
                    }
                    case "get": {
                        getOrder(serverPath, msg.split(" ", 2)[1], PATH);
                        break;
                    }
                    case "setting": {
                        while (true) {
                            System.out.println("Please input new directory path：");
                            PATH = in.nextLine();
                            File dir = new File(PATH);
                            if (!dir.exists()) {
                                if (!dir.mkdirs()) {
                                    //如果创建没成功
                                    System.out.println("Wrong directory path!");
                                    continue;
                                }
                            }
                            break;
                        }
                        System.out.println("The new download file path is " + PATH);
                        break;
                    }
                    case "bye": {
                        pw.println("bye");
                        System.out.println("************************SERVICE END************************");
                        return;
                    }
                    default: {
                        System.out.println("Unrecognized command");
                        break;

                    }
                }
                System.out.print(serverPath + ">>");

            }
            br.close();
            bw.close();
            pw.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断路径在服务器端是否存在
     *
     * @param path 需要判断的路径
     * @return 路径是否存在，true是存在
     */
    public boolean pathIsLegal(String path) {
        pw.println("check " + path);
        try {
            return Boolean.parseBoolean(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * cd 命令的实现方法
     *
     * @param targetPath 为想要进入的地址
     */
    public void cdOrder(String targetPath) {
        //cd ..命令
        if ("..".equals(targetPath)) {
            //两个判断
            //先判断是否路径里面没有反斜杠，例如 C:
            //再判断上层目录是否存在（好像没必要）
            if (serverPath.lastIndexOf("\\") != -1 && pathIsLegal(serverPath.substring(0, serverPath.lastIndexOf("\\")))) {
                serverPath = serverPath.substring(0, serverPath.lastIndexOf("\\"));
                pw.println("cd " + serverPath);
                return;
            }
            return;
        }
        //cd 绝对路径
        if (targetPath.lastIndexOf(":") != -1) {

            if (!"\\".equals(targetPath.substring(targetPath.length() - 1, targetPath.length()))) {
                targetPath = targetPath + "\\";
            }
            if (pathIsLegal(targetPath)) {
                serverPath = targetPath;
                pw.println("cd " + serverPath);
            } else {
                System.out.println("Error: directory " + targetPath + "do not exist");
            }
            return;
        }

        //其他cd命令
        // cd dir 判断目录是否合法，合法的话就合并
        if (pathIsLegal(serverPath + "\\" + targetPath)) {
            serverPath = serverPath + "\\" + targetPath;
            pw.println("cd " + serverPath);
        } else {
            System.out.println("Error: directory " + serverPath + "\\" + targetPath + " do not exist");
        }

    }

    /**
     * get命令的具体实现
     *
     * @param currentPath 当前服务器目录
     * @param targetPath  请求的文件
     * @param filePath    保存文件的目录
     */
    public void getOrder(String currentPath, String targetPath, String filePath) {
        //downloadFilePath为下载文件在本地的保存地址
        String downloadFilePath;
        if (targetPath.contains(":")) {
            //如果是用的绝对地址
            pw.println("get " + targetPath);
            downloadFilePath = targetPath;

        } else {
            //如果是相对地址
            pw.println("get " + currentPath + "\\" + targetPath);
            downloadFilePath = filePath + "\\" + targetPath;
        }
        try {
            String request = br.readLine();
            if ("notFile".equals(request)) {
                System.out.println("Can not download directory!");
                return;
            }
            if (!"false".equals(request)) {
                //包的总大小
                double packetSize = Double.parseDouble(request);

                DatagramPacket datagramPacket = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);
                byte[] receivePacket = new byte[PACKET_SIZE];
                datagramSocket = new DatagramSocket(UDP_PORT);

                System.out.println("start to download file");
                File downloadFile = new File(downloadFilePath);
                if (!downloadFile.createNewFile()) {
                    System.out.println("Can not make a new file");
                }
                FileOutputStream fileOutputStream = new FileOutputStream(downloadFile);

                for (int i = 0; i < Math.ceil(packetSize / PACKET_SIZE); i++) {

                    datagramSocket.receive(datagramPacket);
                    receivePacket = datagramPacket.getData();
                    fileOutputStream.write(receivePacket, 0, datagramPacket.getLength());
                    fileOutputStream.flush();

                    System.out.printf("download has finished %.2f %%\n", 100 * (i + 1) / Math.ceil(packetSize / PACKET_SIZE));


                }
                datagramSocket.close();
                fileOutputStream.close();

                System.out.println("download " + currentPath + "\\" + targetPath + " to " +
                        filePath + "\\" + targetPath + " success!");
            } else {
                System.out.println(targetPath + " do not exist");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ls 命令的具体实现
     *
     * @param currentPath 是当前目录
     */
    public void lsOrder(String currentPath) {
        pw.println("ls " + currentPath);
        try {
            int length = Integer.parseInt(br.readLine());
            System.out.println("total " + length);
            for (int i = 0; i < length; i++) {
                System.out.println(br.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            new FileClient().send();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
