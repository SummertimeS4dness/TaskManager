package com.company.controller;

import com.company.model.*;
import com.company.view.ConsoleView;
import com.company.view.menu.Menu;
import com.company.view.menu.Menu.MenuCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;

import org.apache.log4j.Logger;

/**
 * Main controller of TaskManager.
 *.\
 * @author MishchenkoAnton
 * @version MavenAndLog4j
 */

public class MainController {
    private final static Logger logger = Logger.getLogger(MainController.class);
    private static boolean exit = true;
    private static boolean back = true;
    private static boolean back1 = true;
    private static int taskNumber;
    private static Scanner scanner;
    private static DateFormat dateFormat;
    private static JFileChooser fileopen;
    private static File file;
    private static ArrayTaskList arrayTaskList;
    private static ConsoleView consoleView;
    private static String fileName = "";

    /**
     * getter for current file with task list.
     *
     * @return current file with task list
     */
    public static File getFile() {
        return file;
    }

    /**
     * getter for ArrayTaskList with current tasks.
     *
     * @return ArrayTaskList with current tasks
     */
    public static ArrayTaskList getArrayTaskList() {
        return arrayTaskList;
    }

    private static boolean isExit() {
        return exit;
    }

    private static boolean dateValidator(String dateToValidate) throws ParseException {
        if (dateToValidate == null) {
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

    private static void exitHandler() {
        exit = false;
    }

    private static void backHandler() {
        back = false;
    }

    private static void backHandler1() {
        back1 = false;
    }

    private static void addTaskHandler() throws IOException, ParseException {
        Menu menu = new Menu(ConsoleView.getConsole());

        menu.add("Repeated", new Menu.MenuCallback() {
            public void Invoke() throws ParseException {
                repeatedTaskHandler();
            }
        });
        menu.add("Non-repeated", new Menu.MenuCallback() {
            public void Invoke() throws ParseException {
                nonRepeatedTaskHandler();
            }
        });
        menu.add("Back", new Menu.MenuCallback() {
            public void Invoke() {
                backHandler();
            }
        });
        back = true;

        while (back) {
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
            logger.error(e);
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
            logger.error(e);
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
                System.out.print("Error in your input, enter interval in minutes again: ");
                logger.error(nfe);
            }
        }
        task = new Task(title, start, end, interval * 60000);
        arrayTaskList.add(task);
        logger.debug("Task added: " + task.toString());
        back = false;
    }

    private static void nonRepeatedTaskHandler() throws ParseException {
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
            logger.error(e);
        }
        task = new Task(title, time);
        arrayTaskList.add(task);
        logger.debug("Task added: " + task.toString());
        back = false;
    }

