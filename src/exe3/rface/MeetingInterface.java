package exe3.rface;

import exe3.bean.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;

public interface MeetingInterface extends Remote {
    public boolean userLogin(String user, String password) throws RemoteException;

    public boolean userRegister(String user, String password) throws RemoteException;

    public boolean addMeeting(String user, String password, ArrayList<String> otherUsers, String meetingTitle, Date meetingStartTime,
                           Date MeetingEndTime) throws RemoteException;

    public String searchMeeting(Date startTime, Date endTime) throws RemoteException;

    public boolean deleteMeeting(String username, String password, int meetingID) throws RemoteException;

    public boolean clearMeeting(String username, String password) throws RemoteException;

}
