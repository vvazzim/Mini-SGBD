package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.io.*;
import java.util.ArrayList;

/**
 * Cette classe représente les informations sur la base de données et les
 * informations sur les tables.
 */
public class DataBaseInfo {
    private static DataBaseInfo instance;
    private ArrayList<TableInfo> tableInfoList;
    private int compteur;

    /**
     * Constructeur de DataBaseInfo.
     */
    private DataBaseInfo() {
        tableInfoList = new ArrayList<>();
        compteur = 0;
    }

    /**
     * Obtenir l'instance unique de la classe DataBaseInfo.
     *
     * @return L'instance unique de DataBaseInfo.
     */
    public static DataBaseInfo getInstance() {
        if (instance == null) {
            instance = new DataBaseInfo();
        }
        return instance;
    }

    /**
     * Initialise les informations de la base de données en lisant depuis un
     * fichier.
     */
    public void init() {
        readFromFile();
        System.out.println("DatabaseInfo initialisé.");
    }

    /**
     * Finalise les informations de la base de données en les sauvegardant dans un
     * fichier.
     */
    public void finish() {
        saveToFile();
        System.out.println("DataBaseInfo fini.");
    }

    /**
     * Sauvegarde les informations de la base de données dans un fichier.
     */
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("BD\\DBInfo.save"))) {
            // oos.writeObject(tableInfoList);
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    /**
     * Lit les informations de la base de données depuis un fichier.
     */
    private void readFromFile() {
        File file = new File(DBParams.DBPath + "\\DBInfo.save");

        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                tableInfoList = (ArrayList<TableInfo>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                // e.printStackTrace();
            }
        }
    }

    /**
     * Ajoute des informations sur une table à la base de données.
     *
     * @param tableInfo Les informations de la table à ajouter.
     */
    public void addTableInfo(TableInfo tableInfo) {
        tableInfoList.add(tableInfo);
        compteur++;
    }

    /**
     * Obtient les informations d'une table par son nom.
     *
     * @param tableName Le nom de la table.
     * @return Les informations de la table ou null si la table n'existe pas.
     */
    public TableInfo getTableInfo(String tableName) {
        for (TableInfo tableInfo : tableInfoList) {
            if (tableInfo.getNom_relation().equals(tableName)) {
                return tableInfo;
            }
        }
        return null;
    }

    /**
     * Obtient la liste des informations sur les tables de la base de données.
     *
     * @return La liste des informations sur les tables.
     */
    public ArrayList<TableInfo> getTableInfoList() {
        return tableInfoList;
    }

    /**
     * Obtient le compteur du nombre de tables dans la base de données.
     *
     * @return Le compteur du nombre de tables.
     */
    public int getCompteur() {
        return compteur;
    }

    /**
     * Vérifie si une table existe dans la base de données par son nom.
     *
     * @param tableName Le nom de la table.
     * @return True si la table existe, sinon False.
     */
    public boolean tableExists(String tableName) {
        for (TableInfo table : tableInfoList) {
            if (table.getNom_relation().equals(tableName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Réinitialise les informations de la base de données, supprimant toutes les
     * tables.
     */
    public void reset() {
        tableInfoList.clear();
        compteur = 0;
        System.out.println("DatabaseInfo reset.");
    }
}
