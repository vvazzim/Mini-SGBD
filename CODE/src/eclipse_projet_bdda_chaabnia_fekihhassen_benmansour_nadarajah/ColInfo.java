package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

/**
 * Cette classe représente les informations sur une colonne dans une relation.
 */
public class ColInfo {
    private String name; // nom de la colonne
    private String type; // le type de données de la colonne

    /**
     * Constructeur de ColInfo
     * 
     * @param name Le nom de la colonne.
     * @param type Le type de données de la colonne.
     * @throws IllegalArgumentException Si le type spécifié n'est pas valide.
     */
    public ColInfo(String name, String type) {
        if (!type.equals("INT") && !type.equals("FLOAT") && !type.startsWith("STRING")
                && !type.startsWith("VARSTRING")) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        this.name = name;
        this.type = type;
    }

    /**
     * Obtient le nom de la colonne.
     * 
     * @return Le nom de la colonne.
     */
    public String getName() {
        return name;
    }

    /**
     * Definit le nom de la colonne.
     * 
     * @param name Le nom de la colonne à définir.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtient le type de données de la colonne.
     * 
     * @return Le type de données de la colonne.
     */
    public String getType() {
        return type;
    }

    /**
     * Définit le type de données de la colonne.
     * 
     * @param type Le type de données de la colonne à définir.
     */
    public void setType(String type) {
        this.type = type;
    }

}