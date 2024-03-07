package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Cette classe gère la création et la gestion des pages de fichiers sur le
 * disque.
 * Elle est responsable de la création des pages d'en-tête et des pages de
 * données pour une table,
 * de la récupération d'une page de données libre, de l'écriture de records sur
 * des pages de données
 * et de la récupération de tous les records d'une table.
 */
public class FileManager {

  // instance unique
  private static FileManager instance;

  private FileManager() {

  }

  /**
   * Récupère l'instance unique de FileManager.
   *
   * @return L'instance unique de FileManager.
   */
  public static FileManager getInstance() {
    if (instance == null) {

      instance = new FileManager();
    }
    return instance;
  }

  /**
   * Crée une nouvelle page d'en-tête pour une table. Cette page contient des
   * informations sur la table,
   * telles que les index des pages de données associées. Cette méthode alloue une
   * nouvelle page dans le DiskManager,
   * initialise les informations de l'en-tête et la libère dans le BufferManager.
   *
   * @return L'identifiant (PageId) de la nouvelle page d'en-tête créée.
   * @throws IOException           En cas d'erreur lors de l'accès au disque.
   * @throws PageNotFoundException En cas d'indisponibilité de la page dans le
   *                               BufferManager.
   */
  public PageId createNewHeaderPage() throws IOException, PageNotFoundException {
    DiskManager dm = DiskManager.getInstance();
    PageId newHeaderPageId = dm.allocatePage();

    BufferManager bm = BufferManager.getInstance();
    ByteBuffer headerPageBuffer = bm.getPage(newHeaderPageId);

    headerPageBuffer.putInt(-1);
    headerPageBuffer.putInt(-1);

    bm.freePage(newHeaderPageId, 1);

    return newHeaderPageId;
  }

  /**
   * Ajoute une nouvelle page de données à une table donnée. Cette méthode alloue
   * une nouvelle page de données dans le DiskManager, initialise les emplacements
   * de la nouvelle page,
   * met à jour l'en-tête de la table pour refléter la nouvelle page de données,
   * et la libère dans le
   * BufferManager.
   *
   * @param tabInfo Informations sur la table à laquelle ajouter la page de
   *                données.
   * @return L'identifiant (PageId) de la nouvelle page de données créée.
   * @throws IOException           En cas d'erreur lors de l'accès au disque.
   * @throws PageNotFoundException En cas d'indisponibilité de la page dans le
   *                               BufferManager.
   */
  public PageId addDataPage(TableInfo tabInfo) throws IOException, PageNotFoundException {
    DiskManager dm = DiskManager.getInstance();
    PageId newDataPageId = dm.allocatePage();

    BufferManager bm = BufferManager.getInstance();
    ByteBuffer newDataPageBuffer = bm.getPage(newDataPageId);

    // initialise la nouvelle page de données
    int slotCount = (DBParams.SGBDPageSize - 4) / 8; // calcule le nombre total d'emplacements en fonction de l'espace
                                                     // disponible

    for (int i = 0; i < slotCount; i++) {
      newDataPageBuffer.putInt(4 + i * 8, 0); // initialise slots
      newDataPageBuffer.putInt(8 + i * 8, 0); // initialise sizes
    }
    bm.freePage(newDataPageId, 1);

    PageId headerPageId = tabInfo.getHeaderPageId();
    ByteBuffer headerPageBuffer = bm.getPage(headerPageId);

    int firstFreePageFileIdx = headerPageBuffer.getInt(0);
    int firstFreePagePageIdx = headerPageBuffer.getInt(4);

    if (firstFreePageFileIdx == -1 && firstFreePagePageIdx == 0) {
      headerPageBuffer.putInt(0, newDataPageId.getFileIdx());
      headerPageBuffer.putInt(4, newDataPageId.getPageIdx());
    } else {
      PageId lastPageId = new PageId(firstFreePageFileIdx, firstFreePagePageIdx);
      ByteBuffer lastPageBuffer = bm.getPage(lastPageId);

      int nextPageFileIdx = lastPageBuffer.getInt(0);
      int nextPagePageIdx = lastPageBuffer.getInt(4);

      while (nextPageFileIdx != -1 && nextPagePageIdx != 0) {
        lastPageId = new PageId(nextPageFileIdx, nextPagePageIdx);
        lastPageBuffer = bm.getPage(lastPageId);
        nextPageFileIdx = lastPageBuffer.getInt(0);
        nextPagePageIdx = lastPageBuffer.getInt(4);
      }

      lastPageBuffer.putInt(0, newDataPageId.getFileIdx());
      lastPageBuffer.putInt(4, newDataPageId.getPageIdx());
      bm.freePage(lastPageId, 1);
    }

    bm.freePage(headerPageId, 1);
    return newDataPageId;
  }

