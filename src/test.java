import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class test {
    public static void main(String[] args) {
        String []para="add zl zz ll 2020-10-20 12:00:01 2020-10-21 12:00:22 helloWorld".split(" ");
        ArrayList<String> otherNames = new ArrayList<String>(Arrays.asList(para).subList(1, para.length - 5));
        System.out.println(otherNames);
        System.out.println(new Date()+"\n"+new Date(0));
    }
}
