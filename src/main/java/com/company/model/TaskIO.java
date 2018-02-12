package com.company.model;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 * class TaskIO that contains methods for writing and reading Tasks.
 *
 * @author MishchenkoAnton
 * @version MavenAndLog4j
 */

public class TaskIO {
    final static Logger logger = Logger.getLogger(TaskIO.class);
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static void write(TaskList tasks, OutputStream out) throws IOException {
        DataOutputStream output = new DataOutputStream(out);
        try {
            output.writeInt(tasks.size());
            for (Task task : tasks) {
                output.writeInt(task.getTitle().length());
                output.writeUTF(task.getTitle());
                if (task.isActive()) {
                    output.writeInt(1);
                } else {
                    output.writeInt(0);
                }
                output.writeLong(task.getRepeatInterval());
                if (task.isRepeated()) {
                    output.writeLong(task.getStartTime().getTime());
                    output.writeLong(task.getEndTime().getTime());
                } else {
                    output.writeLong(task.getTime().getTime());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
    }

    private static void read(TaskList tasks, InputStream in) throws IOException {
        DataInputStream input = new DataInputStream(in);
        try {
            int size = input.readInt();
            for (int i = 0; i < size; i++) {
                input.readInt();
                String title = input.readUTF();
                int activeI = input.readInt();
                boolean active;
                if (activeI == 1) {
                    active = true;
                } else {
                    active = false;
                }
                int interval = input.readInt();
                if (interval != 0) {
                    Date start = new Date(input.readLong());
                    Date end = new Date(input.readLong());
                    Task task = null;
                    task = new Task(title, start, end, interval);
                    task.setActive(active);
                    if (task != null) {
                        tasks.add(task);
                    }
                } else {
                    Date time = new Date(input.readLong());
                    Task task = null;
                    task = new Task(title, time);
                    task.setActive(active);
                    if (task != null) {
                        tasks.add(task);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
    }

    /**
     * for binary writing from TaskList to file.
     *
     * @param tasks TaskList to write from
     * @param file  to write to
     * @throws IOException
     */
    public static void writeBinary(TaskList tasks, File file) throws IOException {
        try {
            write(tasks, new FileOutputStream(file.getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("No such file " + file.getPath(), e);
        }
    }

    /**
     * for binary reading from file to TaskList.
     *
     * @param tasks TaskList to read to
     * @param file  to read from
     * @throws IOException
     */
    public static void readBinary(TaskList tasks, File file) throws IOException {
        try {
            read(tasks, new FileInputStream(file.getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("No such file " + file.getPath(), e);
        }
    }

    private static void write(TaskList tasks, Writer out) throws IOException {
        BufferedWriter output = new BufferedWriter(out);
        String taskStr = "";
        int size = tasks.size();
        try {
            for (Task task : tasks) {
                taskStr = "\"" + task.getTitle() + "\"";
                if (task.isRepeated()) {
                    taskStr += " from [" + dateFormat.format(task.getStartTime()) + "] to [" +
                            dateFormat.format(task.getEndTime()) + "] " + calculateTime(task.getRepeatInterval());
                } else {
                    taskStr += " at [" + dateFormat.format(task.getTime()) + "]";
                }
                if (!task.isActive()) {
                    taskStr += " inactive";
                }
                if (size > 1) {
                    taskStr += ";\n";
                } else {
                    taskStr += ".\n";
                }
                size--;
                output.write(taskStr);
                taskStr = "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
    }

    private static void read(TaskList tasks, Reader in) throws ParseException, IOException {
        BufferedReader input = new BufferedReader(in);
        String task;
        String title;
        Date time = null;
        Date start = null;
        Date end = null;
        boolean repeated = false;
        boolean active = false;
        int interval = 0;
        try {
            while (input.ready()) {
                task = input.readLine();
                title = task.substring(1, task.lastIndexOf("\""));
                if (task.indexOf(" at ") != -1) {
                    repeated = false;
                    int start1 = task.indexOf("[", task.indexOf(" at "));
                    int end1 = task.indexOf("]", start1);
                    String date = task.substring(start1 + 1, end1);
                    try {
                        time = dateFormat.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        logger.error(date, e);
                    }
                } else if (task.indexOf(" from ") != -1) {
                    repeated = true;
                    int start1 = task.indexOf("[", task.indexOf(" from "));
                    int end1 = task.indexOf("]", start1);
                    String date = task.substring(start1 + 1, end1);
                    try {
                        start = dateFormat.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        logger.error(date, e);
                    }
                    start1 = task.indexOf("[", task.indexOf(" to "));
                    end1 = task.indexOf("]", start1);
                    date = task.substring(start1 + 1, end1);
                    try {
                        end = dateFormat.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        logger.error(date, e);
                    }
                    start1 = task.indexOf("[", task.indexOf(" every "));
                    end1 = task.indexOf("]", start1);
                    date = task.substring(start1 + 1, end1);
                    String[] every = date.split(" ");
                    for (int i = 0; i < every.length; i += 2) {
                        int res = Integer.parseInt(every[i]);
                        interval = 0;
                        if (every[i + 1].equals("day") || every[i + 1].equals("days")) {
                            interval += res * 86400000;
                        } else if (every[i + 1].equals("hour") || every[i + 1].equals("hours")) {
                            interval += res * 3600000;
                        } else if (every[i + 1].equals("minute") || every[i + 1].equals("minutes")) {
                            interval += res * 60000;
                        } else if (every[i + 1].equals("second") || every[i + 1].equals("seconds")) {
                            interval += res * 1000;
                        }
                    }
                }
                if (task.indexOf("inactive") == -1) {
                    active = true;
                } else {
                    active = false;
                }
                if (repeated) {
                    Task taskToAdd = null;
                    taskToAdd = new Task(title, start, end, interval);
                    taskToAdd.setActive(active);
                    if (taskToAdd != null) {
                        tasks.add(taskToAdd);
                    }
                } else {
                    Task taskToAdd = null;
                    taskToAdd = new Task(title, time);
                    taskToAdd.setActive(active);
                    if (taskToAdd != null) {
                        tasks.add(taskToAdd);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
    }

    /**
     * for text writing from TaskList to file.
     *
     * @param tasks TaskList to write from
     * @param file  to write to
     * @throws IOException
     */
    public static void writeText(TaskList tasks, File file) throws IOException {
        try {
            write(tasks, new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("No such file " + file.getPath(), e);
        }
    }

    /**
     * for text reading from file to TaskList.
     *
     * @param tasks TaskList to read to
     * @param file  to read from
     * @throws IOException
     * @throws ParseException
     */
    public static void readText(TaskList tasks, File file) throws ParseException, IOException {
        try {
            read(tasks, new FileReader(file.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("No such file " + file.getPath(), e);
        }
    }

    /**
     * for converting time from milliseconds to string.
     *
     * @param time1 milliseconds to convert
     * @return converted string
     */
    public static String calculateTime(long time1) {
        long time = time1 / 1000;
        int days = (int) TimeUnit.SECONDS.toDays(time);
        long hours = TimeUnit.SECONDS.toHours(time) - TimeUnit.DAYS.toHours(days);
        long minutes = TimeUnit.SECONDS.toMinutes(time) - TimeUnit.DAYS.toMinutes(days) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.SECONDS.toSeconds(time) - TimeUnit.DAYS.toSeconds(days) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);
        String interval = "every [";
        if (days == 1) {
            interval += days + " day ";
        } else if (days > 1) {
            interval += days + " days ";
        }
        if (hours == 1) {
            interval += hours + " hour ";
        } else if (hours > 1) {
            interval += hours + " hours ";
        }
        if (minutes == 1) {
            interval += minutes + " minute ";
        } else if (minutes > 1) {
            interval += minutes + " minutes ";
        }
        if (seconds == 1) {
            interval += seconds + " second ";
        } else if (seconds > 1) {
            interval += seconds + " seconds ";
        }
        interval += "]";

        return interval;
    }
}
