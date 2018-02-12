package com.company.View;

import com.company.Controller.MainController;
import com.company.View.javaConsole.JavaConsole;
import com.company.View.menu.Menu;

/**
 * Console view of task manager
 *
 * @author MishchenkoAnton
 *
 * @version MavenAndLog4j
 */

public class ConsoleView {

    private static JavaConsole console;
    private static Menu menu;

    /**
     * constructor with initialisations of fields
     */
    public ConsoleView() {
        console = new JavaConsole();
        console.setTitle("Task Manager");
        menu = new Menu(console);
    }

    /**
     * getter of console
     * @return console
     */
    public static JavaConsole getConsole() {
        return console;
    }

    /**
     * getter of menu
     * @return menu
     */
    public static Menu getMenu() {
        return menu;
    }
}
