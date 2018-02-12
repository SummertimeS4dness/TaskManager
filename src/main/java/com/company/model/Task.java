package com.company.model;

import java.util.Date;

/**
 * class that describes task - main element of TaskManager.
 *
 * @author MishchenkoAnton
 * @version MavenAndLog4j
 */
public class Task implements Cloneable {
    private static final long serialVersionUID = 1L;
    private String title;
    private Date time;
    private Date start;
    private Date end;
    private long interval;
    private boolean active;
    private boolean repeated;
    private Task next;

    /**
     * constructor for creating task from another task.
     *
     * @param data task from which you want to create the new one
     */
    public Task(Task data) {
        this.title = data.title;
        if (data.time != null) {
            this.time = new Date(data.time.getTime());
        }
        this.repeated = data.repeated;
        if (data.start != null && data.end != null) {
            this.start = new Date(data.start.getTime());
            this.end = new Date(data.end.getTime());
        }
        this.interval = data.interval;
        this.active = data.active;
    }

    /**
     * constructor of non-repeated tasks.
     *
     * @param title of task
     * @param time  of task
     */
    public Task(String title, Date time) {
        this.title = title;
        this.time = new Date(time.getTime());
        this.repeated = false;
        start = new Date(time.getTime());
        end = new Date(time.getTime());
        interval = 0;
        active = true;
    }

    /**
     * constructor of repeated tasks.
     *
     * @param title    of task
     * @param start    of task
     * @param end      of task
     * @param interval of task
     */
    public Task(String title, Date start, Date end, long interval) {
        this.title = title;
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        this.interval = interval;
        this.repeated = true;
        active = true;
    }

    /**
     * getter for next task from current.
     *
     * @return next task from current
     */
    public Task getNext() {
        return next;
    }

    /**
     * setter for next task from current.
     *
     * @param next task to set as next
     * @throws NullPointerException
     */
    public void setNext(Task next) throws NullPointerException {
        if (next == null) {
            throw new NullPointerException("Next to set is null");
        }
        this.next = next;
    }

    /**
     * setter for start time of repeated task.
     *
     * @param start time
     */
    public void setStart(Date start) {
        this.start = new Date(start.getTime());
    }

    /**
     * setter for end time of repeated task.
     *
     * @param end time
     */
    public void setEnd(Date end) {
        this.end = new Date(end.getTime());
    }

    /**
     * setter for interval time of repeated task.
     *
     * @param interval time
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * getter for title of task.
     *
     * @return title of task
     */
    public String getTitle() {
        return title;
    }

    /**
     * getter for active of task.
     *
     * @return true if task is active, false if inactive
     */
    public boolean isActive() {
        return active;
    }

    /**
     * setter for title of task.
     *
     * @param title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * setter for active of task.
     *
     * @param active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * getter for start time of repeated task.
     *
     * @return start time of repeated task
     */
    public Date getStartTime() {
        return start;
    }

    /**
     * getter for end time of repeated task.
     *
     * @return end time of repeated task
     */
    public Date getEndTime() {
        return end;
    }

    /**
     * getter for start time of task.
     *
     * @return start time of repeated task or time of non-repeated task
     */
    public Date getTime() {
        if (isRepeated()) {
            return start;
        }
        return time;
    }

    /**
     * setter of time of non-repeated task.
     *
     * @param time to set
     */
    public void setTime(Date time) {
        repeated = false;
        this.time = new Date(time.getTime());
        start = new Date(time.getTime());
        end = new Date(time.getTime());
        interval = 0;
    }

    /**
     * getter of interval of task.
     *
     * @return 0 if task if non-repeated or interval of repeated task
     */
    public long getRepeatInterval() {
        if (!isRepeated()) {
            return 0;
        } else {
            return interval;
        }
    }

    /**
     * setter of time of repeated task.
     *
     * @param start    time to set
     * @param end      time to set
     * @param interval time to set
     */
    public void setTime(Date start, Date end, long interval) {
        repeated = true;
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        this.interval = interval;
    }

    /**
     * getter for repeated of task.
     *
     * @return true if task is repeated, false if non-repeated
     */
    public boolean isRepeated() {
        return repeated;
    }

    /**
     * for getting time of next time of task after some time.
     *
     * @param current time to calculate next time after it
     * @return next time after current
     */
    public Date nextTimeAfter(Date current) {
        if (!isActive()) {
            return null;
        } else if (!isRepeated()) {
            if (current.getTime() < time.getTime()) {
                return time;
            } else {
                return null;
            }
        } else if (isRepeated()) {
            Date res = null;
            for (Date i = new Date(start.getTime()); i.compareTo(end) <= 0; i.setTime(i.getTime() + interval * 1000)) {
                if (i.compareTo(current) > 0) {
                    res = new Date(i.getTime());
                    break;
                }
            }
            return res;
        } else {
            return null;
        }
    }

    /**
     * for cloning Task.
     *
     * @return cloned Task
     */
    public Task clone() {
        Object result = null;
        try {
            result = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if (this.next != null) {
            ((Task) result).next = this.next.clone();
        }
        return (Task) result;
    }

    /**
     * for comparing Tasks.
     *
     * @param o object to compare with
     * @return true if this Task and o are equal, false - if unequal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Task task = (Task) o;

        if (interval != task.interval) {
            return false;
        }
        if (active != task.active) {
            return false;
        }
        if (repeated != task.repeated) {
            return false;
        }
        if (title != null ? !title.equals(task.title) : task.title != null) {
            return false;
        }
        if (time != null ? !time.equals(task.time) : task.time != null) {
            return false;
        }
        if (start != null ? !start.equals(task.start) : task.start != null) {
            return false;
        }
        return end != null ? end.equals(task.end) : task.end == null;
    }

    /**
     * for getting hashcode of Task.
     *
     * @return hashCode of this Task
     */
    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (int) interval;
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (repeated ? 1 : 0);
        result = 31 * result + (next != null ? next.hashCode() : 0);
        return result;
    }

    /**
     * for describing Task in String.
     *
     * @return String that describes Task
     */
    @Override
    public String toString() {
        if (repeated) {
            return title + ", start: " + start + ", end: " + end + ", interval: " + TaskIO.calculateTime(interval)
                    + ", active: " + active + ", repeated: " + repeated;
        } else {
            return title + ", time: " + start + ", active: " + active + ", repeated: " + repeated;
        }
    }
}