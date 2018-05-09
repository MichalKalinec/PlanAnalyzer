package core;

/**
 *
 * @author Michal Kalinec 444505
 */
public class NoteCategoryClass {

    private int category;
    private String description;

    public NoteCategoryClass(int category, String description) {
        this.category = category;
        this.description = description;
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

    public static String getShortDesc(int category) {
        switch (category) {
            case 1:
                return "RučUkonč";
            case 2:
                return "ChMat";
            case 3:
                return "ChNár";
            case 4:
                return "ChPra";
            case 5:
                return "ChOpTP";
            case 6:
                return "OdČasTP";
            case 7:
                return "MešPredOp";
            case 8:
                return "ZmPorZák";
            case 9:
                return "NáhNepVýr";
            case 10:
                return "ChPap";
            case 11:
                return "ChOrgPra";
            case 12:
                return "Iné";
            default:
                return null;
        }
    }

    public static String getDescWithCat(int category) {
        switch (category) {
            case 1:
                return "ručné ukončenie operácie";
            case 2:
                return "chýbajúci materiál";
            case 3:
                return "chýbajúce náradie";
            case 4:
                return "chýbajúci pracovník";
            case 5:
                return "chýbajúca operácia v TP";
            case 6:
                return "odchýlka od času v TP";
            case 7:
                return "meškanie predchádzajúcich operácií";
            case 8:
                return "zmena poradia zákaziek";
            case 9:
                return "náhrada nepodarkovej výroby";
            case 10:
                return "chyba v papieroch";
            case 11:
                return "chyba v organizácií práce";
            case 12:
                return "iné";
            default:
                return null;
        }
    }
}
