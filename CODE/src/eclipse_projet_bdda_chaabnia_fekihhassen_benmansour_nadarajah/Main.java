package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IOException, PageNotFoundException {

		DBParams.DBPath = "BD\\"; // DBParams.DBPath = args[0];

		DBParams.SGBDPageSize = 4096;
		DBParams.DMFileCount = 4;
		DBParams.FrameCount = 2;

		// Call the init method
		DataBaseManager dbManager = DataBaseManager.getInstance();
		dbManager.init();

		Scanner scanner = new Scanner(System.in);
		String command;
		String upperCaseCommand;
		String[] commands = { "HELP", "EXIT", "CREATE RELATION <Relation> (c1:type,c2:type,...)", "RESETDB",
				"INSERT INTO <Relation> VALUES (val1,val2)",
				"SELECT * FROM <RELATION>", };

		do {
			System.out.println("Bonjour, Veuillez saisir une commande ?\n(HELP pour l'aide et EXIT pour quitter)\n");
			command = scanner.nextLine();

			upperCaseCommand = command.toUpperCase();
			switch (upperCaseCommand) {
				case "HELP":
					System.out.println("Commandes disponibles : " + Arrays.toString(commands));
					break;
				case "EXIT":
					System.out.println("Au revoir!\n");
					dbManager.finish();
					break;
				default:
					dbManager.processCommand(command);
					break;
			}
		} while (!upperCaseCommand.equals("EXIT"));

		scanner.close();
	}
}