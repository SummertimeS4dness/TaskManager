package com.company.model;

import java.util.*;
import org.apache.log4j.Logger;

/**
 * class with methods for getting tasks from interval.
 *
 * @author MishchenkoAnton
 * @version MavenAndLog4j
 */
public class Tasks {
    final static Logger logger = Logger.getLogger(Tasks.class);

    /**
     * for getting tasks from interval of time.
     *
     * @param tasks list with tasks
     * @param start this time
     * @param end   this time
     * @return ArrayTaskList with tasks from interval of time
     */
    public static Iterable<Task> incoming(Iterable<Task> tasks, Date start, Date end) {
        TaskList incomingTasks = new ArrayTaskList();
        for (Task task : tasks) {
            if (task.nextTimeAfter(start) != null && task.nextTimeAfter(start).compareTo(end) <= 0) {
                incomingTasks.add(task);
            }
        }

        return incomingTasks;
    }

    /**
     * for getting calendar with tasks from interval of time.
     *
     * @param tasks list with tasks
     * @param start this time
     * @param end   this time
     * @return SortedMap with tasks from interval of time
     */
    public static SortedMap<Date, Set<Task>> calendar(Iterable<Task> tasks, Date start, Date end)
            throws NullPointerException {
        SortedMap<Date, Set<Task>> sortedMap = new TreeMap<>();
        try {
            for (Task task : tasks) {
                ArrayList<Date> dates = new ArrayList<>();
                Date date = task.nextTimeAfter(start);
                if (task.isRepeated() && date != null && date.compareTo(end) <= 0) {
                    for (Date res = new Date(date.getTime()); res.compareTo(task.getEndTime()) <= 0;
                         res.setTime(res.getTime() + task.getRepeatInterval())) {
                        if (sortedMap.get(res) != null) {
                            sortedMap.get(res).add(new Task(task));
                        } else {
                            Set<Task> toAdd = new HashSet<>();
                            toAdd.add(new Task(task));
                            sortedMap.put(new Date(res.getTime()), toAdd);
                        }
                    }
                } else if (!task.isRepeated() && date != null) {
                    if (task.getTime().compareTo(start) >= 0 && task.getTime().compareTo(end) <= 0) {
                        if (sortedMap.get(task.getTime()) != null) {
                            sortedMap.get(task.getTime()).add(new Task(task));
                        } else {
                            Set<Task> toAdd = new HashSet<>();
                            toAdd.add(new Task(task));
                            sortedMap.put(new Date(task.getTime().getTime()), toAdd);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            logger.error(e);
        }

        return sortedMap;
    }
}