  /**
   * Obtient l'identifiant d'une page de données libre dans une table donnée,
   * pouvant accueillir un enregistrement
   * de la taille spécifiée. Cette méthode parcourt les pages de données associées
   * à la table à partir de son en-tête,
   * recherche une page avec suffisamment d'espace libre pour stocker
   * l'enregistrement, puis renvoie l'identifiant de cette page.
   *
   * @param tabInfo    Informations sur la table à laquelle ajouter la page de
   *                   données.
   * @param sizeRecord Taille de l'enregistrement à stocker.
   * @return L'identifiant (PageId) de la page de données libre pouvant stocker
   *         l'enregistrement ou null si aucune page n'est disponible.
   * @throws IOException           En cas d'erreur lors de l'accès au disque.
   * @throws PageNotFoundException En cas d'indisponibilité de la page dans le
   *                               BufferManager.
   */
  public PageId getFreeDataPageId(TableInfo tabInfo, int sizeRecord) throws IOException, PageNotFoundException {
    BufferManager bm = BufferManager.getInstance();
    PageId headerPageId = tabInfo.getHeaderPageId();
    ByteBuffer headerPageBuffer = bm.getPage(headerPageId);

    int numDataPages = headerPageBuffer.getInt(0);

    for (int i = 0; i < numDataPages; i++) {
      int dataPageFileIdx = headerPageBuffer.getInt(4 + i * 12);
      int dataPagePageIdx = headerPageBuffer.getInt(8 + i * 12);
      int freeSpace = headerPageBuffer.getInt(12 + i * 12);

      PageId dataPageId = new PageId(dataPageFileIdx, dataPagePageIdx);
      ByteBuffer dataPageBuffer = bm.getPage(dataPageId);

      int slotCount = (DBParams.SGBDPageSize - 8) / 8;
      boolean pageHasSpace = false;

      for (int j = 0; j < slotCount; j++) {
        int slotStart = dataPageBuffer.getInt(4 + j * 8);
        int slotSize = dataPageBuffer.getInt(8 + j * 8);

        if (slotStart == 0 && slotSize == 0) {
          if (sizeRecord <= freeSpace) {
            pageHasSpace = true;
            break;
          }
        }
      }

      if (pageHasSpace) {
        bm.freePage(headerPageId, 0);
        bm.freePage(dataPageId, 0);
        return dataPageId;
      }

      bm.freePage(dataPageId, 0);
    }

    bm.freePage(headerPageId, 0);
    return null;
  }

  /**
   * Écrit un enregistrement record sur une page de données spécifiée. Cette
   * méthode recherche un emplacement
   * libre sur la page de données pour stocker l'enregistrement, l'écrit à cet
   * emplacement et met à jour les informations
   * de slot correspondantes. Elle renvoie l'identifiant (RecordId) de
   * l'emplacement où l'enregistrement a été écrit.
   *
   * @param record L'enregistrement (Record) à écrire sur la page de données.
   * @param pageId L'identifiant (PageId) de la page de données sur laquelle
   *               écrire l'enregistrement.
   * @return L'identifiant (RecordId) de l'emplacement où l'enregistrement a été
   *         écrit.
   * @throws IOException           En cas d'erreur lors de l'accès au disque.
   * @throws PageNotFoundException En cas d'indisponibilité de la page dans le
   *                               BufferManager.
   * @throws IOException           Si aucun emplacement libre n'est disponible sur
   *                               la page de données.
   */
  public RecordId writeRecordToDataPage(Record record, PageId pageId) throws IOException, PageNotFoundException {

    BufferManager bm = BufferManager.getInstance();
    ByteBuffer dataPageBuffer = bm.getPage(pageId);
    byte[] dataPageArray = new byte[dataPageBuffer.capacity()];
    dataPageBuffer.get(dataPageArray);

    int slotCount = (DBParams.SGBDPageSize - 8) / 8;
    int freeSlotIndex = -1;
    int recordStart = -1;

    for (int i = 0; i < slotCount; i++) {
      int slotStart = ByteBuffer.wrap(dataPageArray, 4 + i * 8, 4).getInt();
      int slotSize = ByteBuffer.wrap(dataPageArray, 8 + i * 8, 4).getInt();

      if (slotStart == 0 && slotSize == 0) {
        freeSlotIndex = i;
        recordStart = 8 + i * 8;
        break;
      }
    }

    if (freeSlotIndex == -1) {
      bm.freePage(pageId, 0);
      throw new IOException("Aucun emplacement libre sur la page de données.");
    }

    byte[] recordArray = new byte[record.getSize()];
    record.writeToBuffer(recordArray, 0);
    System.arraycopy(recordArray, 0, dataPageArray, recordStart, recordArray.length);

    ByteBuffer.wrap(dataPageArray).putInt(4 + freeSlotIndex * 8, recordStart);
    ByteBuffer.wrap(dataPageArray).putInt(8 + freeSlotIndex * 8, recordArray.length);

    dataPageBuffer.clear();
    dataPageBuffer.put(dataPageArray);

    bm.freePage(pageId, 1);

    return new RecordId(pageId, freeSlotIndex);
  }

