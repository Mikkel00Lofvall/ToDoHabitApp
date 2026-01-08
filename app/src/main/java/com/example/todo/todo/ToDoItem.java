package com.example.todo.todo;

import java.io.Serializable;

public class ToDoItem implements Serializable {
    private String title;
    private String creationTime;
    private boolean done;
    private String description;
    private int reward = 0;


    public ToDoItem(String title, int reward, String creationTime, boolean done, String description) {
        this.title = title;
        this.creationTime = creationTime;
        this.done = done;
        this.description = description;
        this.reward = reward;
    }

    public String getTitle() { return this.title; }
    public String getCreationTime() { return this.creationTime; }
    public boolean isDone() { return this.done; }

    public void setDone(boolean done) { this.done = done; }
    public String getDescription() { return this.description; }
    public int getReward() { return this.reward; }
}
