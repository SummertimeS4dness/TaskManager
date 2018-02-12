package com.company.view.menu;

import com.company.controller.MainController;
import com.company.model.ArrayTaskList;
import com.company.model.TaskIO;
import com.company.view.javaconsole.JavaConsole;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.log4j.Logger;

/**
 * A menu system for organizing code in the JavaConsole.
 *
 * @author Mishchenko Anton
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

    /**
     * getter of choosen.
     *
     * @return choosen
     */
    public int getChoosen() {
        return choosen;
    }

    /**
     * Class Constructor.
     *
     * @param c an instance of the JavaConsole UI
     */
    public Menu(JavaConsole c) {
        console = c;
    }

    /**
     * A menu item object.
     *
     * @author David MacDermot
     * @date 02-07-2012
     * @bug
     */
    private class MenuItem {

        private MenuCallback mc;
        private String text;

        /**
         * Class Constructor.
         *
         * @param text The text to display
         * @param mc   an MenuCallback object
         */
        public MenuItem(String text, MenuCallback mc) {
            mc = mc;
            text = text;
        }

        /**
         * @return the MenuCallback object
         */
        public MenuCallback get_mc() {
            return mc;
        }

        /**
         * @return the display text
         */
        public String get_text() {
            return text;
        }

        public void set_text(String text) {
            text = text;
        }

    }

    private ArrayList<MenuItem> items =
            new ArrayList<MenuItem>();

    /**
     * @param text The text to display
     * @param mc   an MenuCallback object
     * @return boolean true if successful.
     */
    public boolean add(String text, MenuCallback mc) {
        return items.add(new MenuItem(text, mc));
    }

    /**
     * for removing MenuItem by index.
     *
     * @param i index for removing
     */
    public void remove(int i) {
        items.remove(i);
    }

    /**
     * for updating menu with ArrayTaskList.
     *
     * @param arrayTaskList with tasks for updating
     */
    public void update(ArrayTaskList arrayTaskList) {
        for (int i = 0; i < arrayTaskList.size(); i++) {
            items.get(i).set_text(arrayTaskList.getTask(i).toString());
        }
    }

    /**
     * for clearing menu.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Display the list of menu item choices.
     */
    public void show() throws IOException, ParseException {
        choosen = 0;
        String a, b;
        Scanner in = new Scanner(System.in);

        for (int i = 0; i < items.size(); ++i) {
            MenuItem mi = items.get(i);
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

        if (choosen > items.size() || choosen < 1) {
            System.out.println("Invalid option.\nPress enter to continue...");
            in.nextLine();
            in.nextLine();
        } else {
            MenuItem mi = items.get(choosen - 1);
            MenuCallback mc = mi.get_mc();
            mc.Invoke();
        }
    }
}