    private static void editTaskHandler() throws IOException, ParseException {
        Menu menu = new Menu(ConsoleView.getConsole());
        for (Task task : arrayTaskList) {
            menu.add(task.toString(), new Menu.MenuCallback() {
                public void Invoke() throws IOException, ParseException {
                    selectionOfCharacteristicHandler(menu);
                }
            });
        }
        menu.add("Back", new Menu.MenuCallback() {
            public void Invoke() {
                backHandler();
            }
        });
        back = true;

        while (back) {
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
        if (arrayTaskList.getTask(taskNumber - 1).isRepeated()) {
            menu1.add("Title", new Menu.MenuCallback() {
                public void Invoke() throws ParseException {
                    editingOfCharacteristicHandler(1, menu);
                }
            });
            menu1.add("Start", new Menu.MenuCallback() {
                public void Invoke() throws ParseException {
                    editingOfCharacteristicHandler(2, menu);
                }
            });
            menu1.add("End", new Menu.MenuCallback() {
                public void Invoke() throws ParseException {
                    editingOfCharacteristicHandler(3, menu);
                }
            });
            menu1.add("Interval", new Menu.MenuCallback() {
                public void Invoke() throws ParseException {
                    editingOfCharacteristicHandler(4, menu);
                }
            });
            menu1.add("Active", new Menu.MenuCallback() {
                public void Invoke() throws ParseException {
                    editingOfCharacteristicHandler(5, menu);
                }
            });
            menu1.add("Type", new Menu.MenuCallback() {
                public void Invoke() throws ParseException {
                    editingOfCharacteristicHandler(7, menu);
                }
            });
        } else {
            menu1.add("Title", new Menu.MenuCallback() {
                public void Invoke() throws ParseException {
                    editingOfCharacteristicHandler(1, menu);
                }
            });
            menu1.add("Time", new Menu.MenuCallback() {
                public void Invoke() throws ParseException {
                    editingOfCharacteristicHandler(6, menu);
                }
            });
            menu1.add("Active", new Menu.MenuCallback() {
                public void Invoke() throws ParseException {
                    editingOfCharacteristicHandler(5, menu);
                }
            });
            menu1.add("Type", new Menu.MenuCallback() {
                public void Invoke() throws ParseException {
                    editingOfCharacteristicHandler(7, menu);
                }
            });
        }
        menu1.add("Back", new Menu.MenuCallback() {
            public void Invoke() {
                backHandler();
            }
        });
        back = true;

        while (back) {
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
        Task task = arrayTaskList.getTask(taskNumber - 1);
        boolean active = true;
        consoleView.getConsole().setCountTo0();
        switch (i) {
            case 1:
                System.out.println("Enter new title:");
                res = scanner.nextLine();
                String title = processBackspace(res);
                task.setTitle(title);
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
                    logger.error(e);
                }
                task.setStart(date);
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
                    logger.error(e);
                }
                task.setEnd(date);
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
                        logger.error(e);
                        consoleView.getConsole().setCountTo0();
                    }
                }
                task.setInterval(interval * 60000);
                break;
            case 5:
                System.out.println("Enter new active:");
                while (true) {
                    res = scanner.nextLine();
                    res1 = processBackspace(res);
                    consoleView.getConsole().setCountTo0();
                    if (res1.equals("true")) {
                        active = true;
                        break;
                    } else if (res1.equals("false")) {
                        active = false;
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
                    logger.error(e);
                }
                task.setTime(date);
                break;
            case 7:
                if (task.isRepeated()) {
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
                        logger.error(e);
                    }
                    task.setTime(date);
                    back = false;
                } else {
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
                        logger.error(e);
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
                        logger.error(e);
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
                            logger.error(nfe);
                        }
                    }
                    task.setTime(start, end, interval * 60000);
                    back = false;
                }
            default:
                break;
        }
        menu.update(arrayTaskList);
        logger.debug("Task edited: " + task.toString());
        back = false;
        back1 = false;
    }

    private static void deleteTaskHandler() throws IOException, ParseException {
        Menu menu = new Menu(ConsoleView.getConsole());
        for (Task task : arrayTaskList) {
            menu.add(task.toString(), new Menu.MenuCallback() {
                public void Invoke() {
                    deleteTaskHandler(menu);
                }
            });
        }
        menu.add("Back", new Menu.MenuCallback() {
            public void Invoke() {
                backHandler();
            }
        });
        back = true;

        while (back) {
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
        arrayTaskList.remove(arrayTaskList.getTask(taskNumber));
        menu.remove(taskNumber);
        back = false;
    }

    private static void listTaskHandler() throws IOException, ParseException {
        Menu menu = new Menu(ConsoleView.getConsole());
        menu.add("Back", new Menu.MenuCallback() {
            public void Invoke() {
                backHandler();
            }
        });
        back = true;

        while (back) {
            ConsoleView.getConsole().clear();
            if (arrayTaskList.size() != 0) {
                System.out.println("Your tasks:");
                for (Task task : arrayTaskList) {
                    System.out.println(task.toString());
                    System.out.println();
                }
            } else {
                System.out.println("No tasks in your list yet");
            }
            consoleView.getConsole().setCountTo0();
            menu.show();
        }
    }

    private static void calendarTaskHandler() throws ParseException, IOException {
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
            logger.error(e);
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
            logger.error(e);
        }
        menu.add("Back", new Menu.MenuCallback() {
            public void Invoke() {
                backHandler();
            }
        });
        back = true;
        ArrayTaskList calendar = new ArrayTaskList();
        for (Task task: arrayTaskList) {
            if(task.nextTimeAfter(start).compareTo(end) <= 0){
                calendar.add(task);
            }
        }
        while (back) {
            ConsoleView.getConsole().clear();
            if (calendar.size() != 0) {
                System.out.println("Your tasks for: " + start.toString() + " - " + end.toString() + ":");
                for (Task task: calendar) {
                        System.out.println(task.toString());
                        System.out.println();
                }
            } else {
                System.out.println("No tasks for: " + start.toString() + " - " + end.toString());
            }
            consoleView.getConsole().setCountTo0();
            menu.show();
        }
    }

    private static void dates(Set<Task> set) throws IOException, ParseException {
        back1 = true;
        Menu menu = new Menu(ConsoleView.getConsole());
        menu.add("Back", new Menu.MenuCallback() {
            public void Invoke() {
                backHandler1();
            }
        });
        while (back1) {
            consoleView.getConsole().setCountTo0();
            ConsoleView.getConsole().clear();
            for (Task task : set) {
                System.out.println(task.toString());
            }
            consoleView.getConsole().setCountTo0();
            menu.show();
        }
    }

    private static void newFileTaskHandler(Menu menu) {
        System.out.println("Enter name of new file:");
        consoleView.getConsole().setCountTo0();
        String res = scanner.nextLine();
        String name = processBackspace(res);
        file = new File("TaskLists\\" + name + ".txt");
        menu.clear();
        menu.add("Add new task", new Menu.MenuCallback() {
            public void Invoke() throws IOException, ParseException {
                addTaskHandler();
            }
        });
        menu.add("Edit task", new Menu.MenuCallback() {
            public void Invoke() throws IOException, ParseException {
                editTaskHandler();
            }
        });
        menu.add("Delete task", new Menu.MenuCallback() {
            public void Invoke() throws IOException, ParseException {
                deleteTaskHandler();
            }
        });
        menu.add("Task list", new Menu.MenuCallback() {
            public void Invoke() throws IOException, ParseException {
                listTaskHandler();
            }
        });
        menu.add("Calendar", new Menu.MenuCallback() {
            public void Invoke() throws ParseException, IOException {
                calendarTaskHandler();
            }
        });
        menu.add("Save list to file", new Menu.MenuCallback() {
            public void Invoke() throws IOException {
                saveToFileHandler();
            }
        });
        menu.add("Exit", new Menu.MenuCallback() {
            public void Invoke() {
                exitHandler();
            }
        });
    }

    private static void existingFileTaskHandler(Menu menu) throws IOException, ParseException {
        fileopen = new JFileChooser();
        fileopen.setCurrentDirectory(new File("."));
        int ret = fileopen.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            file = fileopen.getSelectedFile();

        }
        if (file != null) {
            TaskIO.readText(arrayTaskList, file);
            menu.clear();
            menu.add("Add new task", new Menu.MenuCallback() {
                public void Invoke() throws IOException, ParseException {
                    addTaskHandler();
                }
            });
            menu.add("Edit task", new Menu.MenuCallback() {
                public void Invoke() throws IOException, ParseException {
                    editTaskHandler();
                }
            });
            menu.add("Delete task", new Menu.MenuCallback() {
                public void Invoke() throws IOException, ParseException {
                    deleteTaskHandler();
                }
            });
            menu.add("Task list", new Menu.MenuCallback() {
                public void Invoke() throws IOException, ParseException {
                    listTaskHandler();
                }
            });
            menu.add("Calendar", new Menu.MenuCallback() {
                public void Invoke() throws ParseException, IOException {
                    calendarTaskHandler();
                }
            });
            menu.add("Save list to file", new Menu.MenuCallback() {
                public void Invoke() throws IOException {
                    saveToFileHandler();
                }
            });
            menu.add("Exit", new Menu.MenuCallback() {
                public void Invoke() {
                    exitHandler();
                }
            });
        }
    }

    private static void continueFileTaskHandler(Menu menu) throws IOException, ParseException {
        File f = new File("TaskLists\\lastFile.txt");
        if (f.exists() && !f.isDirectory()) {
            try {
                fileName = new Scanner(f).useDelimiter("\\Z").next();
            } catch (FileNotFoundException e) {
                logger.error("No such file " + f.getName(), e);
            }
            file = new File("TaskLists\\" + fileName);
            if (file != null) {
                TaskIO.readText(arrayTaskList, file);
                menu.clear();
                menu.add("Add new task", new Menu.MenuCallback() {
                    public void Invoke() throws IOException, ParseException {
                        addTaskHandler();
                    }
                });
                menu.add("Edit task", new Menu.MenuCallback() {
                    public void Invoke() throws IOException, ParseException {
                        editTaskHandler();
                    }
                });
                menu.add("Delete task", new Menu.MenuCallback() {
                    public void Invoke() throws IOException, ParseException {
                        deleteTaskHandler();
                    }
                });
                menu.add("Task list", new Menu.MenuCallback() {
                    public void Invoke() throws IOException, ParseException {
                        listTaskHandler();
                    }
                });
                menu.add("Calendar", new Menu.MenuCallback() {
                    public void Invoke() throws ParseException, IOException {
                        calendarTaskHandler();
                    }
                });
                menu.add("Save list to file", new Menu.MenuCallback() {
                    public void Invoke() throws IOException {
                        saveToFileHandler();
                    }
                });
                menu.add("Exit", new Menu.MenuCallback() {
                    public void Invoke() {
                        exitHandler();
                    }
                });
            }
        } else {
            menu.remove(2);
            System.out.println("There is no last file to work");
        }
    }

    private static void exit() throws IOException {
        if (arrayTaskList != null && file != null) {
            File save = new File("TaskLists\\" + MainController.getFile().getName());
            TaskIO.writeText(arrayTaskList, file);
            TaskIO.writeText(arrayTaskList, save);
            try (PrintWriter out = new PrintWriter("TaskLists\\lastFile.txt")) {
                out.println(file.getName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                logger.error("No such file " + file.getName(), e);
            }
        }
        logger.debug("Session ended");
    }

    private static void saveToFileHandler() throws IOException {
        System.out.println("Enter name of file to save your list:");
        consoleView.getConsole().setCountTo0();
        String res = scanner.nextLine();
        String name = processBackspace(res);
        File save = new File("TaskLists\\" + name + ".txt");
        if (arrayTaskList != null && file != null) {
            TaskIO.writeText(arrayTaskList, save);
        }
        logger.debug("Saved to file \"" + save + "\"");
    }

    /**
     * for removing backspaces from string.
     *
     * @param input string from which you want to remove backspaces
     * @return input string without backspaces
     */
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
        arrayTaskList = new ArrayTaskList();
        logger.debug("Session started");
        File dir = new File("TaskLists");
        if (!dir.exists()) {
            new File("TaskLists").mkdir();
        }
        consoleView.getMenu().add("New file", new Menu.MenuCallback() {
            public void Invoke() {
                newFileTaskHandler(consoleView.getMenu());
            }
        });
        consoleView.getMenu().add("Existing file", new Menu.MenuCallback() {
            public void Invoke() throws IOException, ParseException {
                existingFileTaskHandler(consoleView.getMenu());
            }
        });
        consoleView.getMenu().add("Continue last file", new Menu.MenuCallback() {
            public void Invoke() throws IOException, ParseException {
                continueFileTaskHandler(consoleView.getMenu());
            }
        });
        consoleView.getMenu().add("Exit", new Menu.MenuCallback() {
            public void Invoke() {
                exitHandler();
            }
        });
        while (MainController.isExit()) {
            consoleView.getConsole().clear();
            System.out.println("Please choose an option:");
            consoleView.getMenu().show();
        }
        MainController.exit();
        System.exit(0);
    }
}
