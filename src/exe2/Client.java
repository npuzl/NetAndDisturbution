package exe2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Class <em>Client</em> is a class representing a simple HTTP client.
 *
 * @author wben, zl
 * @version 1.0
 * @date 2020/10/09
 */

public class Client {

    /**
     * default HTTP port is port 80
     */
    private static final int port = 80;

    /**
     * Allow a maximum buffer size of 8192 bytes
     */
    private static final int buffer_size = 8192;

    /**
     * The end of line character sequence.
     */
    private static final String CRLF = "\r\n";

    /**
     * Input is taken from the keyboard
     */
    static BufferedReader keyboard = new BufferedReader(new InputStreamReader(
            System.in));

    /**
     * Output is written to the screen (standard out)
     */
    static PrintWriter screen = new PrintWriter(System.out, true);

    public static void main(String[] args) throws Exception {
        try {
            /*
              Create a new HttpClient object.
             */
            HttpClient myClient = new HttpClient();

            /**
             * Parse the input arguments.
             */
            if (args.length != 1) {
                System.err.println("Usage: Client <server>");
                System.exit(0);
            }

            /**
             * Connect to the input server
             */
            myClient.connect(args[0]);

            /**
             * Read the get request from the terminal.
             */
            screen.println(args[0] + " is listening to your request:");
            String request = keyboard.readLine();
            if (request.startsWith("GET")) {
                /**
                 * Ask the client to process the GET request.
                 */
                myClient.processGetRequest(request);

            } else if (request.startsWith("PUT")) {
                /**
                 * Ask the client to process the PUT request.
                 */
                myClient.processPutRequest(request);
            } else {
                /**
                 * Do not process other request.
                 */
                screen.println("Bad request! \n");
                myClient.close();
                return;
            }

            /**
             * Get the headers and display them.
             */
            screen.println("Header: ");
            screen.print(myClient.getHeader() + "\n");
            screen.flush();

            if (request.startsWith("GET")) {
                /**
                 * Ask the user to input a name to save the GET resultant web page.
                 */
                screen.println();
                screen.print("Enter the name of the file to save: ");
                screen.flush();


                String filename = keyboard.readLine();
                String dirName = filename.split("\\.")[0];
                File dir = new File(dirName);
                if (!dir.mkdirs()) {
                    System.out.println("文件夹创建失败");
                }

                File html = new File(dirName +"/"+ filename);
                if ((!html.exists() && html.createNewFile()) || html.exists()) {
                    /////
                    ;
                }
                FileOutputStream outfile = new FileOutputStream(html);
                /**
                 * Save the response to the specified file.
                 */
                ArrayList<byte[]> fileBytes = myClient.getFile();
                StringBuilder response = new StringBuilder();
                for (byte[] b : fileBytes) {
                    outfile.write(b);
                    outfile.flush();
                    response.append(new String(b));
                }
                outfile.close();

                String[] resources = SenderAndReceiver.getResources(response.toString(), "img");

                for (String r : resources) {
                    //生成get
                    String get = "GET " + r + "HTTP/1.1";
                    myClient.processGetRequest(get);
                    File file = new File(dirName + "/" + r);
                    if ((!file.exists() && file.createNewFile()) || file.exists()) {
                        FileOutputStream fos = new FileOutputStream(file);
                        fileBytes = myClient.getFile();
                        for (byte[] b : fileBytes) {
                            fos.write(b);
                            fos.flush();
                        }
                        fos.close();
                    }
                }


            }

            /*
              Close the connection client.
             */
            myClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