  /**
   * Récupère la liste des records stockés sur une page de
   * données spécifiée. Cette méthode
   * parcourt la page de données, lit les enregistrements présents dans les
   * emplacements de slot et les renvoie
   * sous forme de liste.
   *
   * @param tabInfo L'information sur la table associée aux enregistrements.
   * @param pageId  L'identifiant (PageId) de la page de données à partir de
   *                laquelle récupérer les enregistrements.
   * @return Une liste des enregistrements présents sur la page de données.
   * @throws IOException           En cas d'erreur lors de l'accès au disque.
   * @throws PageNotFoundException En cas de d'indisponibilité de la page dans le
   *                               BufferManager.
   */
  public List<Record> getRecordsInDataPage(TableInfo tabInfo, PageId pageId) throws IOException, PageNotFoundException {
    if (tabInfo == null) {
      // System.out.println("tabInfo is null");
      return Collections.emptyList();
    }
    if (pageId == null) {
      // System.out.println("pageId is null");
      return Collections.emptyList();
    }

    List<Record> records = new ArrayList<>();
    BufferManager bm = BufferManager.getInstance();
    byte[] dataPageBuffer = bm.getPage(pageId).array();
    if (dataPageBuffer == null) {
      // System.out.println("dataPageBuffer is null");
      return Collections.emptyList();
    }

    try {
      int slotCount = (DBParams.SGBDPageSize - 8) / 8;

      for (int i = 0; i < slotCount; i++) {
        int slotStart = ByteBuffer.wrap(dataPageBuffer, 4 + i * 8, 4).getInt();
        int slotSize = ByteBuffer.wrap(dataPageBuffer, 8 + i * 8, 4).getInt();

        // System.out.println("Slot " + i + ": Start = " + slotStart + ", Size = " +
        // slotSize);

        if (slotStart > 0 && slotSize > 0) {
          byte[] recordBuffer = new byte[slotSize];
          System.arraycopy(dataPageBuffer, slotStart, recordBuffer, 0, slotSize);

          // System.out.println("Record Buffer: " + Arrays.toString(recordBuffer));

          Record record = new Record(tabInfo);
          record.readFromBuffer(recordBuffer, 0);
          records.add(record);

          // System.out.println("Added Record: " + record);
        }
      }

      return records;
    } finally {
      bm.freePage(pageId, 0);
    }
  }

  /**
   * Récupère la liste des PageIds des pages de données associées à une table.
   *
   * @param tabInfo Les informations de la table pour lesquelles les PageIds sont
   *                récupérés.
   * @return Une liste de PageIds des pages de données de la table.
   * @throws IOException           En cas d'erreur d'entrée/sortie lors de la
   *                               lecture des données.
   * @throws PageNotFoundException Si une page nécessaire n'a pas été trouvée dans
   *                               le gestionnaire de tampons.
   */
  public List<PageId> getDataPages(TableInfo tabInfo) throws IOException, PageNotFoundException {
    if (tabInfo == null) {
      // System.out.println("tabInfo is null");
      return Collections.emptyList();
    }

    List<PageId> dataPageIds = new ArrayList<>();
    BufferManager bm = BufferManager.getInstance();
    PageId headerPageId = tabInfo.getHeaderPageId();
    ByteBuffer headerPageBuffer = bm.getPage(headerPageId);
    if (headerPageBuffer == null) {
      // System.out.println("headerPageBuffer is null");
      return Collections.emptyList();
    }

    try {
      int numDataPages = headerPageBuffer.getInt(0);
      // System.out.println("Number of data pages: " + numDataPages);

      for (int i = 0; i < numDataPages; i++) {
        int dataPageFileIdx = headerPageBuffer.getInt(4 + i * 12);
        int dataPagePageIdx = headerPageBuffer.getInt(8 + i * 12);
        PageId dataPageId = new PageId(dataPageFileIdx, dataPagePageIdx);
        dataPageIds.add(dataPageId);
        // System.out.println("Added data page: " + dataPageId);
      }

      return dataPageIds;
    } finally {
      bm.freePage(headerPageId, 0);
    }
  }

