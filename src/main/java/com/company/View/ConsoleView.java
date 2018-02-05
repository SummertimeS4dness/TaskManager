package com.company.View;

import com.company.Controller.MainController;
import com.company.View.javaConsole.JavaConsole;
import com.company.View.menu.Menu;
import com.company.View.menu.MenuCallback;

import java.awt.*;

public class ConsoleView {

    private static JavaConsole console;
    private static Menu menu;
    private static Menu addMenu;

    public ConsoleView() {
        console = new JavaConsole();
        console.setTitle("Task Manager");
        menu = new Menu(console);
        menu.add("New file", new MenuCallback() { public void Invoke() { MainController.newFileTaskHandler(menu); } });
        menu.add("Existing file", new MenuCallback() { public void Invoke() { MainController.existingFileTaskHandler(menu); } });
        menu.add("Exit", new MenuCallback() { public void Invoke() { MainController.exitHandler(); } });
    }

    public void run() {
        while(MainController.isExit()) {
            console.clear();
            System.out.println("Please choose an option:");
            menu.show();
        }
        MainController.exit();
        System.exit(0);
    }

    public static Menu getAddMenu() {
        return addMenu;
    }

    public static JavaConsole getConsole() {
        return console;
    }
}
