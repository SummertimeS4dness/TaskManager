package com.company.model;

import java.util.Arrays;
import java.util.Iterator;

/**
 * class ArrayTaskList that contains tasks.
 *
 * @author MishchenkoAnton
 * @version MavenAndLog4j
 */

public class ArrayTaskList extends TaskList implements Iterable<Task>, Cloneable {
    private Task[] tasks = new Task[0];

    /**
     * for adding task to list.
     *
     * @param task to add
     * @throws NullPointerException
     */
    public void add(Task task) throws NullPointerException {
        if (task == null) {
            throw new NullPointerException("Task to add is null");
        }
        expand();
        tasks[tasks.length - 1] = task;
    }

    /**
     * for getting number of tasks.
     *
     * @return number of tasks
     */
    public int size() {
        return tasks.length;
    }

    /**
     * for removing task.
     *
     * @param task to remove
     * @return true if remove is successful, false - if there is no such task in list
     * @throws NullPointerException
     */
    public boolean remove(Task task) throws NullPointerException {
        if (task == null) {
            throw new NullPointerException("Task to remove is null");
        }
        for (int i = 0; i < tasks.length; i++) {
            if (tasks[i].equals(task)) {
                remove(i);
                return true;
            }
        }

        return false;
    }

    private void remove(int i) throws IndexOutOfBoundsException {
        Task[] copy = new Task[tasks.length - 1];
        System.arraycopy(tasks, 0, copy, 0, i);
        System.arraycopy(tasks, i + 1, copy, i, tasks.length - i - 1);
        tasks = copy;
    }

    /**
     * for getting task by index.
     *
     * @param index of task
     * @return task with needed index
     * @throws IndexOutOfBoundsException
     */
    public Task getTask(int index) throws IndexOutOfBoundsException {
        return tasks[index];
    }

    private void expand() {
        Task[] newArray = new Task[tasks.length + 1];
        System.arraycopy(tasks, 0, newArray, 0, tasks.length);
        tasks = newArray;
    }

    /**
     * for comparing ArrayTaskLists.
     *
     * @param o object to compare with
     * @return true if this ArrayTaskList and o are equal, false - if unequal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ArrayTaskList tasks1 = (ArrayTaskList) o;

        return Arrays.equals(tasks, tasks1.tasks);
    }

    /**
     * for getting hashcode of ArrayTaskList.
     *
     * @return hashCode of this ArrayTaskList
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(tasks);
    }

    /**
     * for describing ArrayTaskList in String.
     *
     * @return String that describes ArrayTaskList
     */
    @Override
    public String toString() {
        return "ArrayTaskList{" + "tasks=" + Arrays.toString(tasks) + '}';
    }

    /**
     * for cloning ArrayTaskList.
     *
     * @return cloned ArrayTaskList
     */
    public ArrayTaskList clone() {
        ArrayTaskList result = null;
        try {
            result = (ArrayTaskList) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * for getting ArrayTaskListIterator.
     *
     * @return ArrayTaskListIterator
     */
    @Override
    public Iterator<Task> iterator() {
        return new ArrayTaskListIterator();
    }

    private class ArrayTaskListIterator implements Iterator<Task> {
        int count = 0;
        boolean check = false;

        @Override
        public boolean hasNext() {
            if (count == tasks.length) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public Task next() {
            if (hasNext()) {
                Task res = ArrayTaskList.this.getTask(count);
                count++;
                check = true;
                return res;
            } else {
                return null;
            }
        }

        @Override
        public void remove() {
            if (check) {
                ArrayTaskList.this.remove(count - 1);
                count--;
                check = false;
            } else {
                throw new IllegalStateException();
            }
        }
    }
}