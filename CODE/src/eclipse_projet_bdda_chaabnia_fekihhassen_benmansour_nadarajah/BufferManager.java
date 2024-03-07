package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Cette classe gère le tampon de mémoire utilisé pour stocker les pages de
 * données en mémoire.
 */
public class BufferManager {
	private static BufferManager instance; // Instance unique du Buffer Manager
	public Map<PageId, Frame> bufferPool; // Tampon de mémoire pour stocker les pages

	/**
	 * Constructeur privé de la classe BufferManager.
	 * Initialise le tampon de mémoire sous forme de HashMap.
	 */
	private BufferManager() {
		bufferPool = new HashMap<>();
	}

	/**
	 * Obtient l'instance unique du Buffer Manager (singleton).
	 *
	 * @return L'instance unique du Buffer Manager.
	 */
	public static BufferManager getInstance() {
		if (instance == null) {
			instance = new BufferManager();
		}
		return instance;
	}

	/**
	 * Initialise le tampon de mémoire en vidant son contenu.
	 */
	public void init() {
		bufferPool.clear();
	}

	/**
	 * Obtient une page à partir de son identifiant (PageId) en utilisant un tampon
	 * de mémoire.
	 * Si la page est déjà en mémoire, elle est récupérée depuis le tampon.
	 * Sinon, elle est lue depuis le gestionnaire de disque et stockée dans le
	 * tampon.
	 *
	 * @param pageId L'identifiant de la page à obtenir.
	 * @return Le ByteBuffer contenant les données de la page.
	 */
	public ByteBuffer getPage(PageId pageId, ByteBuffer buff) {
		try {
			if (bufferPool.containsKey(pageId)) {
				Frame frame = bufferPool.get(pageId);
				frame.incrementerPinCount();
				return frame.getBuffer();
			} else {
				ByteBuffer pageData = DiskManager.getInstance().readPage(pageId);
				if (pageData == null) {
					System.err.println("Error: Null pageData rencontrée pour getPage: " + pageId);
					// Potential causes: Disk read issues, missing page, etc.

					return null;
				} else {
					// System.out.println("PageData n'est pas null pour getPage dans getPage: " +
					// pageId);
				}
				Frame newFrame = new Frame(pageData);
				bufferPool.put(pageId, newFrame);
				return newFrame.getBuffer();
			}
		} catch (

		Exception e) {
			System.err.println("Erreurr dans getPage pour PageId: " + pageId + ". Raison: " + e.getMessage());
			return null;
		}

	}

	/**
	 * Obtient une page à partir de son identifiant (PageId) en utilisant un tampon
	 * de mémoire.
	 * Si la page est déjà en mémoire elle est récupérée depuis le tampon.
	 * Sinon elle est lue depuis le gestionnaire de disque et stockée dans le
	 * tampon.
	 *
	 * @param pageId L'identifiant de la page à obtenir.
	 * @param buff   Le ByteBuffer existant dans lequel stocker les données de la
	 *               page.
	 * @return Le ByteBuffer contenant les données de la page.
	 */
	public ByteBuffer getPage(PageId pageId) {
		try {

			if (bufferPool.containsKey(pageId)) {
				Frame frame = bufferPool.get(pageId);
				frame.incrementerPinCount();
				return frame.getBuffer();
			} else {
				ByteBuffer pageData = DiskManager.getInstance().readPage(pageId);
				if (pageData == null) {
					System.err.println("Error: pageData null pour getPage: " + pageId);
				} else {
					// System.out.println("PageData n'est pas null dans getPage pour getPage " +
					// pageId);
				}
				Frame newFrame = new Frame(pageData);
				bufferPool.put(pageId, newFrame);
				return newFrame.getBuffer();
			}
		} catch (

		Exception e) {
			System.err.println("Erreurr dans getPage pour PageId: " + pageId + ". Raison: " + e.getMessage());
			return null;
		}

	}

	/**
	 * Libère une page du tampon de mémoire et la désalloue si nécessaire.
	 *
	 * @param pageId   L'identifiant de la page à libérer.
	 * @param valDirty Une valeur indiquant si la page est marquée comme "dirty" (1
	 *                 pour vrai, 0 pour faux).
	 */
	public void freePage(PageId pageId, int valDirty) {
		if (bufferPool.containsKey(pageId)) {
			Frame frame = bufferPool.get(pageId);
			frame.decrementerPinCount();
			;

			if (valDirty == 1) {
				frame.setDirty(true);
			}

			if (frame.getPinCount() == 0) {
				// Si le pinCount est egale à 0, supprime la page de la mémoire tampon
				bufferPool.remove(pageId);

				// Desallouer la page dans le DiskManager
				DiskManager.getInstance().deallocatePage(pageId);
			}
		}
	}

	/**
	 * Écrit toutes les pages dirty du tampon de mémoire dans le gestionnaire de
	 * disque et vide le tampon.
	 */
	public void flushAll() {
		for (Map.Entry<PageId, Frame> entry : bufferPool.entrySet()) {
			Frame frame = entry.getValue();
			if (frame.getDirty()) {
				// Si dirty, réécrire la page dans DiskManager
				DiskManager.getInstance().writePage(entry.getKey(), frame.getBuffer());
				frame.setDirty(false);
			}
		}

		// Vider la memoire tampon
		bufferPool.clear();
	}

	/**
	 * Réinitialise le tampon de mémoire en remettant à zéro les compteurs pincount
	 * et les états dirty
	 * et en réalloue de nouveaux ByteBuffer aux frames.
	 */
	public void reset() {
		for (Frame frame : bufferPool.values()) {
			frame.setPinCount(0);
			frame.setDirty(false);
			frame.setBuffer(ByteBuffer.allocate(DBParams.SGBDPageSize));
		}
		bufferPool.clear();
	}

}