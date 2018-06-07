package backend_core;

/**
 *
 * @author Michal Kalinec 444505
 */
public class Note {
    private int category;
    private String text;

    public Note(int category, String text){
        this.category = category;
        this.text = text;
    }
    
    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
