package viettel.com.sf.common;

import org.apache.tika.Tika;
import org.zkoss.util.media.Media;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;

public class TikaFileTypeDetector extends FileTypeDetector {
	private final Tika tika = new Tika();

	public TikaFileTypeDetector() {
		super();
	}

	@Override
	public String probeContentType(Path path) throws IOException {
		return tika.detect(path.toFile());
	}

	public String probeContentType(Media media) {
		try {
			return tika.detect(media.getByteData());
		} catch (Exception ex) {
			return null;
		}
	}
}
