package com.company.view.javaconsole;

import com.company.controller.MainController;
import com.company.model.TaskIO;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import org.apache.log4j.Logger;

/**
 * @author Mishchenko Anton
 * A simple Java Console for your application (Swing version).
 * @par Comments:
 * Original located at http://www.comweb.nl/java/Console/Console.html
 * @history added label for notifications
 * fixed erasing of the menu
 * added saving to file in windowClosed(WindowEvent evt)
 * @bug
 */

public class JavaConsole extends WindowAdapter implements WindowListener, ActionListener, Runnable {
    private final static Logger logger = Logger.getLogger(JavaConsole.class);
    private JFrame frame;
    public JTextArea textArea;
    private Thread reader;
    private Thread reader2;
    private boolean quit;
    private int count = 0;

    private final PipedInputStream pin = new PipedInputStream();
    private final PipedInputStream pin2 = new PipedInputStream();
    private final PipedOutputStream pout3 = new PipedOutputStream(); //DWM 02-07-2012

    private JLabel label;

    /**
     * Class Constructor.
     */
    public JavaConsole() {
        // create all components and add them
        frame = new JFrame("Java Console");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = new Dimension((int) (screenSize.width / 2), (int) (screenSize.height / 2));
        int x = (int) (frameSize.width / 2);
        int y = (int) (frameSize.height / 2);
        frame.setBounds(x, y, frameSize.width, frameSize.height);
        textArea = new JTextArea();
        textArea.setBackground(Color.black); //DWM 02-07-2012
        textArea.setForeground(Color.white); //DWM 02-07-2012
        textArea.setCaretColor(textArea.getForeground()); //DWM 02-07-2012
        textArea.setFont(new Font("Lucida Sans", Font.BOLD, 14)); //DWM 02-07-2012
        textArea.setLineWrap(true); //DWM 02-07-2012
        textArea.setWrapStyleWord(true); //DWM 02-07-2012
        textArea.setEditable(true); //DWM 02-07-2012
        label = new JLabel("");

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.getContentPane().add(label, BorderLayout.SOUTH);
        frame.setVisible(true);

        frame.addWindowListener(this);

        try {
            PipedOutputStream pout = new PipedOutputStream(this.pin);
            System.setOut(new PrintStream(pout, true));
        } catch (IOException io) {
            textArea.append("Couldn't redirect STDOUT to this console\n" + io.getMessage());
            textArea.setCaretPosition(textArea.getDocument().getLength()); //DWM 02-07-2012
            logger.error("Couldn't redirect STDOUT to this console\n" + io.getMessage());
        } catch (SecurityException se) {
            textArea.append("Couldn't redirect STDOUT to this console\n" + se.getMessage());
            textArea.setCaretPosition(textArea.getDocument().getLength()); //DWM 02-07-2012
            logger.error("Couldn't redirect STDOUT to this console\n" + se.getMessage());
        }

        try {
            PipedOutputStream pout2 = new PipedOutputStream(this.pin2);
            System.setErr(new PrintStream(pout2, true));
        } catch (IOException io) {
            textArea.append("Couldn't redirect STDERR to this console\n" + io.getMessage());
            textArea.setCaretPosition(textArea.getDocument().getLength()); //DWM 02-07-2012
            logger.error("Couldn't redirect STDERR to this console\n" + io.getMessage());
        } catch (SecurityException se) {
            textArea.append("Couldn't redirect STDERR to this console\n" + se.getMessage());
            textArea.setCaretPosition(textArea.getDocument().getLength()); //DWM 02-07-2012
            logger.error("Couldn't redirect STDERR to this console\n" + se.getMessage());
        }

        try {
            System.setIn(new PipedInputStream(this.pout3));
        } catch (IOException io) {
            textArea.append("Couldn't redirect STDIN to this console\n" + io.getMessage());
            textArea.setCaretPosition(textArea.getDocument().getLength()); //DWM 02-07-2012
            logger.error(io);
        } catch (SecurityException se) {
            textArea.append("Couldn't redirect STDIN to this console\n" + se.getMessage());
            textArea.setCaretPosition(textArea.getDocument().getLength()); //DWM 02-07-2012
            logger.error(se);
        }

        textArea.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == '\b') {
                    if (count > 0) {
                        count--;
                    } else {
                        textArea.getInputMap().put(KeyStroke.getKeyStroke("BACK_SPACE"), "none");
                    }
                } else {
                    count++;
                }
            }

            public void keyReleased(KeyEvent e) {
                textArea.getInputMap().remove(KeyStroke.getKeyStroke("BACK_SPACE"));
            }

            public void keyTyped(KeyEvent e) {
                try {
                    pout3.write(e.getKeyChar());
                } catch (IOException ex) {
                    logger.error(ex);
                }
            }
        }); //DWM 02-07-2012

        quit = false; // signals the Threads that they should exit

        // Starting two seperate threads to read from the PipedInputStreams
        reader = new Thread(this);
        reader.setDaemon(true);
        reader.start();

        reader2 = new Thread(this);
        reader2.setDaemon(true);
        reader2.start();

    }

    /**
     * for setting text to label.
     *
     * @param text to set
     */
    public void setLabel(String text) {
        label.setText(text);
    }

    /**
     * for setting count to zero.
     */
    public void setCountTo0() {
        count = 0;
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
     */
    public synchronized void windowClosed(WindowEvent evt) {
        quit = true;
        if (MainController.getArrayTaskList() != null && MainController.getFile() != null) {
            File save = new File("TaskLists\\" + MainController.getFile().getName());
            try {
                TaskIO.writeText(MainController.getArrayTaskList(), MainController.getFile());
                TaskIO.writeText(MainController.getArrayTaskList(), save);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("No such file " + MainController.getFile().getName() + e);
            }
            try (PrintWriter out = new PrintWriter("TaskLists\\lastFile.txt")) {
                out.println(MainController.getFile().getName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
        logger.debug("Session ended");
        this.notifyAll(); // stop all threads
        try {
            reader.join(1000);
            pin.close();
        } catch (Exception e) {
            logger.error(e);
        }
        try {
            reader2.join(1000);
            pin2.close();
        } catch (Exception e) {
            logger.error(e);
        }
        try {
            pout3.close();
        } catch (Exception e) {
            logger.error(e);
        } //DWM 02-07-2012
        System.exit(0);
    }

    /* (non-Javadoc)
     * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
     */
    public synchronized void windowClosing(WindowEvent evt) {
        frame.setVisible(false); // default behaviour of JFrame
        frame.dispose();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public synchronized void actionPerformed(ActionEvent evt) {
        this.clear();
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public synchronized void run() {
        try {
            while (Thread.currentThread() == reader) {
                try {
                    this.wait(100);
                } catch (InterruptedException ie) {
                    logger.error(ie);
                }
                if (pin.available() != 0) {
                    String input = this.readLine(pin);
                    textArea.append(input);
                    textArea.setCaretPosition(textArea.getDocument().getLength()); //DWM 02-07-2012
                }
                if (quit) return;
            }

            while (Thread.currentThread() == reader2) {
                try {
                    this.wait(100);
                } catch (InterruptedException ie) {
                    logger.error(ie);
                }
                if (pin2.available() != 0) {
                    String input = this.readLine(pin2);
                    textArea.append(input);
                    textArea.setCaretPosition(textArea.getDocument().getLength()); //DWM 02-07-2012
                }
                if (quit) {
                    return;
                }
            }
        } catch (Exception e) {
            textArea.append("\nConsole reports an Internal error.");
            textArea.append("The error is: " + e);
            textArea.setCaretPosition(textArea.getDocument().getLength()); //DWM 02-07-2012
            logger.error("Console reports an Internal error " + e);
        }

    }

    /**
     * Read a line of text from the input stream.
     *
     * @param in The PipedInputStream to read
     * @return String A line of text
     * @throws IOException
     */
    private synchronized String readLine(PipedInputStream in) throws IOException {
        String input = "";
        do {
            int available = in.available();
            if (available == 0) {
                break;
            }
            byte[] b = new byte[available];
            in.read(b);
            input = input + new String(b, 0, b.length);
        } while (!input.endsWith("\n") && !input.endsWith("\r\n") && !quit);
        return input;
    }

    /**
     * Clear the console window
     */
    public void clear() //DWM 02-07-2012
    {
        textArea.setText("Welcome to TASK MANAGER.\nTo close the program, type \"exit\" or use option in main menu.\n\n");
    }

    /**
     * @return the consol's background color
     */
    public Color getBackground() { //DWM 02-07-2012
        return textArea.getBackground();
    }

    /**
     * @param bg the desired background Color
     */
    public void setBackground(Color bg) { //DWM 02-07-2012
        this.textArea.setBackground(bg);
    }

    /**
     * @return the consol's foreground color
     */
    public Color getForeground() { //DWM 02-07-2012
        return textArea.getForeground();
    }

    /**
     * @param fg the desired foreground Color
     */
    public void setForeground(Color fg) { //DWM 02-07-2012
        this.textArea.setForeground(fg);
        this.textArea.setCaretColor(fg);
    }

    /**
     * @return the consol's font
     */
    public Font getFont() { //DWM 02-07-2012
        return textArea.getFont();
    }

    /**
     * @param f the font to use as the current font
     */
    public void setFont(Font f) { //DWM 02-07-2012
        textArea.setFont(f);
    }

    /**
     * @param i the icon image to display in console window's corner
     */
    public void setIconImage(Image i) { //DWM 02-07-2012
        frame.setIconImage(i);
    }

    /**
     * @param title the console window's title
     */
    public void setTitle(String title) { //DWM 02-07-2012
        frame.setTitle(title);
    }
}