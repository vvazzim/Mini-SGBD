package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

/**
 * Représente l'identifiant d'un enregistrement, composé d'un PageId et d'un
 * index de slot.
 */
public class RecordId {
    private PageId pageId;
    private int slotIdx;

    /**
     * Initialise un nouvel identifiant d'enregistrement avec le PageId et l'index
     * de slot spécifiés.
     *
     * @param pageId    Le PageId associé à cet identifiant d'enregistrement.
     * @param slotIndex L'index de slot associé à cet identifiant d'enregistrement.
     */
    public RecordId(PageId pageId, int slotIndex) {
        this.pageId = pageId;
        this.slotIdx = slotIndex;
    }

    /**
     * Obtient le PageId associé à cet identifiant d'enregistrement.
     *
     * @return Le PageId associé.
     */
    public PageId getPageId() {
        return pageId;
    }

    /**
     * Définit le PageId associé à cet identifiant d'enregistrement.
     *
     * @param pageId Le nouveau PageId associé.
     */
    public void setPageId(PageId pageId) {
        this.pageId = pageId;
    }

    /**
     * Obtient l'index de slot associé à cet identifiant d'enregistrement.
     *
     * @return L'index de slot associé.
     */
    public int getSlotIdx() {
        return slotIdx;
    }

    /**
     * Définit l'index de slot associé à cet identifiant d'enregistrement.
     *
     * @param slotIdx Le nouvel index de slot associé.
     */
    public void setSlotIdx(int slotIdx) {
        this.slotIdx = slotIdx;
    }
}