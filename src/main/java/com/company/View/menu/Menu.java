package com.company.View.menu;

import com.company.Controller.MainController;
import com.company.Model.ArrayTaskList;
import com.company.Model.Task;
import com.company.Model.TaskIO;
import com.company.View.javaConsole.JavaConsole;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.log4j.Logger;

/**
 * @author Mishchenko Anton
 * @brief A menu system for organizing code in the JavaConsole.
 * @history added method for removing menu item
 * added method for updating menu
 * added method for clearing menu
 * fixed backspacing in choosing menu item
 * added exit from the program by typing "exit"
 * @bug
 */
public class Menu {
    final static Logger logger = Logger.getLogger(Menu.class);
    private JavaConsole console;
    private int choosen;

    public int getChoosen() {
        return choosen;
    }

    /**
     * @param c an instance of the JavaConsole UI
     * @brief Class Constructor
     */
    public Menu(JavaConsole c) {
        console = c;
    }

    /**
     * @author David MacDermot
     * @brief A menu item object.
     * @date 02-07-2012
     * @bug
     */
    private class MenuItem {

        private MenuCallback _mc;
        private String _text;

        /**
         * @param text The text to display
         * @param mc   an MenuCallback object
         * @brief Class Constructor
         */
        public MenuItem(String text, MenuCallback mc) {
            _mc = mc;
            _text = text;
        }

        /**
         * @return the MenuCallback object
         */
        public MenuCallback get_mc() {
            return _mc;
        }

        /**
         * @return the display text
         */
        public String get_text() {
            return _text;
        }

        public void set_text(String text) {
            _text = text;
        }

    }

    private ArrayList<MenuItem> Items =
            new ArrayList<MenuItem>();

    /**
     * @param text The text to display
     * @param mc   an MenuCallback object
     * @return boolean true if successful.
     */
    public boolean add(String text, MenuCallback mc) {
        return Items.add(new MenuItem(text, mc));
    }

    public void remove(int i) {
        Items.remove(i);
    }

    public void update(ArrayTaskList arrayTaskList) {
        for (int i = 0; i < arrayTaskList.size(); i++) {
            Items.get(i).set_text(arrayTaskList.getTask(i).toString());
        }
    }

    public void clear() {
        Items.clear();
    }

    /**
     * @brief Display the list of menu item choices
     */
    public void show() throws IOException, ParseException {
        choosen = 0;
        String a, b;
        Scanner in = new Scanner(System.in);

        for (int i = 0; i < Items.size(); ++i) {
            MenuItem mi = Items.get(i);
            System.out.printf(" [%d] %s \n", i + 1, mi.get_text());
        }

        System.out.println(); // add a line

        try {
            a = in.nextLine();
            b = MainController.processBackspace(a);
            if (b.equals("exit")) {
                if (MainController.getArrayTaskList() != null && MainController.getFile() != null) {
                    TaskIO.writeText(MainController.getArrayTaskList(), MainController.getFile());
                    try (PrintWriter out = new PrintWriter("TaskLists\\lastFile.txt")) {
                        out.println(MainController.getFile().getName());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        logger.error(e);
                    }
                }
                logger.debug("Session ended");
                System.exit(0);
            }
            choosen = Integer.parseInt(b);
        } catch (Exception e1) {
        }

        console.clear();

        if (choosen > Items.size() || choosen < 1) {
            System.out.println("Invalid option.\nPress enter to continue...");
            in.nextLine();
            in.nextLine();
        } else {
            MenuItem mi = Items.get(choosen - 1);
            MenuCallback mc = mi.get_mc();
            mc.Invoke();
        }
    }
}
