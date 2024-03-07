package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.nio.ByteBuffer;

public class TestBufferManager {

	public static void main(String[] args) {
		testBufferManager();
		// Ajoutez d'autres tests au besoin
		// testRechercheFichier();
		// testTailleDePage();
	}

	public static void testBufferManager() {
		BufferManager bufferManager = BufferManager.getInstance();

		// Test 1: Lecture Ecriture
		System.out.println("Test 1: Lecture Ecriture");
		PageId pageId1 = new PageId(1, 1);
		ByteBuffer buffer1 = bufferManager.getPage(pageId1, ByteBuffer.allocate(1024));
		// ... Operations sur le buffer ...
		bufferManager.freePage(pageId1, 1);
		// Verification que la page est bien ecrite dans le DiskManager
		assert bufferManager.getPage(pageId1, ByteBuffer.allocate(1024)).equals(buffer1)
				: "Test 1 Echoué: La page n'est pas dans le BufferManager";
		System.out.println("Test 1 Reussi");

		// Test 2: Allocation de Page
		System.out.println("Test 2: Allocation de Page ");
		PageId pageId2 = new PageId(2, 2);
		ByteBuffer buffer2 = bufferManager.getPage(pageId2, ByteBuffer.allocate(1024));
		// Verification que le PinCount est incrementé
		bufferManager.getPage(pageId2, ByteBuffer.allocate(1024));
		// Liberer la page, decrementer PinCount avec la page toujours dans le
		// BufferManager
		bufferManager.freePage(pageId2, 0);
		assert bufferManager.getPage(pageId2, ByteBuffer.allocate(1024)).equals(buffer2) : "Test 2 Echoué: Pin ";
		// // Libère la page, PinCount est egale à 0 et la page est supprimée du
		// BufferManager
		bufferManager.freePage(pageId2, 0);
		assert !bufferManager.bufferPool.containsKey(pageId2)
				: "Test 2 Echou2: La page n'a pas etait retire du BufferManager";
		System.out.println("Test 2 Reussi");

		// Test 3: Flush All
		System.out.println("Test 3: Flush All");
		PageId pageId3 = new PageId(3, 3);
		PageId pageId4 = new PageId(4, 4);
		// ByteBuffer buffer3 = bufferManager.getPage(pageId3,
		// ByteBuffer.allocate(1024));
		// ByteBuffer buffer4 = bufferManager.getPage(pageId4,
		// ByteBuffer.allocate(1024));
		// ... Operations sur le Buffer ...
		bufferManager.freePage(pageId3, 1);
		bufferManager.freePage(pageId4, 0);
		// FlushAll et verification que seules les pages non vides sont ecrites dans le
		// DiskManager
		bufferManager.flushAll();
		assert !bufferManager.bufferPool.containsKey(pageId3) : "Test 3 Echoué: Page non reecrites dans le DiskManager";
		assert !bufferManager.bufferPool.containsKey(pageId4) : "Test 3 Echoué : Liberer la page du DiskManager";
		System.out.println("Test 3 Reussi");
	}
}