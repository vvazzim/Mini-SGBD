package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.util.ArrayList;

/**
 * Représente une commande d'insertion de données dans une table.
 */
public class InsertCommand {
    private String nomRelation;
    private ArrayList<String> values;
    FileManager fileManager = FileManager.getInstance();

    /**
     * Initialise une nouvelle instance de la commande d'insertion en faisant un
     * parsing de la commande fournie.
     *
     * @param command La commande d'insertion sous forme de chaîne de caractères.
     */
    public InsertCommand(String command) {
        parseCommand(command);
    }

    /**
     * Analyse la commande d'insertion pour extraire le nom de la relation et les
     * valeurs à insérer.
     *
     * @param command La commande d'insertion sous forme de chaine de caractères.
     * @throws IllegalArgumentException Si le format de la commande est incorrect.
     */
    private void parseCommand(String command) {
        String[] parts = command.split("VALUES");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Format incorrect pour la commande INSERT");
        }

        this.nomRelation = parts[0].split("\\s+")[2].trim();

        String valuePart = parts[1].trim();
        valuePart = valuePart.substring(1, valuePart.length() - 1);

        this.values = new ArrayList<>();

        for (String val : valuePart.split(",")) {
            values.add(val.trim());
        }
    }

    /**
     * Exécute la commande d'insertion en ajoutant un record à la table spécifiée.
     *
     * @throws Exception Si une erreur survient pendant l'exécution de la commande.
     */
    public void execute() {
        try {
            TableInfo tableInfo = DataBaseInfo.getInstance().getTableInfo(nomRelation);

            if (tableInfo == null) {
                throw new IllegalArgumentException("Table non trouvée : " + nomRelation);
            } else {
                System.out.println("Table trouvée : " + nomRelation);
            }

            if (tableInfo.getNb_colonnes() != values.size()) {
                throw new IllegalArgumentException(
                        "Le nombre de valeurs fournies ne correspond pas au nombre de colonnes dans la table");
            }

            Record record = new Record(tableInfo);

            for (int i = 0; i < values.size(); i++) {
                String value = values.get(i);
                String colType = tableInfo.getColInfoList().get(i).getType();

                if (colType.equalsIgnoreCase("INT")) {
                    record.addValue(Integer.parseInt(value));
                } else if (colType.equalsIgnoreCase("FLOAT")) {
                    record.addValue(Float.parseFloat(value));
                } else if (colType.toUpperCase().startsWith("STRING")) {
                    record.addValue(value);
                } else if (colType.toUpperCase().startsWith("VARSTRING")) {
                    record.addValue(value);
                } else {
                    throw new IllegalArgumentException("Type de colonne non supporté : " + colType);
                }
            }

            FileManager.getInstance().InsertRecordIntoTable(record);
        } catch (Exception e) {
            System.out.println("Une exception s'est produite lors de l'exécution de la commande INSERT.");
            e.printStackTrace();
        }
    }
}