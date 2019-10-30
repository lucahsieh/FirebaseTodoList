package io.github.lucahsieh.firebaserocks;

import java.util.Date;

public class ToDoItem {
    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public Date getDue() {
        return due;
    }

    public void setDue(Date due) {
        this.due = due;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;
    String task;
    String who;
    Date due;
    boolean done;

    public ToDoItem(){}

    public ToDoItem(String id,
                    String task,
                    String who,
                    Date due,
                    boolean done){
        this.id=id;
        this.task=task;
        this.who=who;
        this.due=due;
        this.done=done;
    }
}
