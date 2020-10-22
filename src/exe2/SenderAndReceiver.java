package exe2;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class SenderAndReceiver {

    private static final int BUFFER_SIZE = 8 * 1024;

    public static boolean sendFile(BufferedOutputStream ostream, File file) throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            long contentLength = file.length();
            int bufferSize = Math.toIntExact(Math.min(BUFFER_SIZE, contentLength));
            if (BUFFER_SIZE < contentLength) {
                contentLength = contentLength - BUFFER_SIZE;
            }
            byte[] buffer = new byte[bufferSize];


            while (fileInputStream.read(buffer) >= 0) {
                ostream.write(buffer, 0, buffer.length);
                ostream.flush();

                if(fileInputStream.available()==0)
                    break;

                if (contentLength > BUFFER_SIZE) {
                    bufferSize = BUFFER_SIZE;
                    contentLength = contentLength - BUFFER_SIZE;
                } else {
                    bufferSize = Math.toIntExact(contentLength);
                }
                buffer = new byte[bufferSize];


            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<byte[]> receiveFile(BufferedInputStream istream,String head) throws IOException {
        try {
            ArrayList<byte[]> fileBytes=new ArrayList < byte[] > ();

            //FileOutputStream fileOutputStream = new FileOutputStream(file);
            int contentLength=getContentLength(head);
            int bufferSize = Math.min(BUFFER_SIZE, contentLength);
            if (BUFFER_SIZE < contentLength) {
                contentLength = contentLength - BUFFER_SIZE;
            }
            byte[] buffer = new byte[bufferSize];
            while (istream.read(buffer) != -1) {

                fileBytes.add(buffer);
                //String temp = new String(buffer, StandardCharsets.ISO_8859_1);

                //response.append(temp);
                if (istream.available() == 0)
                    break;

                if (contentLength > BUFFER_SIZE) {
                    bufferSize = BUFFER_SIZE;
                    contentLength = contentLength - BUFFER_SIZE;
                } else {
                    bufferSize = contentLength;
                }
                buffer = new byte[bufferSize];

            }
            //FileOutputStream fileOutputStream=new FileOutputStream(file);
            //fileOutputStream.write(response.toString().getBytes(StandardCharsets.ISO_8859_1));

            return fileBytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String receiveHeader(BufferedInputStream istream) {
        try {
            int last = 0, c = 0;
            boolean inHeader = true; // loop control
            StringBuilder header = new StringBuilder();
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
            return header.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getContentType(String fileName) {
        if (fileName.endsWith(".jpg") || fileName.endsWith("jpeg")) {
            return "image/jpeg";
        }
        if (fileName.endsWith(".txt"))
            return "ext/plain";
        if (fileName.endsWith(".html"))
            return "text/html";
        if (fileName.endsWith(".json"))
            return "application/json";
        if (fileName.endsWith(".pdf"))
            return "application/pdf";
        return "application/octet-stream";
    }
    public static int getContentLength(String head){
        String []heads=head.split("\n");
        for(String s:heads){
            if(s.contains("Content-Length")){
                return Integer.parseInt(s.split(" ")[1]);
            }
        }
        return 0;
    }
    public static String[] getResources(String html,String resName){
        ArrayList<String> resources=new ArrayList<String> ();
        String []elem=html.split("<");
        for(String e:elem){
            if(e.startsWith(resName)){
                String []token=e.split("\"");
                for(int i=0;i<token.length; i++){
                    if(token[i].contains("src")){
                        resources.add(token[i+1]);
                        break;
                    }
                }
            }
        }
        for(int i=0;i<resources.size(); i++){
            resources.set(i, resources.get(i).replaceAll(" ", ""));
        }

        return resources.toArray(new String[0]);
    }

/*
    public static void main(String[] args) {
        String s="<!DOCTYPE html>\n" +
                "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/html\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>用于测试</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>\n" +
                "    这个网页是用于测试的\n" +
                "</h1>\n" +
                "<h2>\n" +
                "    下一行是为了影响解析html内容的\n" +
                "</h2>\n" +
                "<h3>\n" +
                "    < img src =\"f.jpg\" alt=“图片呢”>\n" +
                "    <br>\n" +
                "    上一行应该是文本，不应该被解析成图片\n" +
                "    <img src= \"f.jpg\" alt=\"?\" width=\"10px\" height=\"10\"\n" +
                "</h3>\n" +
                "<img src=\n" +
                "             \" f.jpg \" alt=\"图片没传过来\">\n" +
                "</body>\n" +
                "</html>";
        System.out.println(Arrays.toString(getResources(s,"img")));
    }*/

}
