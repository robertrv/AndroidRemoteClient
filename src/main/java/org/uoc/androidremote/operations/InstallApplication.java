package org.uoc.androidremote.operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Operation used to install a new application inside the server.
 * 
 * This is a simple transport bean.
 * 
 * @author robertrv [at] gmail
 *
 */
public class InstallApplication extends Operation implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String fileName;
	private byte[] fileContent;
	
	/**
	 * Creates a new InstallApplication where we specify the file 
	 * corresponding to an APK file, which we want to install.
	 * 
	 * @param fileName
	 * @param apkFile
	 * @throws IOException 
	 */
	public InstallApplication(File apkFile) throws IOException {
		super(OP_INSTALL_APPLICATION, "Install application: "+apkFile.getName());
		this.fileName = apkFile.getName();
		if (!apkFile.exists() || !apkFile.getName().endsWith("apk")) {
			throw new RuntimeException("Should be a apk file !");
		}
		FileInputStream ins = new FileInputStream(apkFile);
		int size = ins.available();

		fileContent = new byte[size];
		ins.read(fileContent);
		ins.close();
	}
	
	public byte[] getFile() {
		return fileContent;
	}
	
	public String getFileName() {
		return fileName;
	}
}
