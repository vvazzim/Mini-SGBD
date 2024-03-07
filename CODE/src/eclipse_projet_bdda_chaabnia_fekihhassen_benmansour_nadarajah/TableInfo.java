package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.util.ArrayList;

/**
 * Représente les informations d'une table dans la base de données.
 */
public class TableInfo {
    private String nom_relation;
    private int nb_colonnes;
    private ArrayList<ColInfo> colInfoList;
    private PageId headerPageId;

    /**
     * Crée une instance de TableInfo avec le nom de relation, le nombre de colonnes
     * et l'identifiant de la page d'en-tête spécifiés.
     *
     * @param nom_relation Le nom de la relation.
     * @param nb_colonnes  Le nombre de colonnes dans la table.
     * @param headerPageId L'identifiant de la page d'en-tête de la table.
     */
    public TableInfo(String nom_relation, int nb_colonnes, PageId headerPageId) {
        this.nom_relation = nom_relation;
        this.nb_colonnes = nb_colonnes;
        this.colInfoList = new ArrayList<>();
        this.headerPageId = headerPageId;
    }

    /**
     * Crée une instance de TableInfo avec le nom de relation, la liste des
     * informations de colonnes et l'identifiant de la page d'en-tête spécifiés.
     *
     * @param nom_relation Le nom de la relation.
     * @param colInfoList  La liste des informations de colonnes.
     * @param headerPageId L'identifiant de la page d'en-tête de la table.
     */
    public TableInfo(String nom_relation, ArrayList<ColInfo> colInfoList, PageId headerPageId) {
        this.nom_relation = nom_relation;
        this.nb_colonnes = colInfoList.size();
        this.colInfoList = colInfoList;
        this.headerPageId = headerPageId;
    }

    /**
     * Crée une instance de TableInfo avec uniquement l'identifiant de la page
     * d'en-tête spécifié.
     *
     * @param headerPageId L'identifiant de la page d'en-tête de la table.
     */
    public TableInfo(PageId headerPageId) {
        this.headerPageId = headerPageId;
    }

    /**
     * Obtient le nom de la relation.
     *
     * @return Le nom de la relation.
     */
    public String getNom_relation() {
        return nom_relation;
    }

    /**
     * Définit le nom de la relation.
     *
     * @param nom_relation Le nom de la relation à définir.
     */
    public void setNom_relation(String nom_relation) {
        this.nom_relation = nom_relation;
    }

    /**
     * Obtient le nombre de colonnes dans la table.
     *
     * @return Le nombre de colonnes.
     */
    public int getNb_colonnes() {
        return nb_colonnes;
    }

    /**
     * Définit le nombre de colonnes dans la table.
     *
     * @param nb_colonnes Le nombre de colonnes à définir.
     */
    public void setNb_colonnes(int nb_colonnes) {
        this.nb_colonnes = nb_colonnes;
    }

    /**
     * Obtient la liste des informations de colonnes.
     *
     * @return La liste des informations de colonnes.
     */
    public ArrayList<ColInfo> getColInfoList() {
        return colInfoList;
    }

    /**
     * Définit la liste des informations de colonnes.
     *
     * @param colInfoList La liste des informations de colonnes à définir.
     */
    public void setColInfoList(ArrayList<ColInfo> colInfoList) {
        this.colInfoList = colInfoList;
    }

    /**
     * Obtient l'identifiant de la page d'en-tête de la table.
     *
     * @return L'identifiant de la page d'en-tête.
     */
    public PageId getHeaderPageId() {
        return headerPageId;
    }

    /**
     * Définit l'identifiant de la page d'en-tête de la table.
     *
     * @param headerPageId L'identifiant de la page d'en-tête à définir.
     */
    public void setHeaderPageId(PageId headerPageId) {
        this.headerPageId = headerPageId;
    }

    /**
     * Affiche les informations de la table, y compris le nom de la table, le nombre
     * de colonnes et les informations de colonnes.
     */
    public void printTableInfo() {
        System.out.println("Nom de la relation: " + nom_relation);
        // System.out.println("N: " + nb_colonnes);
        System.out.println("Colonnes:");
        for (ColInfo colInfo : colInfoList) {
            System.out.println("  Nom: " + colInfo.getName() + ", Type: " + colInfo.getType());
        }
    }

}
