package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Cette classe gère la commande d'importation de données à partir d'un fichier
 * CSV dans une table.
 */
public class ImportCommand {
    private String relationName; // Nom de la relation cible
    private String csvFileName; // Nom du fichier CSV source

    /**
     * Constructeur de la classe. Il prend la commande d'importation comme argument
     * et le parse.
     *
     * @param command La commande d'importation.
     */
    public ImportCommand(String command) {
        parseCommand(command);
    }

    /**
     * Parse la commande d'importation pour extraire le nom de la relation et le nom
     * du fichier CSV.
     *
     * @param command La commande d'importation.
     */
    private void parseCommand(String command) {
        String[] commandParts = command.split(" ");
        if (commandParts.length != 4 || !commandParts[0].equalsIgnoreCase("IMPORT")
                || !commandParts[2].equalsIgnoreCase("INTO")) {
            throw new IllegalArgumentException("Commande d'importation incorrecte.");
        }

        this.relationName = commandParts[1];
        this.csvFileName = commandParts[3];
    }

    /**
     * Exécute la commande d'importation en lisant les données depuis le fichier CSV
     * et en les insérant dans la table.
     *
     * @throws Exception En cas d'erreur lors de l'exécution de la commande.
     */
    public void execute() throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                Record record = createRecordWithValues(values);

                FileManager.getInstance().InsertRecordIntoTable(record);
            }
        } catch (IOException e) {
            throw new IOException("Erreur lors de la lecture du fichier CSV.", e);
        }
    }

    /**
     * Crée un objet Record à partir des valeurs extraites du fichier CSV.
     *
     * @param values Les valeurs extraites du fichier CSV.
     * @return Un objet Record avec les valeurs.
     * @throws Exception En cas d'erreur lors de la création de l'objet Record.
     */
    private Record createRecordWithValues(String[] values) throws Exception {
        TableInfo tableInfo = DataBaseInfo.getInstance().getTableInfo(relationName);
        if (tableInfo == null) {
            throw new IllegalArgumentException("Table non trouvée : " + relationName);
        }

        if (tableInfo.getNb_colonnes() != values.length) {
            throw new IllegalArgumentException(
                    "Le nombre de valeurs fournies ne correspond pas au nombre de colonnes dans la table");
        }

        Record record = new Record(tableInfo);
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            String colType = tableInfo.getColInfoList().get(i).getType();
            switch (colType.toUpperCase()) {
                case "INT":
                    record.addValue(Integer.parseInt(value));
                    System.out.println("Added INT value: " + value);
                    break;
                case "FLOAT":
                    record.addValue(Float.parseFloat(value));
                    System.out.println("Added FLOAT value: " + value);
                    break;
                case "STRING":
                    record.addValue(value);
                    System.out.println("Added STRING value: " + value);
                    break;
                default:
                    if (colType.toUpperCase().startsWith("VARSTRING")) {
                        record.addValue(value);
                        System.out.println("Added VARSTRING value: " + value);
                    } else {
                        throw new IllegalArgumentException("Type de colonne non supporté : " + colType);
                    }
            }
        }

        return record;
    }
}
