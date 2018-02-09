package com.company.Controller;

import com.company.Model.*;
import com.company.View.ConsoleView;
import com.company.View.menu.Menu;
import com.company.View.menu.MenuCallback;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;
import org.apache.log4j.Logger;

public class MainController {
    final static Logger logger = Logger.getLogger(MainController.class);
    private static boolean exit = true;
    private static boolean back = true;
    private static boolean back1 = true;
    private static boolean back2 = true;
    private static boolean back3 = true;
    private static int taskNumber;
    private static Scanner scanner;
    private static DateFormat dateFormat;
    private static ArrayList<TaskController> taskControllers;
    private static JFileChooser fileopen;
    private static File file;
    private static ArrayTaskList arrayTaskList;
    private static ConsoleView consoleView;
    public static ArrayTaskList getArrayTaskList() {
        return arrayTaskList;
    }
    private static String fileName = "";

    public static ConsoleView getConsoleView() {
        return consoleView;
    }

    public static File getFile() {
        return file;
    }

    public static boolean isExit() {
        return exit;
    }

    public static boolean isBack() {
        return back;
    }

    public static boolean dateValidator(String dateToValidate){
        if(dateToValidate == null){
            return false;
        }
        dateFormat.setLenient(false);
        try {
            Date date = dateFormat.parse(dateToValidate);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    public static void exitHandler() {
        exit = false;
    }

    public static void backHandler(){
        back = false;
    }

    public static void backHandler1(){
        back1 = false;
    }

    public static void backHandler2(){
        back2 = false;
    }

    public static void backHandler3(){
        back3 = false;
    }

    public static void addTaskHandler(){
        Menu menu = new Menu(ConsoleView.getConsole());

        menu.add("Repeated", new MenuCallback() { public void Invoke() { repeatedTaskHandler(); } });
        menu.add("Non-repeated", new MenuCallback() { public void Invoke() { nonRepeatedTaskHandler(); } });
        menu.add("Back", new MenuCallback() { public void Invoke() { backHandler(); } });
        back = true;

        while(back) {
            ConsoleView.getConsole().clear();
            System.out.println("Choose type of task:");
            menu.show();
        }
    }

    public static void repeatedTaskHandler(){
        Task task = null;
        task = new Task("1", new Date(), new Date(), 0);
        String dateStr;
        String title;
        String res;
        Date start = new Date();
        Date end = new Date();

        System.out.println("Enter title:");
        res = scanner.nextLine();
        title = processBackspace(res);
        do {
            System.out.println("Enter start time in format \"yyyy-MM-dd HH:mm\":");
            res = scanner.nextLine();
            dateStr = processBackspace(res);
        } while (!dateValidator(dateStr));

        try {
            start = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        do {
            System.out.println("Enter end time in format \"yyyy-MM-dd HH:mm\":");
            res = scanner.nextLine();
            dateStr = processBackspace(res);
        } while (!dateValidator(dateStr));

        try {
            end = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("Enter interval in minutes:");
        int interval;
        while (true) {
            try {
                interval = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException nfe) {
                System.out.print("Enter interval in minutes: ");
            }
        }
        task = new Task(title, start, end, interval * 60000);
        arrayTaskList.add(task);
        taskControllers.add(new TaskController(task));
        logger.info("Task added: " + task.toString());
        back = false;
    }

    public static void nonRepeatedTaskHandler(){
        Task task = null;
        task = new Task("1", new Date(), new Date(), 0);
        String res;
        String dateStr;
        String title;
        Date time = new Date();

        System.out.println("Enter title:");
        res = scanner.nextLine();
        title = processBackspace(res);
        System.out.println("Enter time in format \"yyyy-MM-dd HH:mm\":");
        do {
            res = scanner.nextLine();
            dateStr = processBackspace(res);
        } while (!dateValidator(dateStr));

        try {
            time = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        task = new Task(title, time);
        arrayTaskList.add(task);
        taskControllers.add(new TaskController(task));
        logger.info("Task added: " + task.toString());
        back = false;
    }

    public static void editTaskHandler(){
        Menu menu = new Menu(ConsoleView.getConsole());
        for(Task task: arrayTaskList) {
            menu.add(task.toString(), new MenuCallback() { public void Invoke() { selectionOfCharacteristicHandler(menu); } });
        }
        menu.add("Back", new MenuCallback() { public void Invoke() { backHandler(); } });
        back = true;

        while(back) {
            ConsoleView.getConsole().clear();
            System.out.println("Choose task to edit:");
            menu.show();
        }
    }

    private static void selectionOfCharacteristicHandler(Menu menu){
        taskNumber = menu.getChoosen();
        Menu menu1 = new Menu(ConsoleView.getConsole());
        if(arrayTaskList.getTask(taskNumber - 1).isRepeated()) {
            menu1.add("Title", new MenuCallback() { public void Invoke() { editingOfCharacteristicHandler(1, menu); } });
            menu1.add("Start", new MenuCallback() { public void Invoke() { editingOfCharacteristicHandler(2, menu); } });
            menu1.add("End", new MenuCallback() { public void Invoke() { editingOfCharacteristicHandler(3, menu); } });
            menu1.add("Interval", new MenuCallback() { public void Invoke() { editingOfCharacteristicHandler(4, menu); } });
            menu1.add("Active", new MenuCallback() { public void Invoke() { editingOfCharacteristicHandler(5, menu); } });
        } else {
            menu1.add("Title", new MenuCallback() { public void Invoke() { editingOfCharacteristicHandler(1, menu); } });
            menu1.add("Time", new MenuCallback() { public void Invoke() { editingOfCharacteristicHandler(6, menu); } });
            menu1.add("Active", new MenuCallback() { public void Invoke() { editingOfCharacteristicHandler(5, menu); } });
        }
        menu1.add("Back", new MenuCallback() { public void Invoke() { backHandler1(); } });
        back1 = true;

        while(back1) {
            ConsoleView.getConsole().clear();
            System.out.println("Choose characteristic to edit:");
            menu1.show();
        }
    }

    private static void editingOfCharacteristicHandler(int i, Menu menu){
        String res;
        String res1;
        int interval;
        Date date = new Date();
        Task task =  arrayTaskList.getTask(taskNumber - 1);
        ArrayList<Timer> timers = TaskController.getTasksToDo().get(task.getTitle());
        boolean active = true;
        switch (i){
            case 1:
                System.out.println("Enter new title:");
                res = scanner.nextLine();
                String title = processBackspace(res);
                TaskController.getTasksToDo().get(task.getTitle()).clear();
                TaskController.getTasksToDo().remove(task.getTitle());
                task.setTitle(title);
                for(Timer timer: timers){
                    timer.cancel();
                    timer.purge();
                }
                taskControllers.add(new TaskController(task));
                break;
            case 2:
                System.out.println("Enter new start:");
                do {
                    res = scanner.nextLine();
                    res1 = processBackspace(res);
                } while (!dateValidator(res1));

                try {
                    date = dateFormat.parse(res1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                task.setStart(date);
                TaskController.getTasksToDo().get(task.getTitle());
                for(Timer timer: timers){
                    timer.cancel();
                    timer.purge();
                }
                TaskController.getTasksToDo().get(task.getTitle()).clear();
                TaskController.getTasksToDo().remove(task.getTitle());
                break;
            case 3:
                System.out.println("Enter new end:");
                do {
                    res = scanner.nextLine();
                    res1 = processBackspace(res);
                } while (!dateValidator(res1));

                try {
                    date = dateFormat.parse(res1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                task.setEnd(date);
                for(Timer timer: timers){
                    timer.cancel();
                    timer.purge();
                }
                TaskController.getTasksToDo().get(task.getTitle()).clear();
                TaskController.getTasksToDo().remove(task.getTitle());
                break;
            case 4:
                System.out.println("Enter new interval in minutes:");
                while (true) {
                    try {
                        interval = Integer.parseInt(scanner.nextLine());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.print("Enter new interval in minutes:");
                    }
                    for(Timer timer: timers){
                        timer.cancel();
                        timer.purge();
                    }
                    TaskController.getTasksToDo().get(task.getTitle()).clear();
                    TaskController.getTasksToDo().remove(task.getTitle());
                }
                task.setInterval(interval * 60000);
                break;
            case 5:
                System.out.println("Enter new active:");
                while(true) {
                    res = scanner.nextLine();
                    res1 = processBackspace(res);
                    if(res1.equals("true")){
                        active = true;
                        break;
                    }
                    else if(res1.equals("false")){
                        active = false;
                        for(Timer timer: timers){
                            timer.cancel();
                            timer.purge();
                        }
                        TaskController.getTasksToDo().get(task.getTitle()).clear();
                        TaskController.getTasksToDo().remove(task.getTitle());
                        break;
                    }
                }
                task.setActive(active);
                break;
            case 6:
                System.out.println("Enter new time:");
                do {
                    res = scanner.nextLine();
                    res1 = processBackspace(res);
                } while (!dateValidator(res1));

                try {
                    date = dateFormat.parse(res1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                task.setTime(date);
                for(Timer timer: timers){
                    timer.cancel();
                    timer.purge();
                }
                TaskController.getTasksToDo().get(task.getTitle()).clear();
                TaskController.getTasksToDo().remove(task.getTitle());
                break;
            default:
                break;
        }
        menu.update(arrayTaskList);
        logger.info("Task edited: " + task.toString());
        back = false;
        back1 = false;
    }

    public static void deleteTaskHandler() {
        Menu menu = new Menu(ConsoleView.getConsole());
        for(Task task: arrayTaskList) {
            menu.add(task.toString(), new MenuCallback() { public void Invoke() { deleteTaskHandler(menu); } });
        }
        menu.add("Back", new MenuCallback() { public void Invoke() { backHandler(); } });
        back = true;

        while(back) {
            ConsoleView.getConsole().clear();
            System.out.println("Choose task to delete:");
            menu.show();
        }
    }

    private static void deleteTaskHandler(Menu menu) {
        taskNumber = menu.getChoosen() - 1;
        Task task = arrayTaskList.getTask(taskNumber);
        logger.info("Task deleted: " + task.toString());
        ArrayList<Timer> timers = TaskController.getTasksToDo().get(task.getTitle());
        for(Timer timer: timers){
            timer.cancel();
            timer.purge();
        }
        TaskController.getTasksToDo().get(task.getTitle()).clear();
        TaskController.getTasksToDo().remove(task.getTitle());
        arrayTaskList.remove(arrayTaskList.getTask(taskNumber));
        menu.remove(taskNumber);
        back = false;
    }

    public static void listTaskHandler() {
        Menu menu = new Menu(ConsoleView.getConsole());
        menu.add("Back", new MenuCallback() { public void Invoke() { backHandler(); } });
        back = true;

        while(back) {
            ConsoleView.getConsole().clear();
            if (arrayTaskList.size() != 0){
                System.out.println("Your tasks:");
                for (Task task : arrayTaskList) {
                    System.out.println(task.toString());
                    System.out.println();
                }
            }
            else {
                System.out.println("No tasks in your list yet");
            }
            menu.show();
        }
    }

    public static void calendarTaskHandler() {
        Menu menu = new Menu(ConsoleView.getConsole());
        Date start = null;
        Date end = null;
        String string;
        System.out.println("Enter start time in format \"yyyy-MM-dd HH:mm\":");
        do {
            string = scanner.nextLine();
        } while (!dateValidator(string));

        try {
            start = dateFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("Enter end time in format \"yyyy-MM-dd HH:mm\":");
        do {
            string = scanner.nextLine();
        } while (!dateValidator(string));

        try {
            end = dateFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Map<Date, Set<Task>> calendar = Tasks.calendar(arrayTaskList, start, end);
        for(Map.Entry<Date, Set<Task>> entry: calendar.entrySet()) {
            Set<Task> set = entry.getValue();
            menu.add(entry.getKey().toString(), new MenuCallback() { public void Invoke() { dates(set); } });
        }
        menu.add("Back", new MenuCallback() { public void Invoke() { backHandler(); } });
        back = true;

        while(back) {
            ConsoleView.getConsole().clear();
            if(calendar.size() != 0) {
                System.out.println("Your tasks for: " + start.toString() + " - " + end.toString());
            }
            else {
                System.out.println("No tasks for: " + start.toString() + " - " + end.toString());
            }
            menu.show();
        }
    }

    private static void dates(Set<Task> set) {
        back3 = true;
        Menu menu = new Menu(ConsoleView.getConsole());
        menu.add("Back", new MenuCallback() { public void Invoke() { backHandler3(); } });
        while(back3) {
            ConsoleView.getConsole().clear();
            for(Task task: set){
                System.out.println(task.toString());
            }
            menu.show();
        }
    }

    public static void newFileTaskHandler(Menu menu ){
        System.out.println("Enter name of new file:");
        String name = scanner.nextLine();
        file = new File(name + ".txt");
        menu.clear();
        menu.add("Add new task", new MenuCallback() { public void Invoke() { MainController.addTaskHandler(); } });
        menu.add("Edit task", new MenuCallback() { public void Invoke() { MainController.editTaskHandler(); } });
        menu.add("Delete task", new MenuCallback() { public void Invoke() { MainController.deleteTaskHandler(); } });
        menu.add("Task list", new MenuCallback() { public void Invoke() { MainController.listTaskHandler(); } });
        menu.add("Calendar", new MenuCallback() { public void Invoke() { MainController.calendarTaskHandler(); } });
        menu.add("Exit", new MenuCallback() { public void Invoke() { MainController.exitHandler(); } });
    }

    public static void existingFileTaskHandler(Menu menu) {
        fileopen = new JFileChooser();
        fileopen.setCurrentDirectory(new File("."));
        int ret = fileopen.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            file = fileopen.getSelectedFile();

        }
        if(file != null) {
            TaskIO.readText(arrayTaskList, file);
            for(Task task: arrayTaskList){
                taskControllers.add(new TaskController(task));
            }
            menu.clear();
            menu.add("Add new task", new MenuCallback() {
                public void Invoke() {
                    MainController.addTaskHandler();
                }
            });
            menu.add("Edit task", new MenuCallback() {
                public void Invoke() {
                    MainController.editTaskHandler();
                }
            });
            menu.add("Delete task", new MenuCallback() {
                public void Invoke() {
                    MainController.deleteTaskHandler();
                }
            });
            menu.add("Task list", new MenuCallback() {
                public void Invoke() {
                    MainController.listTaskHandler();
                }
            });
            menu.add("Calendar", new MenuCallback() {
                public void Invoke() {
                    MainController.calendarTaskHandler();
                }
            });
            menu.add("Exit", new MenuCallback() {
                public void Invoke() {
                    MainController.exitHandler();
                }
            });
        }
    }

    public static void continueFileTaskHandler(Menu menu) {
        try {
            fileName = new Scanner(new File("lastFile.txt")).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        file = new File(fileName);
        if(file != null) {
            TaskIO.readText(arrayTaskList, file);
            for(Task task: arrayTaskList){
                taskControllers.add(new TaskController(task));
            }
            menu.clear();
            menu.add("Add new task", new MenuCallback() {
                public void Invoke() {
                    MainController.addTaskHandler();
                }
            });
            menu.add("Edit task", new MenuCallback() {
                public void Invoke() {
                    MainController.editTaskHandler();
                }
            });
            menu.add("Delete task", new MenuCallback() {
                public void Invoke() {
                    MainController.deleteTaskHandler();
                }
            });
            menu.add("Task list", new MenuCallback() {
                public void Invoke() {
                    MainController.listTaskHandler();
                }
            });
            menu.add("Calendar", new MenuCallback() {
                public void Invoke() {
                    MainController.calendarTaskHandler();
                }
            });
            menu.add("Exit", new MenuCallback() {
                public void Invoke() {
                    MainController.exitHandler();
                }
            });
        }
    }

    public static void exit() {
        if(arrayTaskList != null && file != null) {
            TaskIO.writeText(arrayTaskList, file);
        }
        try(PrintWriter out = new PrintWriter("lastFile.txt")  ){
            out.println(file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        logger.info("Session ended");
    }

    private static String processBackspace(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c == '\b') {
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        consoleView = new ConsoleView();
        scanner = new Scanner(System.in);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        taskControllers = new ArrayList<>();
        arrayTaskList = new ArrayTaskList();
        logger.info("Session started");
        consoleView.run();
    }
}
