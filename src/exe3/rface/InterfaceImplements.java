package exe3.rface;

import exe3.bean.Meeting;
import exe3.bean.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class InterfaceImplements extends UnicastRemoteObject implements MeetingInterface {
    private ArrayList<User> UserList = new ArrayList<User>();
    private ArrayList<Meeting> MeetingList = new ArrayList<Meeting>();
    int meetingID = 0;

    public InterfaceImplements() throws RemoteException {

        super();

        UserList.add(new User("A","A"));
        UserList.add(new User("B","B"));
        UserList.add(new User("C","C"));
        MeetingList.add(new Meeting(100,"A",new Date(1),new Date(2),new User("A","A"),null));
    }

    @Override
    public boolean userLogin(String user, String password) throws RemoteException {
        for (User u : UserList) {
            if (Objects.equals(u.getName(), user) && Objects.equals(u.getPassword(), password))
                return true;
        }
        return false;
    }

    public boolean userRegister(String user, String password) throws RemoteException {
        for(User u:UserList){
            if(Objects.equals(u.getName(), user) && Objects.equals(u.getPassword(), password))
                return false;
        }
        User u = new User(user, password);
        UserList.add(u);
        return true;
    }

    public boolean addMeeting(String user, String password, ArrayList<String> otherUsers
            , String meetingTitle, Date meetingStartTime, Date meetingEndTime) throws RemoteException {
        User u = new User();
        for (User us : UserList) {
            if (Objects.equals(us.getName(), user) && Objects.equals(us.getPassword(), password)) {
                u = us;
            }
        }
        int count = 0;
        for (String o : otherUsers) {
            for (User us : UserList) {
                if (Objects.equals(us.getName(), o))
                    count++;
            }
        }
        if (count != otherUsers.size())
            return false;
        Meeting meeting = new Meeting(meetingID++,
                meetingTitle, meetingStartTime, meetingEndTime, u, otherUsers);
        MeetingList.add(meeting);
        return true;
    }


    public String searchMeeting(Date startTime, Date endTime) throws RemoteException {
        StringBuilder sb = new StringBuilder();
        for (Meeting m : MeetingList) {
            if (m.getStartTime().compareTo(startTime) > 0 && m.getStartTime().compareTo(endTime) < 0
                    && m.getEndTime().compareTo(startTime) > 0 && m.getEndTime().compareTo(endTime) < 0) {
                sb.append(m.toString());
            }
        }

        return sb.toString();
    }

    public boolean deleteMeeting(String username, String password, int meetingID) throws RemoteException {
        return MeetingList.removeIf(m -> m.getMeetingID() == meetingID);
    }

    public boolean clearMeeting(String username, String password) throws RemoteException {
        return MeetingList.removeIf(m -> Objects.equals(m.getOrganizer().getName(), username)
                && Objects.equals(m.getOrganizer().getPassword(), password));
    }
}
