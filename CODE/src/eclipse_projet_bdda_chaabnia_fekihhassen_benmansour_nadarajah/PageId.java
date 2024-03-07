package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

/**
 * Représente l'identifiant d'une page de fichier, composé du numéro de fichier
 * et du numéro de page.
 */
public class PageId {
    private int fileId; // id du fichier
    private int pageId; // id de la page

    /**
     * Initialise un nouveau PageId avec les numéros de fichier et de page
     * spécifiés.
     *
     * @param fileId Le numéro de fichier.
     * @param pageId Le numéro de page.
     */
    public PageId(int fileId, int pageId) {
        this.fileId = fileId;
        this.pageId = pageId;
    }

    /**
     * Obtient le numéro de fichier associé à cet identifiant de page.
     *
     * @return Le numéro de fichier.
     */
    public int getFileIdx() {
        return fileId;
    }

    /**
     * Obtient le numéro de page associé à cet identifiant de page.
     *
     * @return Le numéro de page.
     */
    public int getPageIdx() {
        return pageId;
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères de cet
     * identifiant de page,
     * indiquant le numéro de fichier et le numéro de page.
     *
     * @return Une chaîne de caractères représentant l'identifiant de page.
     */
    @Override
    public String toString() {
        return "PageId{" + "File Id=" + fileId + ", Numero de page=" + pageId + '}';
    }
}
