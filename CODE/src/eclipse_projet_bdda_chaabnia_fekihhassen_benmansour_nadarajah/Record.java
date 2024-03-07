package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Représente un enregistrement de données associé à une table.
 */
public class Record {
    private TableInfo tabInfo;
    private ArrayList<String> recvalues;
    private int size;

    /**
     * Initialise un nouvel enregistrement de données associé à la table spécifiée.
     *
     * @param tabInfo Informations sur la table à laquelle cet enregistrement est
     *                associé.
     */
    public Record(TableInfo tabInfo) {
        this.tabInfo = tabInfo;
        this.recvalues = new ArrayList<>();

    }

    /**
     * Calcule la taille de l'enregistrement en octets.
     *
     * @return La taille de l'enregistrement.
     */
    private int calculateSize() {
        int recordSize = 0;

        for (int i = 0; i < recvalues.size(); i++) {
            String colType = tabInfo.getColInfoList().get(i).getType();

            if (colType.startsWith("VARSTRING")) {
                int length = Integer.parseInt(colType.substring(10, colType.length() - 1));
                recordSize += Integer.BYTES + length * Character.BYTES;
            } else if (colType.toUpperCase().startsWith("STRING")) {
                if (colType.contains("(") && colType.contains(")")) {
                    int length = Integer.parseInt(colType.substring(colType.indexOf("(") + 1, colType.indexOf(")")));
                    recordSize += length * Character.BYTES;
                } else {
                    recordSize += recvalues.get(i).length() * Character.BYTES + Character.BYTES;
                }
            } else {
                switch (colType) {
                    case "INT":
                        recordSize += Integer.BYTES;
                        break;
                    case "FLOAT":
                        recordSize += Float.BYTES;
                        break;
                    default:
                        throw new IllegalArgumentException("Type de colonne inconnu : " + colType);
                }
            }
        }

        return recordSize;
    }

    /**
     * Écrit les données de l'enregistrement dans un tampon de bytes à partir de la
     * position spécifiée.
     *
     * @param buff Le tampon de bytes dans lequel écrire les données.
     * @param pos  La position de départ dans le tampon.
     * @return Le nombre d'octets écrits dans le tampon.
     */
    public int writeToBuffer(byte[] buff, int pos) {
        int offset = pos;

        for (String value : recvalues) {

            String type = tabInfo.getColInfoList().get(recvalues.indexOf(value)).getType();
            byte[] bytesToWrite;

            if (type.startsWith("VARSTRING")) {

                int length = Integer.parseInt(type.substring(10, type.length() - 1));
                bytesToWrite = value.getBytes();
                System.arraycopy(bytesToWrite, 0, buff, offset, bytesToWrite.length);
                offset += length;

            } else if (type.equals("INT")) {

                int intValue = Integer.parseInt(value);
                bytesToWrite = ByteBuffer.allocate(Integer.BYTES).putInt(intValue).array();
                System.arraycopy(bytesToWrite, 0, buff, offset, bytesToWrite.length);
                offset += Integer.BYTES;

            } else if (type.equals("FLOAT")) {

                float floatValue = Float.parseFloat(value);
                bytesToWrite = ByteBuffer.allocate(Float.BYTES).putFloat(floatValue).array();
                System.arraycopy(bytesToWrite, 0, buff, offset, bytesToWrite.length);
                offset += Float.BYTES;
            }
        }

        return offset - pos;
    }

    /**
     * Lit les données de l'enregistrement à partir d'un tampon de bytes à partir de
     * la position spécifiée.
     *
     * @param buff Le tampon de bytes à partir duquel lire les données.
     * @param pos  La position de départ dans le tampon.
     * @return Le nombre d'octets lus à partir du tampon.
     */
    public int readFromBuffer(byte[] buff, int pos) {
        int offset = pos;
        recvalues.clear();

        for (ColInfo colInfo : tabInfo.getColInfoList()) {
            String colType = colInfo.getType();
            byte[] bytesToRead;

            if (colType.startsWith("VARSTRING")) {

                int length = Integer.parseInt(colType.substring(10, colType.length() - 1));
                bytesToRead = new byte[length];
                System.arraycopy(buff, offset, bytesToRead, 0, length);
                recvalues.add(new String(bytesToRead));
                offset += length;

            } else if (colType.equals("INT")) {

                bytesToRead = new byte[Integer.BYTES];
                System.arraycopy(buff, offset, bytesToRead, 0, Integer.BYTES);
                recvalues.add(String.valueOf(ByteBuffer.wrap(bytesToRead).getInt()));
                offset += Integer.BYTES;

            } else if (colType.equals("FLOAT")) {

                bytesToRead = new byte[Float.BYTES];
                System.arraycopy(buff, offset, bytesToRead, 0, Float.BYTES);
                recvalues.add(String.valueOf(ByteBuffer.wrap(bytesToRead).getFloat()));
                offset += Float.BYTES;
            }
        }

        return offset - pos;
    }

    /**
     * Obtient les informations sur la table à laquelle cet enregistrement est
     * associé.
     *
     * @return Les informations sur la table.
     */
    public TableInfo getTabInfo() {
        return tabInfo;
    }

    /**
     * Définit les informations sur la table à laquelle cet enregistrement est
     * associé.
     *
     * @param tabInfo Les nouvelles informations sur la table.
     */
    public void setTabInfo(TableInfo tabInfo) {
        this.tabInfo = tabInfo;
    }

    /**
     * Obtient les valeurs de l'enregistrement.
     *
     * @return Les valeurs de l'enregistrement.
     */
    public ArrayList<String> getRecvalues() {
        return recvalues;
    }

    /**
     * Définit les valeurs de l'enregistrement.
     *
     * @param recvalues Les nouvelles valeurs de l'enregistrement.
     */
    public void setRecvalues(ArrayList<String> recvalues) {
        this.recvalues = recvalues;
    }

    // public void addValue(String value) {
    // this.recvalues.add(value);
    // this.size = calculateSize(); // on recalcule la taille apres chaque ajout
    // }

    /**
     * Ajoute une valeur à l'enregistrement.
     *
     * @param value La valeur à ajouter.
     */
    public void addValue(Object value) {
        this.recvalues.add(String.valueOf(value));
        this.size = calculateSize(); // on recalcule la taille apres chaque ajout
    }

    /**
     * Obtient la taille de l'enregistrement en octets.
     *
     * @return La taille de l'enregistrement.
     */
    public int getSize() {
        return size;
    }

    public void printRecordDetails() {
        // System.out.println("Details du record:");
        for (String value : recvalues) {
            System.out.println("Valeur: " + value);
        }
    }

}