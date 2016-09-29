/**
 * Created by joe on 28/09/2016.
 */
public class ToDoItem {
    int id;
    int user_id;
    public String text;
    public boolean isDone;

    public ToDoItem(int user_id, String text, boolean isDone) {
        this.user_id=user_id;
        this.text = text;
        this.isDone = isDone;

    }

    public ToDoItem(int id, int user_id, String text, boolean isDone) {
        this.id = id;
        this.user_id=user_id;
        this.text = text;
        this.isDone = isDone;
    }
}
