package com.yandex.kanban.model;

public class SubTask extends Task {
    private int epicID;

    public  SubTask(String name, String description, int epicID){
        super(name, description);
        this.epicID = epicID;

    }


    @Override
    public String toString() {
        return "SubTask{" +
                "epicID=" + epicID +
                "} " + super.toString();
    }

    public int getEpicID() {
        return epicID;
    }

    public boolean setEpicID(int epicID) {
        if (epicID == this.getId()) {
            return false;
        }
        this.epicID = epicID;
        return true;
    }
}
