package jdkFake;

import java.util.Objects;

public class Couple {
    private String maleName="";
    private String femaleName="";
    private boolean loveState=true;
    public Couple(){
        System.out.println("create anonymous couple success!");
    }
    public Couple(String maleName_,String femaleName_){
        this.maleName = maleName_;
        this.femaleName = femaleName_;
        System.out.println("create a couple with male name:"+maleName_ +"and female name:"+femaleName_+"success!");
    }
    public void setName(String maleName_,String femaleName_){
        this.maleName = maleName_;
        this.femaleName = femaleName_;
    }
    public void setLoveState(boolean LoveState){
        loveState=LoveState;
    }
    public boolean love(){
        return loveState;
    }
    public void kiss(){
        String name="";
        if(!Objects.equals(maleName, "")){
            name=maleName+" and "+femaleName+" kiss ";
        }else{
            name="the couple kiss";
        }
        System.out.println(name);
    }
}