  /**
   * Insère un enregistrement dans une table spécifique.
   *
   * @param record L'enregistrement à insérer dans la table.
   * @return L'identifiant de l'enregistrement inséré.
   * @throws IOException           En cas d'erreur d'entrée/sortie lors de la
   *                               lecture/écriture des données.
   * @throws PageNotFoundException Si une page nécessaire n'a pas été trouvée dans
   *                               le gestionnaire de tampons.
   */
  public RecordId InsertRecordIntoTable(Record record) throws IOException, PageNotFoundException {
    BufferManager bm = BufferManager.getInstance();
    TableInfo tabInfo = record.getTabInfo();
    PageId dataPageId = getFreeDataPageId(tabInfo, record.getSize());

    if (dataPageId == null) {
      dataPageId = addDataPage(tabInfo);
      // System.out.println("Ajout d'une nouvelle page de données: " + dataPageId);
    } else {
      // System.out.println("Page de données free trouvées: " + dataPageId);
    }

    ByteBuffer dataPageBuffer = bm.getPage(dataPageId);
    byte[] dataPageArray = new byte[dataPageBuffer.capacity()];
    dataPageBuffer.get(dataPageArray);

    int offset = ByteBuffer.wrap(dataPageArray, DBParams.SGBDPageSize - 4, 4).getInt();
    byte[] recordArray = new byte[record.getSize()];
    record.writeToBuffer(recordArray, 0);
    System.arraycopy(recordArray, 0, dataPageArray, offset, recordArray.length);

    ByteBuffer.wrap(dataPageArray).putInt(DBParams.SGBDPageSize - 4, offset + recordArray.length);
    int recordCount = ByteBuffer.wrap(dataPageArray, DBParams.SGBDPageSize - 8, 4).getInt();
    ByteBuffer.wrap(dataPageArray).putInt(DBParams.SGBDPageSize - 8, recordCount + 1);
    ByteBuffer.wrap(dataPageArray).putInt(DBParams.SGBDPageSize - (8 + (recordCount + 1) * 8), offset);
    ByteBuffer.wrap(dataPageArray).putInt(DBParams.SGBDPageSize - (8 + (recordCount + 1) * 8) + 4,
        recordArray.length);

    dataPageBuffer.clear();
    dataPageBuffer.put(dataPageArray);

    bm.freePage(dataPageId, 1);

    ByteBuffer headerPageBuffer = bm.getPage(tabInfo.getHeaderPageId());
    byte[] headerPageArray = new byte[headerPageBuffer.capacity()];
    headerPageBuffer.get(headerPageArray);

    int slotCount = (DBParams.SGBDPageSize - 8) / 8;

    for (int i = 0; i < slotCount; i++) {
      int slotStart = ByteBuffer.wrap(headerPageArray, 4 + i * 8, 4).getInt();
      int slotSize = ByteBuffer.wrap(headerPageArray, 8 + i * 8, 4).getInt();

      if (slotStart == 0 && slotSize == 0) {
        ByteBuffer.wrap(headerPageArray).putInt(4 + i * 8, dataPageId.getPageIdx());
        ByteBuffer.wrap(headerPageArray).putInt(8 + i * 8, record.getSize());
        break;
      }
    }

    System.out.println("Details du record:");
    System.out.println("Table: " + record.getTabInfo().getNom_relation());
    System.out.println("Taille: " + record.getSize());
    System.out.println("Contenue:");
    record.printRecordDetails(); // Create a method in Record class to print its details

    headerPageBuffer.clear();
    headerPageBuffer.put(headerPageArray);

    bm.freePage(tabInfo.getHeaderPageId(), 1);

    return new RecordId(dataPageId, recordCount + 1);
  }

  /**
   * Récupère tous les enregistrements d'une table spécifique.
   *
   * @param tabInfo Les informations de la table à partir de laquelle les
   *                enregistrements sont extraits.
   * @return Une liste de tous les enregistrements de la table.
   * @throws IOException           En cas d'erreur d'entrée/sortie lors de la
   *                               lecture des données.
   * @throws PageNotFoundException Si une page nécessaire n'a pas été trouvée dans
   *                               le gestionnaire de tampons.
   */
  public List<Record> GetAllRecords(TableInfo tabInfo) throws IOException, PageNotFoundException {
    List<Record> records = new ArrayList<>();
    List<PageId> dataPageIds = getDataPages(tabInfo);
    BufferManager bm = BufferManager.getInstance();

    for (PageId dataPageId : dataPageIds) {
      List<Record> pageRecords = getRecordsInDataPage(tabInfo, dataPageId);
      records.addAll(pageRecords);
    }

    return records;
  }

}