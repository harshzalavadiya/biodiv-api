package biodiv.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipService {

	private static final Logger log = LoggerFactory.getLogger(ZipService.class);

	private ZipService() {
	}

	public static void zipFile(String filePath) {

		File file = new File(filePath);
		String zipFileName = file.getName().concat(".zip");

		try (
			FileOutputStream fos = new FileOutputStream(zipFileName);
			ZipOutputStream zos = new ZipOutputStream(fos)) {

			zos.putNextEntry(new ZipEntry(file.getName()));

			byte[] bytes = Files.readAllBytes(Paths.get(filePath));
			zos.write(bytes, 0, bytes.length);

		} catch (FileNotFoundException ex) {
			log.error("The file {} does not exist", filePath);
		} catch (IOException ex) {
			log.error("I/O error: {}", ex);
		}
	}

	public static void zipFiles(String... filePaths) {

		File firstFile = new File(filePaths[0]);
		String zipFileName = firstFile.getName().concat(".zip");

		try (
			FileOutputStream fos = new FileOutputStream(zipFileName);
			ZipOutputStream zos = new ZipOutputStream(fos)) {

			for (String aFile : filePaths) {
				zos.putNextEntry(new ZipEntry(new File(aFile).getName()));

				byte[] bytes = Files.readAllBytes(Paths.get(aFile));
				zos.write(bytes, 0, bytes.length);
			}

		} catch (FileNotFoundException ex) {
			log.error("A file does not exist: {}", ex);
		} catch (IOException ex) {
			log.error("I/O error: {}", ex);
		}
	}
}
