package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.nio.ByteBuffer;

public class TestDiskmanager {

    public static void main(String[] args) {
        DiskManager disk = DiskManager.getInstance();
        testLectureEcriturePage(disk);
        testAllocationDesallocationPage(disk);
        // testRechercheFichier();
        testTailleDePage();
    }

    private static void testLectureEcriturePage(DiskManager disk) {
        System.out.println("TestLectureEcriturePage:");
        DBParams.SGBDPageSize = 5;
        System.out.println("Taille de page: " + DBParams.SGBDPageSize);

        DiskManager diskManager = disk;

        // Allocation de page
        PageId pageId = diskManager.allocatePage();
        System.out.println("Page Alloué : " + pageId);

        // Ecriture de page
        ByteBuffer dataToWrite = ByteBuffer.allocate(DBParams.SGBDPageSize);
        dataToWrite.put("Hello".getBytes());
        dataToWrite.flip(); // Flip the buffer to prepare it for reading
        diskManager.writePage(pageId, dataToWrite);
        dataToWrite.rewind(); // Reset position to start

        // Récupère les données à l'aide de readPage et les stocke dans dataRead
        ByteBuffer dataRead = diskManager.readPage(pageId);
        dataRead.rewind(); // Reset position to start

        // Comparaison des données
        boolean testPassed = dataToWrite.equals(dataRead);
        System.out.println("Ecriture: " + new String(dataToWrite.array()));
        System.out.println("Lecture: " + new String(dataRead.array()));

        // Verification
        if (testPassed) {
            System.out.println("Test Reussi.");
        } else {
            System.out.println("Test Echoué.");
        }

        diskManager.deallocatePage(pageId);
        // Reinitialisation page de defaut
        DBParams.SGBDPageSize = 4096;
        System.out.println("Taille de page: " + DBParams.SGBDPageSize);

    }

    private static void testAllocationDesallocationPage(DiskManager disk) {
        System.out.println("\nTestAllocationDeallocationPage:");
        DBParams.SGBDPageSize = 4;
        DiskManager diskManager = disk;

        // Allocation de pages
        PageId pageId1 = diskManager.allocatePage();
        PageId pageId2 = diskManager.allocatePage();
        System.out.println("Allocation de pages: " + pageId1 + ", " + pageId2);

        // desallocation de page
        diskManager.deallocatePage(pageId1);

        // Allocate de page
        PageId pageId3 = diskManager.allocatePage();
        System.out.println("Allocation de page: " + pageId3);

        // Verification nombre de pages allouées
        int countAllocatedPages = diskManager.getCurrentAllocatedPageCount();
        System.out.println("Nombres de pages alloués: " + countAllocatedPages);

        // Verification
        if (countAllocatedPages == 2) {
            System.out.println("Test Reussi.");
        } else {
            System.out.println("Test Echoué.");
        }

        // Reinitialisation de defaut de page
        DBParams.SGBDPageSize = 4096;
    }

    private static void testTailleDePage() {
        System.out.println("TestTailleDePage:");
        int expectedPageSize = 5;
        DBParams.SGBDPageSize = expectedPageSize;
        System.out.println("Taille de page: " + DBParams.SGBDPageSize);

        if (DBParams.SGBDPageSize == expectedPageSize) {
            System.out.println("Test Reussi.");
        } else {
            System.out.println("Test Echoué.");
        }

        // Reinitialisation page de defaut
        DBParams.SGBDPageSize = 4096;
        System.out.println("Taille de page: " + DBParams.SGBDPageSize);
    }
}