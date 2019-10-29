package secondary;

/**
 *
 * @author Michal Kalinec 444505
 */
public class NoteCategoryClass {

    private static final int CATEGORIES_COUNT = 12;
    private int category;
    private String description;

    public NoteCategoryClass(int category, String description) {
        this.category = category;
        this.description = description;
    }

    public static int getCategoriesCount() {
        return CATEGORIES_COUNT;
    }

    @Override
    public String toString() {
        return description;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //change also sumNotesForWorkcens in case of adding/removing a category
    public static String getDescWithCat(int category) {
        switch (category) {
            case 1:
                return "ručné ukončenie operácie";
            case 2:
                return "TPV";
            case 3:
                return "galvanizovňa";
            case 4:
                return "výroba";
            case 5:
                return "MTZ";
            case 6:
                return "chýbajúci pracovník";
            case 7:
                return "odbyt";
            case 8:
                return "ateliér";
            case 9:
                return "Razidlo";
            case 10:
                return "Materiál";
            case 11:
                return "Rez";
            case 12:
                return "iné";
            default:
                return null;
        }
    }
}
