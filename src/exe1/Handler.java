package exe1;

import java.io.*;
import java.net.*;

/**
 * 多线程的主要实现类
 *
 * @author zl
 * @version 2.0
 * @date 2020/09/24
 */
public class Handler implements Runnable {
    /**
     * 输入输出流
     */
    BufferedReader br;
    BufferedWriter bw;
    PrintWriter pw;
    /**
     * TCP连接的socket
     */
    public Socket socket;
    /**
     * 服务器的默认路径（一开始是作为参数传递进来的）
     */
    String path;
    /**
     * UDP的端口号
     */
    int UDP_PORT = 2021;
    /**
     * UDP连接的socket
     */
    DatagramSocket datagramsocket;
    /**
     * UDP发送文件的每个包大小
     */
    int PACKET_SIZE = 8 * 1024;

    /**
     * 初始化输入输出流
     */
    public void initStream() {
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            pw = new PrintWriter(bw, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 主要实现函数
     *
     * @param socket Receive messages from customers
     * @param path   The root directory of server
     */
    public Handler(Socket socket, String path) {
        this.socket = socket;
        this.path = path;
    }

    @Override
    public void run() {
        try {
            //System.out.println("new connection: "+socket.getInetAddress()+":"+socket.getPort());
            initStream();
            //info为收到的命令
            String info;
            //客户端的ip和端口
            String remote = socket.getInetAddress() + ":" + socket.getPort() + ">>";

            while ((info = br.readLine()) != null) {
                //将收到的信息用空格分割，第一段为命令，第二段为目录
                String[] orders = info.split(" ", 2);
                //info为命令
                info = orders[0];
                //命令为返回端口号
                switch (info) {
                    case "PortRequest": {
                        //返回端口号
                        pw.println(socket.getPort());
                        System.out.println(remote + "PortRequest");
                        break;
                    }
                    //命令为获取服务器默认路径
                    case "CurrentPath": {
                        //返回服务器的默认路径
                        System.out.println(remote + "Get Server Current Path");
                        pw.println(path);
                        break;
                    }
                    //ls命令
                    case "ls": {
                        System.out.println(remote + "ls " + orders[1]);
                        String requestPath = orders[1];
                        File file = new File(requestPath);
                        String[] fileNames = file.list();
                        if (fileNames == null) {
                            pw.println(0);
                            continue;
                        }
                        pw.println(fileNames.length);
                        for (String fileName : fileNames) {
                            File tempFile = new File(requestPath + "\\" + fileName);
                            if (tempFile.isDirectory()) {
                                pw.printf("%-18s%-18s%-100s\n", "<directory>", "", fileName);

                            } else {
                                pw.printf("%-18s%-18s%-100s\n", "<file>", tempFile.length() + "Byte", fileName);
                            }
                        }
                        break;

                    }
                    //check 命令，判断路径是否合法
                    case "check": {
                        File file = new File(orders[1]);
                        if (file.exists() && file.isDirectory()) {
                            pw.println("true");
                        } else {
                            pw.println("false");
                        }
                        break;
                    }
                    case "cd": {
                        System.out.println(remote + " cd " + orders[1]);

                        break;
                    }
                    //get 命令
                    case "get": {


                        System.out.println(remote + "get " + orders[1]);

                        File file = new File(orders[1]);

                        if (file.exists()) {
                            if (file.isDirectory()) {
                                pw.println("notFile");
                                break;
                            }
                            File downloadFile = new File(orders[1]);

                            long length = downloadFile.length();
                            //先返回长度
                            pw.println(length);
                            datagramsocket = new DatagramSocket();

                            DatagramPacket datagramPacket;


                            int time = 0;
                            long packetLength = Math.min(length, PACKET_SIZE);
                            time++;
                            byte[] sendPacket = new byte[Math.toIntExact(packetLength)];
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));

                            //发包
                            while (bufferedInputStream.read(sendPacket) != -1) {

                                datagramPacket = new DatagramPacket(sendPacket, sendPacket.length, new InetSocketAddress("127.0.0.1", UDP_PORT));
                                datagramPacket.setData(sendPacket);
                                datagramsocket.send(datagramPacket);
                                packetLength = PACKET_SIZE > (length - (time) * PACKET_SIZE) ? (length - time * PACKET_SIZE) : PACKET_SIZE;
                                time++;
                                if (packetLength > 0) {
                                    sendPacket = new byte[Math.toIntExact(packetLength)];
                                }
                                Thread.sleep(1);
                            }
                            bufferedInputStream.close();
                            datagramsocket.close();
                        } else {
                            pw.println("false");
                        }


                        break;
                    }
                    case "bye": {

                        System.out.println(remote + "Disconnect");
                        //socket.close();
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
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
