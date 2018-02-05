package com.company.Controller;

import com.company.Model.Task;

import java.util.*;

public class TaskController{
    private static HashMap<String, ArrayList<Timer>> tasksToDo = new HashMap<>();
    private int count = 0;
    private ArrayList<Timer> timers = new ArrayList<>();

    public static HashMap<String, ArrayList<Timer>> getTasksToDo() {
        return tasksToDo;
    }

    private class ControlTime extends TimerTask {
        String task;
        public ControlTime(String task){
            this.task = task;
        }
        @Override
        public void run(){
            MainController.getConsoleView().getConsole().setLabel(task);
        }
    }

    public TaskController(Task task){
        if(!task.isRepeated()){
            timers.add(new Timer());
            timers.get(count).schedule(new ControlTime(task.getTitle() + " (" + task.getTime() + ")"), task.getTime());
            tasksToDo.put(task.getTitle(), timers);
            count++;
        }
        else {
            for(Date date = (Date)task.getStartTime().clone(); date.compareTo(task.getEndTime()) <= 0; date.setTime(date.getTime()
                    + task.getRepeatInterval())){
                timers.add(new Timer());
                timers.get(count).schedule(new ControlTime(task.getTitle() + " (" + date.toString() + ")"), date);
                tasksToDo.put(task.getTitle(), timers);
                count++;
            }
        }
        count = 0;
    }
}

