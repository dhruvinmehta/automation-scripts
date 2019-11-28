package helper;

import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@Slf4j
public class DownloadThread implements Runnable {
	private String url;
	private String location;
	private String fileName;

	public DownloadThread(String url, String location, String fileName) {
		this.url = url;
		this.location = location;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		try {
			URL url = new URL(this.url);
			ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
			FileOutputStream fos = new FileOutputStream(this.location + this.fileName);
			fos.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
			fos.close();
			readableByteChannel.close();
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
