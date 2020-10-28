package exe3.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Meeting implements Serializable {

    private int MeetingID;
    private String meetingTitle;
    private Date startTime;
    private Date endTime;
    private User organizer;
    private ArrayList<String> participant;

    public Meeting(int MeetingID, String meetingTitle, Date startTime, Date endTime, User organizer, ArrayList<String> participant) {
        this.MeetingID = MeetingID;
        this.meetingTitle = meetingTitle;
        this.endTime = endTime;
        this.organizer = organizer;
        this.startTime = startTime;
        this.participant = participant;
    }

    public int getMeetingID() {
        return MeetingID;
    }

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public User getOrganizer() {
        return organizer;
    }

    public ArrayList<String> getParticipant() {
        return participant;
    }

    public void setMeetingID(int meetingID) {
        MeetingID = meetingID;
    }

    public void setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public void setParticipant(ArrayList<String> participant) {
        this.participant = participant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meeting meeting = (Meeting) o;
        return MeetingID == meeting.MeetingID &&
                Objects.equals(meetingTitle, meeting.meetingTitle) &&
                Objects.equals(startTime, meeting.startTime) &&
                Objects.equals(endTime, meeting.endTime) &&
                Objects.equals(organizer, meeting.organizer) &&
                Objects.equals(participant, meeting.participant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(MeetingID, meetingTitle, startTime, endTime, organizer, participant);
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "MeetingID=" + MeetingID +
                ", meetingTitle='" + meetingTitle + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", organizer=" + organizer +
                ", participant=" + participant +
                '}';
    }
}

