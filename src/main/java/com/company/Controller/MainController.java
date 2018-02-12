package com.company.Controller;

import com.company.Model.*;
import com.company.View.ConsoleView;
import com.company.View.menu.Menu;
import com.company.View.menu.MenuCallback;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
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

    public static boolean dateValidator(String dateToValidate) throws ParseException {
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

    public static void addTaskHandler() throws IOException, ParseException {
        Menu menu = new Menu(ConsoleView.getConsole());

        menu.add("Repeated", new MenuCallback() { public void Invoke() throws ParseException { repeatedTaskHandler(); } });
        menu.add("Non-repeated", new MenuCallback() { public void Invoke() throws ParseException { nonRepeatedTaskHandler(); } });
        menu.add("Back", new MenuCallback() { public void Invoke() { backHandler(); } });
        back = true;

        while(back) {
            consoleView.getConsole().setCountTo0();
            ConsoleView.getConsole().clear();
            System.out.println("Choose type of task:");
            menu.show();
        }
    }

    private static void repeatedTaskHandler() throws ParseException, NumberFormatException {
        Task task = null;
        task = new Task("1", new Date(), new Date(), 0);
        String dateStr;
        String title;
        String res;
        String warning;
        Date start = new Date();
        Date end = new Date();
        long interval;
        consoleView.getConsole().setCountTo0();
        System.out.println("Enter title:");
        res = scanner.nextLine();
        title = processBackspace(res);
        consoleView.getConsole().setCountTo0();
        warning = "Enter start time in format \"yyyy-MM-dd HH:mm\":";
        do {
            consoleView.getConsole().setCountTo0();
            System.out.println(warning);
            res = scanner.nextLine();
            dateStr = processBackspace(res);
            warning = "Error in your input, enter start time in format \"yyyy-MM-dd HH:mm\" again:";
        } while (!dateValidator(dateStr));

        try {
            start = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        consoleView.getConsole().setCountTo0();
        warning = "Enter end time in format \"yyyy-MM-dd HH:mm\":";
        do {
            consoleView.getConsole().setCountTo0();
            System.out.println(warning);
            res = scanner.nextLine();
            dateStr = processBackspace(res);
            warning = "Error in your input, enter end time in format \"yyyy-MM-dd HH:mm\" again:";
        } while (!dateValidator(dateStr));

        try {
            end = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        consoleView.getConsole().setCountTo0();
        while (true) {
            try {
                System.out.println("Enter interval in minutes:");
                consoleView.getConsole().setCountTo0();
                res = scanner.nextLine();
                dateStr = processBackspace(res);
                interval = Long.parseLong(dateStr);
                break;
            } catch (NumberFormatException nfe) {
                System.out.print("Error in your input, interval in minutes again: ");
            }
        }
        task = new Task(title, start, end, interval * 60000);
        arrayTaskList.add(task);
        taskControllers.add(new TaskController(task));
        logger.debug("Task added: " + task.toString());
        back = false;
    }

    public static void nonRepeatedTaskHandler() throws ParseException{
        Task task = null;
        task = new Task("1", new Date(), new Date(), 0);
        String res;
        String dateStr;
        String title;
        String warning;
        Date time = new Date();

        consoleView.getConsole().setCountTo0();
        System.out.println("Enter title:");
        res = scanner.nextLine();
        title = processBackspace(res);
        consoleView.getConsole().setCountTo0();
        warning = "Enter time in format \"yyyy-MM-dd HH:mm\":";
        do {
            consoleView.getConsole().setCountTo0();
            System.out.println(warning);
            res = scanner.nextLine();
            dateStr = processBackspace(res);
            warning = "Error in your input, enter time in format \"yyyy-MM-dd HH:mm\" again:";
        } while (!dateValidator(dateStr));

        try {
            time = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        task = new Task(title, time);
        arrayTaskList.add(task);
        taskControllers.add(new TaskController(task));
        logger.debug("Task added: " + task.toString());
        back = false;
    }

    public static void editTaskHandler() throws IOException, ParseException {
        Menu menu = new Menu(ConsoleView.getConsole());
        for(Task task: arrayTaskList) {
            menu.add(task.toString(), new MenuCallback() { public void Invoke() throws IOException, ParseException { selectionOfCharacteristicHandler(menu); } });
        }
        menu.add("Back", new MenuCallback() { public void Invoke() { backHandler(); } });
        back = true;

        while(back) {
            consoleView.getConsole().setCountTo0();
            ConsoleView.getConsole().clear();
            System.out.println("Choose task to edit:");
            menu.show();
        }
    }

    private static void selectionOfCharacteristicHandler(Menu menu) throws IOException, ParseException {
        taskNumber = menu.getChoosen();
        Menu menu1 = new Menu(ConsoleView.getConsole());
        consoleView.getConsole().setCountTo0();
        if(arrayTaskList.getTask(taskNumber - 1).isRepeated()) {
            menu1.add("Title", new MenuCallback() { public void Invoke() throws ParseException { editingOfCharacteristicHandler(1, menu); } });
            menu1.add("Start", new MenuCallback() { public void Invoke() throws ParseException { editingOfCharacteristicHandler(2, menu); } });
            menu1.add("End", new MenuCallback() { public void Invoke() throws ParseException { editingOfCharacteristicHandler(3, menu); } });
            menu1.add("Interval", new MenuCallback() { public void Invoke() throws ParseException { editingOfCharacteristicHandler(4, menu); } });
            menu1.add("Active", new MenuCallback() { public void Invoke() throws ParseException { editingOfCharacteristicHandler(5, menu); } });
            menu1.add("Type", new MenuCallback() { public void Invoke() throws ParseException { editingOfCharacteristicHandler(7, menu); } });
        } else {
            menu1.add("Title", new MenuCallback() { public void Invoke() throws ParseException { editingOfCharacteristicHandler(1, menu); } });
            menu1.add("Time", new MenuCallback() { public void Invoke() throws ParseException { editingOfCharacteristicHandler(6, menu); } });
            menu1.add("Active", new MenuCallback() { public void Invoke() throws ParseException { editingOfCharacteristicHandler(5, menu); } });
            menu1.add("Type", new MenuCallback() { public void Invoke() throws ParseException { editingOfCharacteristicHandler(7, menu); } });
        }
        menu1.add("Back", new MenuCallback() { public void Invoke() { backHandler(); } });
        back = true;

        while(back) {
            ConsoleView.getConsole().clear();
            System.out.println("Choose characteristic to edit:");
            menu1.show();
        }
    }

    private static void editingOfCharacteristicHandler(int i, Menu menu) throws ParseException, NullPointerException {
        String res;
        String res1;
        String warning;
        String dateStr;
        long interval;
        Date date = new Date();
        Date start = new Date();
        Date end = new Date();
        Task task =  arrayTaskList.getTask(taskNumber - 1);
        ArrayList<Timer> timers = TaskController.getTasksToDo().get(task.getTitle());
        boolean active = true;
        consoleView.getConsole().setCountTo0();
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
                warning = "Enter new start in format \"yyyy-MM-dd HH:mm\":";
                do {
                    consoleView.getConsole().setCountTo0();
                    System.out.println(warning);
                    res = scanner.nextLine();
                    res1 = processBackspace(res);
                    warning = "Error in your input, enter new start in format \"yyyy-MM-dd HH:mm\" again:";
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
                warning = "Enter new end in format \"yyyy-MM-dd HH:mm\":";
                do {
                    consoleView.getConsole().setCountTo0();
                    System.out.println(warning);
                    res = scanner.nextLine();
                    res1 = processBackspace(res);
                    warning = "Error in your input, enter new end in format \"yyyy-MM-dd HH:mm\" again:";
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
                while (true) {
                    try {
                        System.out.println("Enter new interval in minutes:");
                        consoleView.getConsole().setCountTo0();
                        res = scanner.nextLine();
                        dateStr = processBackspace(res);
                        interval = Long.parseLong(dateStr);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.print("Error in your input, enter new interval in minutes again:");
                        consoleView.getConsole().setCountTo0();
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
                    consoleView.getConsole().setCountTo0();
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
                warning = "Enter new time in format \"yyyy-MM-dd HH:mm\":";
                do {
                    consoleView.getConsole().setCountTo0();
                    System.out.println(warning);
                    res = scanner.nextLine();
                    res1 = processBackspace(res);
                    warning = "Error in your input, enter new time in format \"yyyy-MM-dd HH:mm\" again:";
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
            case 7:
                if(task.isRepeated()){
                    System.out.println("New type: non-repeated.");
                    consoleView.getConsole().setCountTo0();
                    warning = "Enter time in format \"yyyy-MM-dd HH:mm\":";
                    do {
                        consoleView.getConsole().setCountTo0();
                        System.out.println(warning);
                        res = scanner.nextLine();
                        dateStr = processBackspace(res);
                        warning = "Error in your input, enter time in format \"yyyy-MM-dd HH:mm\" again:";
                    } while (!dateValidator(dateStr));

                    try {
                        date = dateFormat.parse(dateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    task.setTime(date);
                    back = false;
                }
                else {
                    System.out.println("New type: repeated.");
                    consoleView.getConsole().setCountTo0();
                    warning = "Enter start time in format \"yyyy-MM-dd HH:mm\":";
                    do {
                        consoleView.getConsole().setCountTo0();
                        System.out.println(warning);
                        res = scanner.nextLine();
                        dateStr = processBackspace(res);
                        warning = "Error in your input, enter start time in format \"yyyy-MM-dd HH:mm\" again:";
                    } while (!dateValidator(dateStr));

                    try {
                        start = dateFormat.parse(dateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    consoleView.getConsole().setCountTo0();
                    warning = "Enter end time in format \"yyyy-MM-dd HH:mm\":";
                    do {
                        consoleView.getConsole().setCountTo0();
                        System.out.println(warning);
                        res = scanner.nextLine();
                        dateStr = processBackspace(res);
                        warning = "Error in your input, enter end time in format \"yyyy-MM-dd HH:mm\" again:";
                    } while (!dateValidator(dateStr));

                    try {
                        end = dateFormat.parse(dateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    consoleView.getConsole().setCountTo0();
                    while (true) {
                        try {
                            System.out.println("Enter interval in minutes:");
                            consoleView.getConsole().setCountTo0();
                            res = scanner.nextLine();
                            dateStr = processBackspace(res);
                            interval = Long.parseLong(dateStr);
                            break;
                        } catch (NumberFormatException nfe) {
                            System.out.print("Error in your input, interval in minutes again: ");
                        }
                    }
                    task.setTime(start, end, interval * 60000);
                    back = false;
                }
                TaskController.getTasksToDo().get(task.getTitle()).clear();
                TaskController.getTasksToDo().remove(task.getTitle());
                for(Timer timer: timers){
                    timer.cancel();
                    timer.purge();
                }
                taskControllers.add(new TaskController(task));
            default:
                break;
        }
        menu.update(arrayTaskList);
        logger.debug("Task edited: " + task.toString());
        back = false;
        back1 = false;
    }

    public static void deleteTaskHandler() throws IOException, ParseException {
        Menu menu = new Menu(ConsoleView.getConsole());
        for(Task task: arrayTaskList) {
            menu.add(task.toString(), new MenuCallback() { public void Invoke() { deleteTaskHandler(menu); } });
        }
        menu.add("Back", new MenuCallback() { public void Invoke() { backHandler(); } });
        back = true;

        while(back) {
            consoleView.getConsole().setCountTo0();
            ConsoleView.getConsole().clear();
            System.out.println("Choose task to delete:");
            menu.show();
        }
    }

    private static void deleteTaskHandler(Menu menu) {
        taskNumber = menu.getChoosen() - 1;
        Task task = arrayTaskList.getTask(taskNumber);
        logger.debug("Task deleted: " + task.toString());
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

    public static void listTaskHandler() throws IOException, ParseException {
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
            consoleView.getConsole().setCountTo0();
            menu.show();
        }
    }

    public static void calendarTaskHandler() throws ParseException, IOException {
        Menu menu = new Menu(ConsoleView.getConsole());
        Date start = null;
        Date end = null;
        String res;
        String res1;
        String warning = "Enter start in format \"yyyy-MM-dd HH:mm\":";
        do {
            consoleView.getConsole().setCountTo0();
            System.out.println(warning);
            res = scanner.nextLine();
            res1 = processBackspace(res);
            warning = "Error in your input, enter start in format \"yyyy-MM-dd HH:mm\" again:";
        } while (!dateValidator(res1));

        try {
            start = dateFormat.parse(res1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        warning = "Enter end in format \"yyyy-MM-dd HH:mm\":";
        do {
            consoleView.getConsole().setCountTo0();
            System.out.println(warning);
            res = scanner.nextLine();
            res1 = processBackspace(res);
            warning = "Error in your input, enter end in format \"yyyy-MM-dd HH:mm\" again:";
        } while (!dateValidator(res1));

        try {
            end = dateFormat.parse(res1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Map<Date, Set<Task>> calendar = Tasks.calendar(arrayTaskList, start, end);
        for(Map.Entry<Date, Set<Task>> entry: calendar.entrySet()) {
            Set<Task> set = entry.getValue();
            menu.add(entry.getKey().toString(), new MenuCallback() { public void Invoke() throws IOException, ParseException { dates(set); } });
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
            consoleView.getConsole().setCountTo0();
            menu.show();
        }
    }

    private static void dates(Set<Task> set) throws IOException, ParseException {
        back1 = true;
        Menu menu = new Menu(ConsoleView.getConsole());
        menu.add("Back", new MenuCallback() { public void Invoke() { backHandler1(); } });
        while(back1) {
            consoleView.getConsole().setCountTo0();
            ConsoleView.getConsole().clear();
            for(Task task: set){
                System.out.println(task.toString());
            }
            consoleView.getConsole().setCountTo0();
            menu.show();
        }
    }

    public static void newFileTaskHandler(Menu menu){
        System.out.println("Enter name of new file:");
        consoleView.getConsole().setCountTo0();
        String res = scanner.nextLine();
        String name = processBackspace(res);
        file = new File("TaskLists\\" + name + ".txt");
        menu.clear();
        menu.add("Add new task", new MenuCallback() { public void Invoke() throws IOException, ParseException { addTaskHandler(); } });
        menu.add("Edit task", new MenuCallback() { public void Invoke() throws IOException, ParseException { editTaskHandler(); } });
        menu.add("Delete task", new MenuCallback() { public void Invoke() throws IOException, ParseException { deleteTaskHandler(); } });
        menu.add("Task list", new MenuCallback() { public void Invoke() throws IOException, ParseException { listTaskHandler(); } });
        menu.add("Calendar", new MenuCallback() { public void Invoke() throws ParseException, IOException { calendarTaskHandler(); } });
        menu.add("Save list to file", new MenuCallback() { public void Invoke() throws IOException { saveToFileHandler(); } });
        menu.add("Exit", new MenuCallback() { public void Invoke() { exitHandler(); } });
    }

    public static void existingFileTaskHandler(Menu menu) throws IOException, ParseException {
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
            menu.add("Add new task", new MenuCallback() { public void Invoke() throws IOException, ParseException { addTaskHandler(); } });
            menu.add("Edit task", new MenuCallback() { public void Invoke() throws IOException, ParseException { editTaskHandler(); } });
            menu.add("Delete task", new MenuCallback() { public void Invoke() throws IOException, ParseException { deleteTaskHandler(); } });
            menu.add("Task list", new MenuCallback() { public void Invoke() throws IOException, ParseException { listTaskHandler(); } });
            menu.add("Calendar", new MenuCallback() { public void Invoke() throws ParseException, IOException { calendarTaskHandler(); } });
            menu.add("Save list to file", new MenuCallback() { public void Invoke() throws IOException { saveToFileHandler(); } });
            menu.add("Exit", new MenuCallback() { public void Invoke() { exitHandler(); } });
        }
    }

    public static void continueFileTaskHandler(Menu menu) throws IOException, ParseException {
        File f = new File("TaskLists\\lastFile.txt");
        if(f.exists() && !f.isDirectory()) {
            try {
                fileName = new Scanner(f).useDelimiter("\\Z").next();
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
                menu.add("Add new task", new MenuCallback() { public void Invoke() throws IOException, ParseException { addTaskHandler(); } });
                menu.add("Edit task", new MenuCallback() { public void Invoke() throws IOException, ParseException { editTaskHandler(); } });
                menu.add("Delete task", new MenuCallback() { public void Invoke() throws IOException, ParseException { deleteTaskHandler(); } });
                menu.add("Task list", new MenuCallback() { public void Invoke() throws IOException, ParseException { listTaskHandler(); } });
                menu.add("Calendar", new MenuCallback() { public void Invoke() throws ParseException, IOException { calendarTaskHandler(); } });
                menu.add("Save list to file", new MenuCallback() { public void Invoke() throws IOException { saveToFileHandler(); } });
                menu.add("Exit", new MenuCallback() { public void Invoke() { exitHandler(); } });
            }
        }
        else {
            menu.remove(2);
            System.out.println("There is no last file to work");
        }
    }

    public static void exit() throws IOException {
        if (arrayTaskList != null && file != null) {
            TaskIO.writeText(arrayTaskList, file);
            try (PrintWriter out = new PrintWriter("TaskLists\\lastFile.txt")) {
                out.println(file.getName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        logger.debug("Session ended");
    }

    public static void saveToFileHandler() throws IOException{
        if (arrayTaskList != null && file != null) {
            TaskIO.writeText(arrayTaskList, file);
            try (PrintWriter out = new PrintWriter("TaskLists\\lastFile.txt")) {
                out.println(file.getName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        logger.debug("Saved to file \"" + fileName + "\"");
    }

    public static String processBackspace(String input) {
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

    public static void main(String[] args) throws IOException, ParseException {
        consoleView = new ConsoleView();
        scanner = new Scanner(System.in);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        taskControllers = new ArrayList<>();
        arrayTaskList = new ArrayTaskList();
        logger.debug("Session started");
        File dir = new File("TaskLists");
        if(!dir.exists()){
            new File("TaskLists").mkdir();
        }
        consoleView.getMenu().add("New file", new MenuCallback() { public void Invoke() { newFileTaskHandler(consoleView.getMenu()); } });
        consoleView.getMenu().add("Existing file", new MenuCallback() { public void Invoke() throws IOException, ParseException { existingFileTaskHandler(consoleView.getMenu()); } });
        consoleView.getMenu().add("Continue last file", new MenuCallback() { public void Invoke() throws IOException, ParseException { continueFileTaskHandler(consoleView.getMenu()); } });
        consoleView.getMenu().add("Exit", new MenuCallback() { public void Invoke() { exitHandler(); } });
        while(MainController.isExit()) {
            consoleView.getConsole().clear();
            System.out.println("Please choose an option:");
            consoleView.getMenu().show();
        }
        MainController.exit();
        System.exit(0);
    }
}
