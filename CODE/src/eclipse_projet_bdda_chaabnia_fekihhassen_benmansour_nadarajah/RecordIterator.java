package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Un itérateur pour parcourir les enregistrements dans une page de données
 * associée à une table.
 */
public class RecordIterator {
  private final TableInfo tabInfo;
  private final PageId pageId;
  private ByteBuffer dataPageBuffer;
  private int currentOffset;

  /**
   * Initialise un nouvel itérateur pour parcourir les enregistrements dans une
   * page de données spécifiée.
   *
   * @param tabInfo L'information sur la table associée à la page de données.
   * @param pageId  Le PageId de la page de données à parcourir.
   */
  public RecordIterator(TableInfo tabInfo, PageId pageId) {
    this.tabInfo = tabInfo;
    this.pageId = pageId;
    this.dataPageBuffer = null;
    this.currentOffset = 0;
  }

  /**
   * Obtient le prochain enregistrement dans la page de données, s'il existe.
   *
   * @return Le prochain enregistrement dans la page de données, ou null s'il n'y
   *         en a plus.
   * @throws IOException           Si une erreur d'entrée/sortie se produit.
   * @throws PageNotFoundException Si la page n'a pas pu être trouvée.
   */
  public Record getNextRecord() throws IOException, PageNotFoundException {
    if (dataPageBuffer == null) {
      BufferManager bm = BufferManager.getInstance();
      dataPageBuffer = bm.getPage(pageId);
    }

    int pageSize = DBParams.SGBDPageSize;
    int recordCount = dataPageBuffer.getInt(pageSize - 8);

    if (currentOffset < recordCount) {
      int slotStart = dataPageBuffer.getInt(pageSize - (8 + (currentOffset + 1) * 8));
      int slotSize = dataPageBuffer.getInt(pageSize - (8 + (currentOffset + 1) * 8) + 4);

      ByteBuffer recordBuffer = ByteBuffer.allocate(slotSize);
      int oldLimit = dataPageBuffer.limit();
      dataPageBuffer.position(slotStart);
      dataPageBuffer.limit(slotStart + slotSize);
      recordBuffer.put(dataPageBuffer);
      dataPageBuffer.limit(oldLimit);
      recordBuffer.flip();

      Record record = new Record(tabInfo);
      record.readFromBuffer(recordBuffer.array(), 0);

      currentOffset++;
      return record;
    } else {
      close();
      return null;
    }
  }

  /**
   * Ferme l'itérateur, libère la page de données associée si elle a été chargée.
   *
   * @throws PageNotFoundException Si la page n'a pas pu être trouvée.
   */
  public void close() throws PageNotFoundException {
    if (dataPageBuffer != null) {
      BufferManager bm = BufferManager.getInstance();
      bm.freePage(pageId, 0);
      dataPageBuffer = null;
    }
  }

    /**
   * Réinitialise l'itérateur pour commencer le parcours depuis le début de la
   * page de données.
   */
  public void reset() {
    currentOffset = 0;
  }
}