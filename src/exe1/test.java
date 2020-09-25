package exe1;

import java.io.File;

public class test {

    public static void t(String s){
        s="1";
    }
    public static void main(String[] args) {
        String s="2";
        t(s);
        System.out.println(s.split(" ").length);
        System.out.println(s);
        String path="C:\\Users\\zl\\Desktop\\test";
        System.out.println(new File(path).isDirectory());
        String ss="~工业大学关于举办第六届中国国际“互联网+”大学生创新创业大赛校内选拔赛通知.docx";
        System.out.printf("%-50s%-12s%s",ss,"directory","12");
        System.out.println(path.lastIndexOf("2222"));
        String source="z:\\java\\kl\\\\";
        System.out.println(source.matches("^[A-z]:\\\\(.+?\\\\)*$"));
        //stem.out.println(path.substring(0,path.lastIndexOf("111")));
    }
}
