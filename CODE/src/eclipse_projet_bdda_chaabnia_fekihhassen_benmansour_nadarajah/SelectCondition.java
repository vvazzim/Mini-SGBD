package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.util.List;

/**
 * Représente une condition pour une requête SELECT sur une table de la base de
 * données.
 */
public class SelectCondition {
  private String columnName; // the name of the column to compare with
  private String operator; // =, <, >, <=, >=, !=
  private String value; // the value to compare with

  /**
   * Crée une instance de condition avec les éléments spécifiés.
   *
   * @param columnName Le nom de la colonne à comparer.
   * @param operator   L'opérateur de comparaison (=, <, >, <=, >=, !=).
   * @param value      La valeur à comparer.
   */
  public SelectCondition(String columnName, String operator, String value) {
    this.columnName = columnName;
    this.operator = operator;
    this.value = value;
  }

  /**
   * Crée une instance de condition sans condition spécifique (tous les attributs
   * sont nuls).
   */
  public SelectCondition() {
    this.columnName = null;
    this.operator = null;
    this.value = null;
  }

  // Getters for the attributes
  public String getColumnName() {
    return columnName;
  }

  public String getOperator() {
    return operator;
  }

  public String getValue() {
    return value;
  }

  /**
   * Vérifie si un enregistrement satisfait cette condition.
   *
   * @param record L'enregistrement à vérifier.
   * @return True si l'enregistrement satisfait la condition, sinon False.
   */
  public boolean isSatisfiedBy(Record record) {
    // Get the value of the column in the record
    int columnIndex = -1;
    List<ColInfo> tableCols = record.getTabInfo().getColInfoList();
    for (int i = 0; i < tableCols.size(); i++) {
      if (tableCols.get(i).getName().equals(columnName)) {
        columnIndex = i;
        break;
      }
    }

    if (columnIndex == -1) {
      // Handle the case where the column does not exist in the record
      System.out.println("The column " + columnName + " does not exist in the record");
      return false;
    } else {
      System.out.println("The column " + columnName + " exists in the record");
    }

    String columnValue = record.getRecvalues().get(columnIndex);
    if (columnValue == null) {
      // Handle the case where the column value is null
      System.out.println(
          "The column " + columnName + " has a null value in the record because of a certain problem");
      return false;
    } else {
      System.out.println(
          "The column " + columnName + " has a non-null value in the record because of a certain problem");
    }

    // Compare the value with the condition
    switch (operator) {
      case "=":
        return columnValue.equals(value);
      case "<":
        return columnValue.compareTo(value) < 0;
      case ">":
        return columnValue.compareTo(value) > 0;
      case "<=":
        return columnValue.compareTo(value) <= 0;
      case ">=":
        return columnValue.compareTo(value) >= 0;
      case "!=":
        return !columnValue.equals(value);
      default:
        return false;
    }
  }
}