package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Commande pour effectuer une requête SELECT sur une table de la base de
 * données.
 */
public class SelectCommand {
  private String relationName;
  private FileManager fileManager;
  private ArrayList<SelectCondition> conditions; // New field to store the conditions
  private boolean condition = false;

  /**
   * Crée une instance de la commande SELECT en analysant la commande fournie.
   *
   * @param command La commande SELECT sous forme de chaîne de caractères.
   * @throws IllegalArgumentException Si la commande est mal formée.
   */
  public SelectCommand(String command) {
    String[] commandParts = command.split(" ");
    if (commandParts.length < 4) {
      throw new IllegalArgumentException("Commande mal formée");
    }
    this.relationName = commandParts[3];
    this.fileManager = FileManager.getInstance();

    for (String elements : commandParts) {
      if (elements.equals("WHERE")) {
        this.condition = true;
        break;
      }
    }

    if (this.condition) {
      String conditionsStr = command.substring(command.indexOf("WHERE") + 6).trim();
      this.conditions = parseConditions(conditionsStr);
    }
  }

  private ArrayList<SelectCondition> parseConditions(String conditionsStr) {
    ArrayList<SelectCondition> parsedConditions = new ArrayList<>();

    if (!conditionsStr.isEmpty()) {
      String[] conditionsSplit = conditionsStr.split(" AND ");
      for (String conditionStr : conditionsSplit) {
        parsedConditions.add(parseEachCondition(conditionStr.trim()));
      }
    } else {
      parsedConditions.add(new SelectCondition());
    }

    return parsedConditions;
  }

  private SelectCondition parseEachCondition(String conditionStr) {
    String[] parts = conditionStr.split("=|<|>|<=|>=|<>", 2); // Split only on the first occurrence
    if (parts.length < 2) {
      // Handle invalid condition
      throw new IllegalArgumentException("Invalid condition: " + conditionStr);
    }

    String columnName = parts[0].trim();
    String rest = parts[1].trim();

    // Extract the operator
    String operator = "";
    if (conditionStr.contains("=")) {
      operator = "=";
    } else if (conditionStr.contains("<>")) {
      operator = "<>";
    } else if (conditionStr.contains("<=")) {
      operator = "<=";
    } else if (conditionStr.contains(">=")) {
      operator = ">=";
    } else if (conditionStr.contains("<")) {
      operator = "<";
    } else if (conditionStr.contains(">")) {
      operator = ">";
    }

    String value = rest.substring(operator.length()).trim();

    return new SelectCondition(columnName, operator, value);
  }

  /**
   * Exécute la requête SELECT sur la table spécifiée en appliquant les conditions
   * si elles sont présentes.
   *
   * @throws IOException           En cas d'erreur d'entrée/sortie.
   * @throws PageNotFoundException Si une page n'est pas trouvée.
   */
  public void execute() {
    System.out.println("SELECT command...");

    try {
      TableInfo tableInfo = DataBaseInfo.getInstance().getTableInfo(relationName);

      if (tableInfo == null) {
        System.out.println("La Table \"" + relationName + "\" n'existe pas.");
        return;
      }

      // System.out.println("Fetching all records from table \"" + relationName +
      // "\"...");
      System.out.println(
          "Nombre de colonnes de la table " + tableInfo.getNom_relation() + " est  = " + tableInfo.getNb_colonnes());
      System.out.println(
          "Column names = " + tableInfo.getColInfoList().stream().map(ColInfo::getName).collect(Collectors.toList()));
      System.out.println("Header page id = " + tableInfo.getHeaderPageId());

      // Get all records using fileManager
      List<Record> allRecords = fileManager.GetAllRecords(tableInfo);
      // System.out.println("Number of records fetched from fileManager = " +
      // allRecords.size());

      List<Record> records = new ArrayList<>(allRecords);

      if (this.condition) {
        // System.out.println("Applying conditions...");
        records = records.stream()
            .filter(record -> {
              System.out.println("Checking conditions for record: " + record);
              boolean satisfies = satisfiesConditions(record);
              System.out.println("Does record satisfy conditions? " + satisfies);
              return satisfies;
            })
            .collect(Collectors.toList());
      }

      // System.out.println("Printing records...");
      printRecords(records);

      // System.out.println("Total records = " + records.size());
    } catch (IOException e) {
      System.out.println("An IOException occurred while executing the SELECT command: " + e.getMessage());
    } catch (PageNotFoundException e) {
      System.out.println("A PageNotFoundException occurred while executing the SELECT command: " + e.getMessage());
    }
  }

  private void printRecords(List<Record> records) {
    // System.out.println("Printing records that satisfy the conditions...");
    for (Record record : records) {
      if (satisfiesConditions(record)) {
        ArrayList<String> values = record.getRecvalues();
        for (String value : values) {
          System.out.print(value + " ; ");
        }
        System.out.println(".");
      }
    }
  }

  private boolean satisfiesConditions(Record record) {
    for (SelectCondition condition : conditions) {
      if (!condition.isSatisfiedBy(record)) {
        return false;
      }
    }

    return true;
  }
}