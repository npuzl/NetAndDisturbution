import java.io.*;
import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        try {

            File file=new File("D:\\netAndDistrubution\\exp\\exe1\\test.txt");
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\netAndDistrubution\\exp\\exe1\\test.txt"));
            FileInputStream fis=new FileInputStream(new File("D:\\netAndDistrubution\\exp\\exe1\\test.txt"));
            int i=0;
            while(i++<1000){
                StringBuilder str= new StringBuilder();
                for(int j=0;j<=i;j++){
                    str.append(j);
                }
                fileOutputStream.write(str.toString().getBytes());
                fileOutputStream.write("\n".getBytes());
            }
            byte[] b=new byte[8*1024];
            while (fis.read(b) != -1){
                System.out.println(Arrays.toString(b));
                b=new byte[8*1024];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
