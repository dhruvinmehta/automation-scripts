import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class DownloadThread implements Runnable {
	private String fileUrl;
	private String downloadLocation;
	private int number;

	public DownloadThread(String fileUrl, String downloadLocation, int number) {
		this.fileUrl = fileUrl;
		this.downloadLocation = downloadLocation;
		this.number = number;
	}

	@Override
	public void run() {
		try {
			URL url = new URL(this.fileUrl);
			ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
			FileOutputStream fos = new FileOutputStream(this.downloadLocation + "Podcast - " + number + ".mp3");
			fos.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
			fos.close();
			readableByteChannel.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
