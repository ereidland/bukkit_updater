package net.evtr.bupdater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class Download {
	private String address, output;
	private boolean bDone, bError;
	private String message;
	Thread downloadThread;
	
	public String getAddress() {
		return address;
	}
	public String getOutAddress() {
		return output;
	}
	 
	public String getMessage() {
		return message;
	}
	public boolean isDone() {
		return bDone;
	}
	public boolean hasError() {
		return bError;
	}
	
	public abstract void onProgress(String itemName);
	public abstract void onFinish();
	
	private class DownloadRunnable implements Runnable {
		public void run() {
			try {
				int BUFFER = 2048;
				BufferedInputStream fis = new BufferedInputStream(new URL(address).openStream());
				
				new File(output).mkdir();

				BufferedOutputStream dest = null;
				ZipInputStream zis = new ZipInputStream(fis);
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					onProgress(entry.getName() + " (" + (entry.getSize() == -1 ? "???" : entry.getSize()) + " bytes)");
					int count;
					byte data[] = new byte[BUFFER];
					
					new File(output + "/" + entry.getName().substring(0, entry.getName().lastIndexOf('/'))).mkdir();
					
					FileOutputStream fos = new FileOutputStream(output + "/" + entry.getName());
					dest = new BufferedOutputStream(fos, BUFFER);
					while ((count = zis.read(data, 0, BUFFER)) != -1 ) {
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
				}
				zis.close();
			} catch ( Exception e ) {
				e.printStackTrace();
				message = "Error: " + e.getMessage();
				bError = true;
			}
			downloadThread = null;
			if ( !bError ) {
				message = "Done!";
			}
			bDone = true;
			onFinish();
		}
	}
	public void Begin() {
		if ( downloadThread == null ) {
			downloadThread = new Thread(new DownloadRunnable());
			downloadThread.start();
		}
	}
	public Download(String address, String output) {
		this.address = address;
		this.output = output;
		bDone = bError = false;
		message = "";
		downloadThread = null;
	}
}
