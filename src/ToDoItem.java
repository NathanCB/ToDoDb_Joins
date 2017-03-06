
public class ToDoItem {
    int id;
    String text;
    boolean isDone;
    int user_id;

    public ToDoItem(int id, String text, boolean isDone, int user_id){
        this.id = id;
        this.text = text;
        this.isDone = isDone;
        this.user_id = user_id;
    }
}
