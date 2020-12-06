package Model;

import java.sql.Time;
import java.time.LocalTime;

public class SimulationTime {
    private String date;
    private Time time;
    private long lastRealTime;
    private float timeSpeed;
    private final int[] summer;
    private final int[] winter;

    /**
     * Gets the date of the simulation.
     *
     * @return date as a string
     */
    public String getDate() {
        return date;
    }


    /**
     * Sets the date of the simulation.
     *
     * @param date the date of the simulation as a string
     */
    public String setDate(String date) {
        this.date = date;
        return "Date set to: " + date + ".";
    }


    /**
     * Gets the time of the simulation. Need to update the time first according to timeSpeed and elapsed time.
     *
     * @return Time object
     */
    public Time getTime() {
        long rightNow = Time.valueOf(LocalTime.now()).getTime();
        time.setTime(time.getTime() + (long) timeSpeed * (rightNow - lastRealTime));
        lastRealTime = rightNow;
        return time;
    }


    /**
     * Sets the time of the simulation using time object.
     *
     * @param time new time to be set
     * @return a string: "time updated"
     */
    public String setTime(Time time) {
        lastRealTime = Time.valueOf(LocalTime.now()).getTime();
        this.time = time;
        return "Time set to: " + time + ".";
    }

    /**
     * Sets the time speed of the simulation
     *
     * @param speed multiplier for time speed
     */
    public String setTimeSpeed(Float speed) {
        timeSpeed = speed;
        return "Set time speed to " + speed.toString() + ".";
    }

    public long getLastRealTime() {
        return lastRealTime;
    }

    public void setLastRealTime(long lastRealTime) {
        this.lastRealTime = lastRealTime;
    }

    public float getTimeSpeed() {
        return timeSpeed;
    }

    /**
     * Get the array defining the summer interval
     *
     * @return
     */
    public int[] getSummer() {
        return this.summer;
    }

    /**
     * Get the array defining the winter interval
     *
     * @return
     */
    public int[] getWinter() {
        return this.winter;
    }

    /**
     * Set the interval of months for summer and winter seasons
     *
     * @param summerStart
     * @param summerEnd
     * @param winterStart
     * @param winterEnd
     * @return String saying the operation has been completed successfully.
     */
    public String setSeasons(int summerStart, int summerEnd, int winterStart, int winterEnd) {
        this.summer[0] = summerStart;
        this.summer[1] = summerEnd;
        this.winter[0] = winterStart;
        this.winter[1] = winterEnd;
        return "Successfully set season intervals.";
    }

    /**
     * Takes in a month and returns whether the month is in the summer or not
     *
     * @param month int (1-12) that represents the month input
     * @return true if the month is in summer, false otherwise
     */
    public boolean isSummer(int month) {
        if (month > 12 || month < 1) {
            return false;
        }
        if (this.summer[0] > this.summer[1]) {
            return month >= this.summer[0] || month <= this.summer[1];
        } else {
            return month >= this.summer[0] && month <= this.summer[1];
        }
    }

    /**
     * Takes in a month and returns whether the month is in the winter or not
     *
     * @param month int (1-12) that represents the month input
     * @return true if the month is in summer, false otherwise
     */
    public boolean isWinter(int month) {
        if (month > 12 || month < 1) {
            return false;
        }
        if (this.winter[0] > this.winter[1]) {
            return month >= this.winter[0] || month <= this.winter[1];
        }
        else {
            return month >= this.winter[0] && month <= this.winter[1];
        }
    }

    public SimulationTime(String date, Time time) {
        this.date = date;
        this.time = time;
        this.lastRealTime = Time.valueOf(LocalTime.now()).getTime();
        this.timeSpeed = 1;
        this.summer = new int[] {5, 8};
        this.winter = new int[] {10, 2};
    }
}
