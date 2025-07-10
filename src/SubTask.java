public class SubTask extends Task{
    private int epicID;

    public  SubTask(String name, String description){
        super(name, description);

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

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }
}
