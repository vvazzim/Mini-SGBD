package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.io.File;

/**
 * Commande pour réinitialiser la base de données en supprimant tous les fichiers et en réinitialisant les gestionnaires.
 */
public class ResetDBCommand {

  /**
   * Exécute la réinitialisation de la base de données en supprimant tous les fichiers de la base de données,
   * en réinitialisant le BufferManager, le DataBaseInfo et le DiskManager.
   */
  public void execute() {
    // Delete all files in the DB folder
    File path = new File(DBParams.DBPath);
    if (path.exists()) {
      File[] files = path.listFiles();
      for (File file : files) {
        file.delete();
      }
    }

    // Reset BufferManager
    BufferManager.getInstance().reset();

    // Reset DBInfo and FileManager
    DataBaseInfo.getInstance().reset();
    // FileManager.getInstance().reset(); pas besoin je crois ?
    
    DiskManager.getInstance().reset();
    
  }
}