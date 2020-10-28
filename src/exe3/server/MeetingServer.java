package exe3.server;

import exe3.rface.InterfaceImplements;
import exe3.rface.MeetingInterface;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class MeetingServer {


    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            MeetingInterface meetingInterface = new InterfaceImplements();
            Naming.rebind("Meeting", meetingInterface);
            System.out.println("Server started successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
