package kr.co.clipsoft.util.biz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;

/**
 * 로컬 폰트 적용 클래스
 * 
 * @author Pakbg
 *
 */
public class FontManager {

	private static FontManager instance = null;

	/**
	 * 단말기 루트경로
	 */
	private final String DEVICE_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

	/**
	 * 단말기 폰트파일 디렉토리 경로
	 */
	private String deviceFontDirPath = null;

	/**
	 * 외부폰트 파일명, 폰트명 관리변수
	 */
	private Map<String, Set<String>> externalFontInformation = null;

	private FontManager() {
		this.externalFontInformation = new HashMap<String, Set<String>>();
	}

	public static FontManager getInstance() {
		if (instance == null) {
			instance = new FontManager();
		}
		return instance;
	}

	/**
	 * 기존 폰트 디렉토리 제거 및 폰트파일 적제
	 * 
	 * @param context
	 *            Activity context
	 * @param localCopyDirPath
	 *            단말기 폰트파일 적제 경로(단말기 루트경로 제외)
	 * @param assetsDirName
	 *            assets 폰트파일 폴더명
	 * @throws IOException
	 */
	public void initFontFile(Context context, String localCopyDirPath, String assetsDirName) throws IOException {

		deviceFontDirPath = DEVICE_ROOT_PATH + File.separator + localCopyDirPath;

		clearLocalDir(deviceFontDirPath);

		File localFontDir = new File(deviceFontDirPath);
		localFontDir.mkdirs();

		copyAssetsFiles(context, assetsDirName, localFontDir);
	}

	/**
	 * RunOption에 추가할 폰트정보 등록
	 * 
	 * @param fontFileName
	 *            폰트파일 명(적제된 파일명과 동일하게 적용)
	 * @param fontName
	 *            적용대상 폰트명(디자이너에 적용된 폰트명)
	 */
	public void addExtFontName(String fontFileName, String fontName) {

		if (!externalFontInformation.containsKey(fontFileName)) {
			new FileNotFoundException("Not init Font File");
		}

		File fontFile = new File(deviceFontDirPath + File.separator + fontFileName);
		if (!(fontFile.exists() && fontFile.isFile())) {
			new FileNotFoundException("Not Found Font File");
		}

		Set<String> fontNames = externalFontInformation.get(fontFileName);
		fontNames.add(fontName);

	}

	/**
	 * RunOption(setExternalFontInformation) 적용할 폰트정보 가져오기
	 * 
	 * @return 등록한 폰트정보
	 * @throws JSONException
	 */
	public String getExternalFontInformation() throws JSONException {

		JSONArray jsonArrayExternalFontInformation = new JSONArray();

		for (Entry<String, Set<String>> item : externalFontInformation.entrySet()) {

			String fontFilePath = deviceFontDirPath + File.separator + item.getKey();
			Set<String> fontNames = item.getValue();
			for (String fontName : fontNames) {
				JSONObject jsonExternalFontInformation = new JSONObject();
				jsonExternalFontInformation.put("font-path", fontFilePath);
				jsonExternalFontInformation.put("font-name", fontName);
				jsonArrayExternalFontInformation.put(jsonExternalFontInformation);
			}
		}

		return jsonArrayExternalFontInformation.toString();
	}

	/**
	 * 대상 디렉토리 전체 삭제(디렉토리포함)
	 * 
	 * @param path
	 *            대상 디렉토리 경로
	 */
	private void clearLocalDir(String path) {

		File dir = new File(path);
		File[] childFileList = dir.listFiles();

		if (dir.exists()) {
			for (File childFile : childFileList) {
				if (childFile.isDirectory()) {
					//하위 디렉토리
					clearLocalDir(childFile.getPath());
				} else {
					//하위 파일
					childFile.delete();
				}
			}
			dir.delete();
		}
	}

	/**
	 * 대상 디렉토리 하위 파일 전체 복사
	 * 
	 * @param context
	 *            context
	 * @param assetsDirName
	 *            assets 하위 디렉토리 명
	 * @param localFontDir
	 *            복제할 단말기 경로
	 * @throws IOException
	 */
	private void copyAssetsFiles(Context context, String assetsDirName, File localFontDir) throws IOException {
		String[] files = context.getAssets().list(assetsDirName);
		for (String name : files) {
			copyAsstesToLocalStorage(context, assetsDirName + File.separator + name, localFontDir.getPath(), name);
		}
	}

	/**
	 * Asstes 폴더 하위 파일 단말기 내장메모리에 적제
	 * 
	 * @param context
	 *            context
	 * @param copyFilePath
	 *            복사대상 파일 경로
	 * @param pastePath
	 *            복사 경로
	 * @param pasteFileName
	 *            복사파일명
	 */
	private void copyAsstesToLocalStorage(Context context, String copyFilePath, String pastePath, String pasteFileName) {

		File outFile = new File(pastePath + File.separator + pasteFileName);
		if (outFile.exists()) {
			return;
		}

		InputStream in = null;
		FileOutputStream out = null;

		try {

			in = context.getAssets().open(copyFilePath);
			out = new FileOutputStream(outFile);
			copyFile(in, out);

			externalFontInformation.put(outFile.getName(), new HashSet<String>());

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// NOOP
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// NOOP
				}
			}
		}
	}

	/**
	 * 파일 복사
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

}
