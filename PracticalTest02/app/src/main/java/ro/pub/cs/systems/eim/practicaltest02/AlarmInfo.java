package ro.pub.cs.systems.eim.practicaltest02;

/**
 * Created by irineu on 20.05.2016.
 */
public class AlarmInfo {

    private int hour;
    private int minutes;
    private boolean isActive;

    public AlarmInfo(int hour, int minutes) {
        this.hour = hour;
        this.minutes = minutes;
        this.isActive = false;
    }

    public boolean isAlarmActive() {
        return this.isActive;
    }

    public void setActive() {
        this.isActive = true;
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinutes() {
        return this.minutes;
    }

    @Override
    public String toString() {
        return isActive + ":" + hour + ":" + minutes;
    }

}
