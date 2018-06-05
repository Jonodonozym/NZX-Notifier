
package jdz.NZXN.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ExportFile {
	/**
	 * Export a resource embedded into a Jar file to the local file path.
	 * Taken from
	 * https://stackoverflow.com/questions/10308221/how-to-copy-file-inside-jar-to-outside-the-jar
	 * 
	 * @param resourceName ie.: "/SmartLibrary.dll"
	 * @return The path to the exported resource
	 * @throws Exception
	 */
	static public String ExportResource(String resourcePath, String targetPath) throws Exception {
		InputStream stream = null;
		OutputStream resStreamOut = null;
		String resourceName = resourcePath.substring(resourcePath.lastIndexOf("/") + 1);
		try {

			stream = ExportFile.class.getResourceAsStream(resourcePath);// note that each / is a directory down in the
																		// "jar tree" been the jar the root of the tree
			if (stream == null) {
				throw new Exception("Cannot get resource \"" + resourcePath + "\" from Jar file.");
			}

			int readBytes;
			byte[] buffer = new byte[4096];
			new File(targetPath).mkdir();
			File file = new File(targetPath + File.separator + resourceName);
			file.createNewFile();
			resStreamOut = new FileOutputStream(file);
			while ((readBytes = stream.read(buffer)) > 0) {
				resStreamOut.write(buffer, 0, readBytes);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(resourcePath + ", " + targetPath);
			throw ex;
		}
		finally {
			if (stream != null)
				stream.close();
			if (resStreamOut != null)
				resStreamOut.close();
		}

		return targetPath + File.separator + resourceName;
	}
}
