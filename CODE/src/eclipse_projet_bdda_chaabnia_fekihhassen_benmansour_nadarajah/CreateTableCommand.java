package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Cette classe représente une commande de création de table dans la BDD.
 */
public class CreateTableCommand {
  private String nom_relation; // le no de la relation à créer
  private ArrayList<ColInfo> colInfoList; // liste des informations sur les colonnes de la table
  FileManager fileManager = FileManager.getInstance();

  /**
   * Constructeur de CreateTableCommand.
   * 
   * @param command La commande de création de table
   */
  public CreateTableCommand(String command) {
    try {
      String[] cmd = command.split(" ");
      if (cmd.length < 4) {
        throw new IllegalArgumentException("Commande CREATE TABLE incomplète");
      }

      this.nom_relation = cmd[2];
      this.colInfoList = new ArrayList<>();

      String[] columns = cmd[3].substring(1, cmd[3].length() - 1).split(",");

      for (String column : columns) {
        String[] colInfo = column.split(":");
        if (colInfo.length != 2) {
          throw new IllegalArgumentException("Format incorrect pour la définition des colonnes");
        }

        String colName = colInfo[0];
        String colType = colInfo[1];

        if (!colType.equalsIgnoreCase("INT") && !colType.equalsIgnoreCase("FLOAT")
            && !(colType.toUpperCase().startsWith("STRING") && colType.contains("(") && colType.contains(")"))
            && !colType.toUpperCase().startsWith("VARSTRING")) {
          throw new IllegalArgumentException("Type de colonne non supporté : " + colType);
        }

        ColInfo col = new ColInfo(colName, colType);
        colInfoList.add(col);
      }
    } catch (Exception e) {
      System.out.println("Erreur lors du parsing de la commande : " + e.getMessage());
    }
  }

  /**
   * Execute la commande de création de table.
   */
  public void execute() {
    DataBaseInfo databaseInfo = DataBaseInfo.getInstance();

    if (databaseInfo.tableExists(nom_relation)) {
      System.out.println("Relation \"" + nom_relation + "\" existe déjà.");
      return;
    }
    try {

      PageId headerPageId = fileManager.createNewHeaderPage();
      TableInfo tableInfo = new TableInfo(nom_relation, colInfoList, headerPageId);
      databaseInfo.addTableInfo(tableInfo);
      System.out.println("Table \"" + nom_relation + "\" a bien été créé avec succès.");
      tableInfo.printTableInfo();

    } catch (IOException | PageNotFoundException e) {
      System.out.println("Erreur lors de la création de la table : " + e.getMessage());
    }
  }
}