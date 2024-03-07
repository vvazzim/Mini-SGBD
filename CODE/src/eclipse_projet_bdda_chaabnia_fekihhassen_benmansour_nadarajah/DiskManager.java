package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Cette classe gère la gestion de l'allocation et de la désallocation des pages
 * sur le disque.
 */
public class DiskManager {
	private static final int pageSize = 4096;
	private static DiskManager instance = new DiskManager();
	private int[] fileSize;
	private ArrayList<PageId> deallocatedPages;
	private HashMap<PageId, ByteBuffer> pageContents;

	private DiskManager() {
		fileSize = new int[10];
		deallocatedPages = new ArrayList<>();
		pageContents = new HashMap<>();

		try {
			for (int i = 0; i < DBParams.DMFileCount; i++) {
				String fileName = DBParams.DBPath + "f" + i + ".data";
				// Creation fichier
				FileOutputStream fileOutputStream = new FileOutputStream(fileName);
				fileOutputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retourne l'instance unique du DiskManager.
	 *
	 * @return L'instance du DiskManager.
	 */
	public static DiskManager getInstance() {
		return instance;
	}

	/**
	 * Alloue une nouvelle page sur le disque.
	 *
	 * @return L'identifiant de la page allouée.
	 */
	public PageId allocatePage() {
		PageId pageId = null;

		if (!deallocatedPages.isEmpty()) {
			pageId = deallocatedPages.remove(0); // réutiliser une page désaoullée
		} else {
			int fileNumber = getMinFile();
			int pageNumber = fileSize[fileNumber] / pageSize;

			// check la limite d'espace fichier
			if (fileSize[fileNumber] + pageSize > fileSize[fileNumber + 1]) {
				fileNumber++;
				pageNumber = 0;
			}

			pageId = new PageId(fileNumber, pageNumber);
			fileSize[fileNumber] += pageSize; // incrémente page count
		}

		ByteBuffer page = ByteBuffer.allocate(pageSize);
		pageContents.put(pageId, page);
		// System.out.println("Page allouée avec id: " + pageId);
		return pageId;
	}

	/**
	 * Lit une page à partir du disque.
	 *
	 * @param pageId L'identifiant de la page à lire.
	 * @return Le contenu de la page sous forme de ByteBuffer.
	 */
	public ByteBuffer readPage(PageId pageId) {
		ByteBuffer page = pageContents.get(pageId);
		if (page == null) {
			// si la page n'est pas présente, alloue un nouveau ByteBuffer et mets dans map
			page = ByteBuffer.allocate(pageSize);
			pageContents.put(pageId, page);
			// System.out.println("Page avec id " + pageId + " introuvable. Une nouvelle
			// page a été attribuée.");
		}

		int copyLength = Math.min(page.remaining(), pageSize);
		ByteBuffer resultBuffer = ByteBuffer.allocate(copyLength);
		// met la position à 0 avant de mettre les données
		page.position(0);
		page.limit(copyLength);
		resultBuffer.put(page);
		resultBuffer.flip();
		// System.out.println("Lecture de la page avec id: " + pageId);
		return resultBuffer;
	}

	/**
	 * Écrit une page sur le disque.
	 *
	 * @param pageId L'identifiant de la page à écrire.
	 * @param buff   Le contenu de la page sous forme de ByteBuffer.
	 */
	public void writePage(PageId pageId, ByteBuffer buff) {
		ByteBuffer page = pageContents.get(pageId);
		if (page == null) {
			// gère le cas ou la page n'est pas présente
			// System.out.println("Page avec id " + pageId + " introuvable.");
			return;
		}

		int copyLength = Math.min(buff.remaining(), pageSize);
		// met la position à 0 avant de mettre les données
		buff.position(0);
		buff.limit(copyLength);
		page.put(buff);
		page.flip(); // Flip page buffer
		// System.out.println("A écrit sur la page avec id: " + pageId);
	}

	/**
	 * Désalloue une page du disque.
	 *
	 * @param pageId L'identifiant de la page à désallouer.
	 */
	public void deallocatePage(PageId pageId) {
		if (pageContents.containsKey(pageId)) {
			deallocatedPages.add(pageId); // Adding deallocated page
			pageContents.remove(pageId);
			// System.out.println("Page désallouée avec l'id: " + pageId);

			ByteBuffer clearedBuffer = ByteBuffer.allocate(pageSize);
			pageContents.put(pageId, clearedBuffer);
		} else {
			System.err.println("Page avec id " + pageId + " introuvable pour la désallocation!");
		}

	}

	/**
	 * Retourne le nombre actuel de pages allouées.
	 *
	 * @return Le nombre de pages allouées.
	 */
	public int getCurrentAllocatedPageCount() {
		return pageContents.size();
	}

	/**
	 * Retourne le numéro du fichier avec la plus petite taille parmi les fichiers
	 * disponibles.
	 *
	 * @return Le numéro du fichier avec la plus petite taille.
	 */
	private int getMinFile() {
		int minFileSize = Integer.MAX_VALUE;
		int fileNumber = 0;

		for (int i = 0; i < fileSize.length; i++) {
			if (fileSize[i] < minFileSize) {
				minFileSize = fileSize[i];
				fileNumber = i;
			}
		}

		return fileNumber;
	}

	/**
	 * Réinitialise le DiskManager en effaçant tous les fichiers et les données.
	 */
	public void reset() {
		Arrays.fill(fileSize, 0);
		deallocatedPages.clear();
		pageContents.clear();

		try {
			for (int i = 0; i < DBParams.DMFileCount; i++) {
				String fileName = DBParams.DBPath + "f" + i + ".data";
				File file = new File(fileName);

				if (file.exists()) {
					file.delete();
				}

				FileOutputStream fileOutputStream = new FileOutputStream(fileName);
				fileOutputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}