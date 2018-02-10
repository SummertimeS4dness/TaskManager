package com.company.View;

import com.company.Controller.MainController;
import com.company.View.javaConsole.JavaConsole;
import com.company.View.menu.Menu;
import com.company.View.menu.MenuCallback;

public class ConsoleView {

    private static JavaConsole console;
    private static Menu menu;

    public ConsoleView() {
        console = new JavaConsole();
        console.setTitle("Task Manager");
        menu = new Menu(console);
    }

    public static JavaConsole getConsole() {
        return console;
    }

    public static Menu getMenu() {
        return menu;
    }
}
