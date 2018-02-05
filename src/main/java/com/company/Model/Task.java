package com.company.Model;

import java.util.Date;

public class Task implements Cloneable {
    private static final long serialVersionUID = 1L;
    private String title;
    private Date time;
    private Date start;
    private Date end;
    private int interval;
    private boolean active;
    private boolean repeated;
    private Task next;

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

    public Task getNext() {
        return next;
    }

    public void setNext(Task next) throws NullPointerException {
        if (next == null) {
            throw new NullPointerException("Next to set is null");
        }
        this.next = next;
    }
    public void setStart(Date start) {
        this.start = new Date(start.getTime());
    }

    public void setEnd(Date end) {
        this.end = new Date(end.getTime());
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
    public void setNextForRemove(Task next) {
        this.next = next;
    }

    public Task(String title, Date time){
        this.title = title;
        this.time = new Date(time.getTime());
        this.repeated = false;
        start = new Date(time.getTime());
        end = new Date(time.getTime());
        interval = 0;
        active = true;
    }

    public Task(String title, Date start, Date end, int interval) {
        this.title = title;
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        this.interval = interval;
        this.repeated = true;
        active = true;
    }

    public String getTitle() {
        return title;
    }

    public boolean isActive() {
        return active;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getStartTime() {
        return start;
    }

    public Date getEndTime() {
        return end;
    }

    public Date getTime() {
        if (isRepeated()) {
            return start;
        }
        return time;
    }

    public void setTime(Date time) {
        repeated = false;
        this.time = new Date(time.getTime());
        start = new Date(time.getTime());
        end = new Date(time.getTime());
        interval = 0;
    }

    public int getRepeatInterval() {
        if (!isRepeated()) {
            return 0;
        } else {
            return interval;
        }
    }
    public void setTime(Date start, Date end, int interval) {
        repeated = true;
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        this.interval = interval;
    }

    public boolean isRepeated() {
        return repeated;
    }
    
    public Date nextTimeAfter(Date current) {
        if (!isActive()) {
            return null;
        } else if(!isRepeated()) {
            if (current.getTime() < time.getTime()) {
                return time;
            } else {
                return null;
            }
        } else if(isRepeated()) {
            Date res = null;
            for (Date i = new Date(start.getTime()); i.compareTo(end) <= 0; i.setTime(i.getTime()+ interval * 1000)) {
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

    public Task clone() {
        Object result = null;
        try {
            result = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if (this.next != null) {
            ((Task)result).next = this.next.clone();
        }
        return (Task)result;
    }

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

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + interval;
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (repeated ? 1 : 0);
        result = 31 * result + (next != null ? next.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if(repeated) {
            return title + ", start: " + start + ", end: " + end + ", interval: " + TaskIO.calculateTime(interval)
                    + ", active: " + active + ", repeated: " + repeated;
        }
        else {
            return title + ", time: " + start + ", active: " + active + ", repeated: " + repeated;
        }
    }
}