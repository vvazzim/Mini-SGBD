package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

public class TestRecord {
	private static DataBaseInfo databaseInfo;
	private static TableInfo tableInfo;

	public static void main(String[] args) {
		initDatabase();
		createTable();
		finishDatabase();
		initDatabase();
		testRecord();
		finishDatabase();
	}

	private static void initDatabase() {
		databaseInfo = DataBaseInfo.getInstance();
		databaseInfo.init();
	}

	private static void createTable() {
		tableInfo = new TableInfo("MaTable", 2, new PageId(0, 0)); // Assuming PageId(0, 0) as an example
		ColInfo col1 = new ColInfo("Colonne1", "INT");
		ColInfo col2 = new ColInfo("Colonne2", "VARSTRING(10)");
		tableInfo.getColInfoList().add(col1);
		tableInfo.getColInfoList().add(col2);
	}

	private static void finishDatabase() {
		databaseInfo.finish();
	}

	private static void testRecord() {
		Record record = new Record(tableInfo);
		record.addValue("42");
		record.addValue("Hello");

		byte[] buffer = new byte[1024];

		int bytesWritten = record.writeToBuffer(buffer, 0);

		Record readRecord = new Record(tableInfo);
		int bytesRead = readRecord.readFromBuffer(buffer, 0);

		System.out.println("Ecriture: " + bytesWritten);
		System.out.println("Lecture: " + bytesRead);
		System.out.println("Record values: " + readRecord.getRecvalues());
	}
}