package com.company.Model;

import java.util.*;

public class Tasks {
    public static Iterable<Task> incoming(Iterable<Task> tasks, Date start, Date end) {
        TaskList incomingTasks = new ArrayTaskList();
        for (Task task: tasks) {
            if (task.nextTimeAfter(start) != null && task.nextTimeAfter(start).compareTo(end) <= 0) {
                incomingTasks.add(task);
            }
        }

        return incomingTasks;
    }

    public static SortedMap<Date, Set<Task>> calendar(Iterable<Task> tasks, Date start, Date end) throws NullPointerException {
        SortedMap<Date, Set<Task>> sortedMap = new TreeMap<>();
        try {
            for (Task task : tasks) {
                ArrayList<Date> dates = new ArrayList<>();
                Date date = task.nextTimeAfter(start);
                if (task.isRepeated() && date != null && date.compareTo(end) <= 0) {
                    for (Date res = new Date(date.getTime()); res.compareTo(task.getEndTime()) <= 0; res.setTime(res.getTime() + task.getRepeatInterval())) {
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
        } catch (NullPointerException e) {}

        return sortedMap;
    }
}
