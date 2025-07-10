import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subTaskIDs;

    public Epic(String name, String description) {
        super(name, description);
        subTaskIDs = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskID=" + subTaskIDs +
                "} " + super.toString();
    }

    public ArrayList<Integer> getSubTaskID() {
        return subTaskIDs;
    }

    public void setSubTaskID(ArrayList<Integer> subTaskID) {
        this.subTaskIDs = subTaskID;
    }

    public void addSubTaskID(int id){
        subTaskIDs.add(id);
    }

    public void removeSubTaskId(int id) {
        subTaskIDs.remove(id);
    }
}
