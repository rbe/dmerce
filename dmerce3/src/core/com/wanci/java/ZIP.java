/*
 * Created on 09.02.2004
 *  
 */
package com.wanci.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author rb
 * @version ${Id}
 * 
 * Easy create a ZIP file:
 * 
 * <p>
 * 
 * <pre>
 *  ZIP z = new ZIP("file.zip"); z.addFile(String, File); z.create();
 * </pre>
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * </p>
 *  
 */
public class ZIP {

	/**
	 *  
	 */
	private String zipFileName;
	/**
	 *  
	 */
	private File zipFile;
	/**
	 *  
	 */
	HashMap files = new HashMap();

	/**
	 * Constructor
	 * 
	 * @param zipFileName
	 *            Name of the resulting zip archive
	 */
	public ZIP(String zipFileName) {
		this.zipFileName = zipFileName;
	}

	/**
	 * Constructor
	 * 
	 * @param zipFile
	 */
	public ZIP(File zipFile) {
		this.zipFile = zipFile;
	}

	/**
	 * Add a file to archive
	 * 
	 * @param fileName
	 * @param file
	 */
	public void addFile(String fileName, File file) {
		files.put(fileName, file);
	}

	/**
	 * Add a file to archive
	 * 
	 * @param fileName
	 * @param fin
	 * @throws IOException
	 */
	public void addFile(String fileName, FileInputStream fin)
		throws IOException {

		files.put(fileName, fin);

	}

	/**
	 * Create the archive
	 * 
	 * @throws IOException
	 */
	public void create() throws IOException {

		ZipOutputStream zout =
			new ZipOutputStream(new FileOutputStream(new File(zipFileName)));
		FileInputStream fin;

		Iterator i = files.entrySet().iterator();
		while (i.hasNext()) {

			Map.Entry e = (Map.Entry) i.next();
			String fn = (String) e.getKey();
			Object fc = (Object) e.getValue();

			zout.putNextEntry(new ZipEntry(fn));

			if (fc instanceof FileInputStream)
				fin = (FileInputStream) fc;
			else if (fc instanceof File)
				fin = new FileInputStream(fn);
			else
				continue;

			byte[] b = new byte[512];
			int len = 0;

			while ((len = fin.read(b)) > 0)
				zout.write(b, 0, len);

			fin.close();
			zout.closeEntry();

		}

		zout.close();

	}

	/**
	 * Get a file from ZIP archive and save it into the filesystem
	 * 
	 * @param fileName
	 * @return @throws
	 *         IOException
	 */
	public File getFile(String fileName) throws IOException {

		int len = 512;
		byte[] b = new byte[len];
		int bytesRead;
		ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
		File file = new File(fileName);
		FileOutputStream fout = new FileOutputStream(file);

		while (zin.available() > 0) {

			ZipEntry ze = zin.getNextEntry();
			if (ze.getName().equals(fileName)) {
				while (true) {
					bytesRead = zin.read(b, 0, len);
					if (bytesRead == -1)
						break;
					fout.write(b, 0, bytesRead);
				}
			}

			fout.close();

		}

		return file;

	}

	/**
	 * Remove file from file list
	 * 
	 * @param fileName
	 */
	public void removeFile(String fileName) {
		files.remove(fileName);
	}

}