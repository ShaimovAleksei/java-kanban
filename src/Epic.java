import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subTaskID;

    public Epic(String name, String description) {
        super(name, description);
        subTaskID = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskID=" + subTaskID +
                "} " + super.toString();
    }

    public ArrayList<Integer> getSubTaskID() {
        return subTaskID;
    }

    public void setSubTaskID(ArrayList<Integer> subTaskID) {
        this.subTaskID = subTaskID;
    }

    public void addSubTaskID(int id){
        subTaskID.add(id);
    }

    public void removeSubTaskId(int id) {
        subTaskID.remove(id);
    }
}
