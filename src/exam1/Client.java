package exam1;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author zl
 * @version 1.3
 * @date 2020/10/16
 * 客户端类，实现客户端功能
 */
public class Client {

    static BufferedReader keyboard = new BufferedReader(new InputStreamReader(
            System.in));
    static PrintWriter screen = new PrintWriter(System.out, true);

    public static void main(String[] args) throws Exception {
        try {

            HttpClientHelper myClient = new HttpClientHelper();
            if (args.length != 1) {
                System.err.println("Usage: Client <server>");
                System.exit(0);
            }
            myClient.connect(args[0]);
            screen.println(args[0] + " is listening to your request:");
            String request = keyboard.readLine();

            if (request.startsWith("GET")) {
                myClient.processGetRequest(request);
            } else {
                screen.println("Bad request! \n");
                myClient.close();
                return;
            }

            screen.println("Header: \n");
            screen.print(myClient.getHeader() + "\n");
            screen.flush();

            if (request.startsWith("GET")) {
                if (myClient.getHeader().contains("200 OK")) {
                    screen.println();
                    screen.print("Enter the name of the file to save: ");
                    screen.flush();
                    String filename = keyboard.readLine();
                    FileOutputStream outfile = new FileOutputStream(filename);
                    String response = myClient.getResponse();


                    outfile.write(response.getBytes(StandardCharsets.ISO_8859_1));
                    outfile.flush();
                    outfile.close();
                } else {
                    screen.println("Bad request!");
                }
            }
            myClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
