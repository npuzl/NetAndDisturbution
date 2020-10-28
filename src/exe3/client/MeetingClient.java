package exe3.client;

import exe3.bean.Meeting;
import exe3.rface.MeetingInterface;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MeetingClient {
    MeetingInterface RMI;
    String username;
    String password;

    public void showMenu() {
        String s = "Menu:\n" +
                "1. add <userNameList> <start> <end> <title>\n" +
                "2. delete <meetingID>\n" +
                "3. clear\n" +
                "4. query <start> <end>\n" +
                "5. help\n" +
                "6. quit";
        System.out.println(s);


    }

    public static void main(String[] args) {
        MeetingClient meetingClient = new MeetingClient();
        //需要至少两个参数，这两个参数为远程的地址和端口号
        try {
            //参数小于2 就直接返回了
            if (args.length < 2) {
                System.err.println("Parameter Error!");
                return;
            }
            String host = args[0];
            String port = args[1];

            meetingClient.RMI = (MeetingInterface) Naming.lookup("//" + host + ":" + port + "/Meeting");

            boolean loginStatus = false;
            if (args.length == 5) {
                //参数登录
                String order = args[2];
                meetingClient.username = args[3];
                meetingClient.password = args[4];
                switch (order) {
                    case "login" -> {

                        loginStatus = meetingClient.RMI.userLogin(meetingClient.username, meetingClient.password);

                    }
                    case "register" -> {

                        loginStatus = meetingClient.RMI.userRegister(meetingClient.username, meetingClient.password);

                    }
                }
                if (loginStatus)
                    System.out.println("Login in success!");


            } else {
                //在命令行输入登录
                while (!loginStatus) {
                    System.out.println("Login In or Register?");
                    System.out.println("[1]Login in   [2]Register");
                    Scanner sc = new Scanner(System.in);
                    String choice = sc.nextLine();
                    System.out.println("UserName:");
                    meetingClient.username = sc.nextLine();
                    System.out.println("Password:");
                    meetingClient.password = sc.nextLine();
                    switch (choice) {
                        case "1" -> {
                            loginStatus = meetingClient.RMI.userLogin(meetingClient.username, meetingClient.password);
                        }
                        case "2" -> {
                            loginStatus = meetingClient.RMI.userRegister(meetingClient.username, meetingClient.password);
                        }
                    }
                    if (loginStatus) {
                        //如果登录成功
                        System.out.println("Welcome " + meetingClient.username + "!");
                        break;
                    }
                    //到这里就是登录失败了
                    System.out.println((Objects.equals(choice, "1") ? "Login in" : "Register") + " failed!");

                }

            }
            boolean flag = true;
            while (flag) {
                meetingClient.showMenu();
                System.out.println("Input an operation:");
                Scanner scanner = new Scanner(System.in);
                String order = scanner.nextLine();
                switch (order.split(" ")[0]) {
                    case "add" -> {
                        if (meetingClient.add(order)) {
                            System.out.println("Add meeting success！");
                        } else {
                            System.out.println("Add meeting failed");
                        }
                    }
                    case "delete" -> {
                        if (meetingClient.delete(order)) {
                            System.out.println("Delete meeting success！");
                        } else {
                            System.out.println("Delete meeting failed");
                        }
                    }
                    case "clear" -> {
                        if (meetingClient.clear(order)) {
                            System.out.println("Clear your meeting success！");
                        } else {
                            System.out.println("clear failed");
                        }
                    }
                    case "query" -> System.out.println(meetingClient.query(order));
                    case "help" -> meetingClient.help();
                    case "quit" -> flag = false;
                    default -> {
                        System.out.println(meetingClient.RMI.searchMeeting(new Date(0), new Date()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void help() {
        String help;
        System.out.println();

    }

    public String query(String order) {
        try {
            String[]params=order.split(" ");
            //格式为
            //query 2020-10-01 12:01:01 2020-10-22 12:01:59
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date startDate = sdf.parse(params[1]+params[2]);


            Date endDate = sdf.parse(params[3] + params[4]);


            return RMI.searchMeeting(startDate, endDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean clear(String order) {

        try {
            RMI.clearMeeting(username, password);
        } catch (RemoteException e) {
            return false;
        }
        return true;


    }

    public boolean delete(String order) {
        try {
            String []para=order.split(" ");
            int meetingID = Integer.parseInt(para[para.length - 1]);
            return RMI.deleteMeeting(username, password, meetingID);
        } catch (Exception e) {
            return false;
        }

    }

    public boolean add(String order) {
        try {
            String[] para = order.split(" ");
            //格式是这样的
            //add zl zz ll 2020-10-20 12:00:01 2020-10-21 12:00:22 helloWorld
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            ArrayList<String> otherNames = new ArrayList<String>(Arrays.asList(para).subList(1, para.length - 5));
            String title = para[para.length - 1];
            Date startDate = sdf.parse(para[para.length - 5] + para[para.length - 4]);
            Date endDate = sdf.parse(para[para.length - 3] + para[para.length - 2]);
            if (startDate.compareTo(endDate) > 0) {
                System.out.println("Time format Error!");
                return false;
            }
            return RMI.addMeeting(username, password, otherNames, title, startDate, endDate);

        } catch (Exception e) {
            return false;
        }

    }
}
