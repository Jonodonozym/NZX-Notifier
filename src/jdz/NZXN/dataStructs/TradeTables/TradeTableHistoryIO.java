
package jdz.NZXN.dataStructs.TradeTables;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class TradeTableHistoryIO {
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-uuuu");

	public static void save(TradeTableHistory history) throws IOException {
		File saveFile = getSaveFile(history.getSecurityCode(), history.getDate());
		saveFile.delete();

		saveFile.createNewFile();

		FileWriter fw = new FileWriter(saveFile);
		fw.write(TradeTableFactory.toString(history));
		fw.close();
	}

	public static TradeTableHistory load(String securityName, LocalDate date) throws IOException {
		File saveFile = getSaveFile(securityName, date);
		if (!saveFile.exists())
			throw new IOException("Data for " + securityName + " on " + date.format(dtf) + " not found");

		Scanner s = new Scanner(saveFile);
		return TradeTableFactory.parseHistory(s);
	}

	private static File getSaveFile(String securityName, LocalDate date) {
		String rootDir = System.getenv("APPDATA") + File.separator + "NZX Notifier" + File.separator + "TradeHistory";
		String securityDir = rootDir + File.separator + securityName;
		String filePath = securityDir + File.separator + securityName + " " + date.format(dtf) + ".tth";

		File file = new File(filePath);
		return file;
	}
}
