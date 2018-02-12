package com.company.model;

import java.util.Date;
import java.util.Iterator;

/**
 * abstract class with prototypes of main methods for ArrayTaskList.
 *
 * @author MishchenkoAnton
 * @version MavenAndLog4j
 */
public abstract class TaskList implements Iterable<Task> {
    private static final long serialVersionUID = 2L;

    public abstract void add(Task task) throws NullPointerException;

    public abstract int size();

    public abstract boolean remove(Task task) throws NullPointerException;

    public abstract Task getTask(int index) throws IndexOutOfBoundsException;

    /**
     * for getting tasks from interval of time.
     *
     * @param from this time
     * @param to   this time
     * @return ArrayTaskList with tasks from interval of time
     */
    public TaskList incoming(Date from, Date to) {
        ArrayTaskList incomingTasks = new ArrayTaskList();
        for (int i = 0; i < size(); i++) {
            if (getTask(i).nextTimeAfter(from) != null && getTask(i).nextTimeAfter(from).compareTo(to) <= 0) {
                incomingTasks.add(getTask(i));
            }
        }
        return incomingTasks;
    }

    @Override
    public abstract Iterator<Task> iterator();
}
