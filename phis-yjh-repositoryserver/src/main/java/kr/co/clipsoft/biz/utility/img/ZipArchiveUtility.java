package kr.co.clipsoft.biz.utility.img;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일(이미지) 압축 기능담당
 *
 *
 */
@Component
public class ZipArchiveUtility {

	private static final Logger logger = LoggerFactory.getLogger(ZipArchiveUtility.class);

	public byte[] createZip(List<MultipartFile> imageFiles) throws IOException {

		Map<String, byte[]> mapFiles = new HashMap<String, byte[]>();

		for (MultipartFile mImageFile : imageFiles) {
			String imageFileName = mImageFile.getOriginalFilename();
			mapFiles.put(imageFileName, mImageFile.getBytes());
		}

		byte[] imageZipData = createZip(mapFiles);
		return imageZipData;
	}

	public byte[] createZip(Map<String, byte[]> mapReporte) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		for (Entry<String, byte[]> reporte : mapReporte.entrySet()) {
			ZipEntry entry = new ZipEntry(reporte.getKey());
			entry.setSize(reporte.getValue().length);
			zos.putNextEntry(entry);
			zos.write(reporte.getValue());
		}
		zos.closeEntry();
		zos.close();
		return baos.toByteArray();

	}

}
