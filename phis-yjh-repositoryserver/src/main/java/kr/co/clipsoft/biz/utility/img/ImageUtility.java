package kr.co.clipsoft.biz.utility.img;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import kr.co.clipsoft.biz.exception.BizErrorInfo;
import kr.co.clipsoft.biz.exception.BusinessException;
import kr.co.clipsoft.biz.model.consent.NuConsentImageDto;
import kr.co.clipsoft.biz.model.consent.NuConsentMstDto;
import kr.co.clipsoft.biz.model.consent.NuTblLinkConsentInfoDto;

@Component
public class ImageUtility {

	private static final Logger logger = LoggerFactory.getLogger(ImageUtility.class);

	/**
	 * 이미지 루트 경로
	 */
	@Value("#{customerProperties['server.nas.root.path']}")
	private String NAS_ROOT_PATH;

	/**
	 * 이미지 경로(연동하는곳에서 접두어 경로가 필요할시 추가)
	 */
	@Value("#{customerProperties['server.nas.image.path.prefix']}")
	private String NAS_IMAGE_PREFIX_PATH;

	/**
	 * 이미지 경로
	 */
	@Value("#{customerProperties['server.nas.image.path']}")
	private String NAS_IMAGE_PATH;

	public List<NuTblLinkConsentInfoDto> uploadImagesLcTech(List<MultipartFile> imageFiles, NuConsentImageDto imageDto, NuConsentMstDto mstDto)
			throws BusinessException {

		String strNowTime = getNowTime();
		ArrayList<NuTblLinkConsentInfoDto> listNuTblLinkConsentInfoDto = new ArrayList<NuTblLinkConsentInfoDto>();

		for (MultipartFile mImageFile : imageFiles) {

			String imageFileName = mImageFile.getOriginalFilename();
			String imageFileDirPath = imageDto.getImgFileSubPath() + File.separator + strNowTime;
			logger.debug("image Util Param Info imageFileName : " + imageFileName + ", imageFileDirPath : " + imageFileDirPath +
					     ", NAS_ROOT_PATH : " + NAS_ROOT_PATH + ", NAS_IMAGE_PATH : " + NAS_IMAGE_PATH);
			// 이미지 경로 생성
			File imageFileDir = createImageDirectory(NAS_ROOT_PATH + NAS_IMAGE_PATH + imageFileDirPath);

			// 이미지 파일 생성(NAS)
			File imageFile = new File(imageFileDir.getPath() + File.separator + imageFileName);

			try {
				// 경로 예시 /imgemr/dev/20250205/20059888/20250205104334339/553f2fc6-5bc5-4ed6-b507-cc3220c34fe7_0.jpg
				mImageFile.transferTo(imageFile);
			} catch (IllegalStateException e) {
				logger.error("NAS 이미지 파일 업로드에서 오류가 발생하였습니다 : " + e.toString());
				throw new BusinessException(BizErrorInfo.ERROR_IMAGE_UPLOAD_NAS);
			} catch (IOException e) {
				logger.error("NAS 이미지 파일 업로드에서 오류가 발생하였습니다 : " + e.toString());
				throw new BusinessException(BizErrorInfo.ERROR_IMAGE_UPLOAD_NAS);
			}

			NuTblLinkConsentInfoDto lcTechDto = new NuTblLinkConsentInfoDto();
			lcTechDto.setCmd("A");
			lcTechDto.setPatId(mstDto.getPatientCode());
			lcTechDto.setOrdDate(mstDto.getClnDate());
			lcTechDto.setOcrTag(mstDto.getOcrTag());
			lcTechDto.setImgPath(NAS_IMAGE_PREFIX_PATH + NAS_IMAGE_PATH + imageFileDirPath + File.separator + imageFileName);
			lcTechDto.setImgIndex(getImageIndex(imageFileName));
			lcTechDto.setSpcId(mstDto.getInstCd());
			lcTechDto.setOrdDd(mstDto.getClnDate());
			lcTechDto.setCretNo(mstDto.getCertNo());
			lcTechDto.setPatFlg(mstDto.getVisitType());
			lcTechDto.setExamTyp(mstDto.getClnDeptCd());
			lcTechDto.setUserId(mstDto.getUserId());
			lcTechDto.setExamName(mstDto.getFormName());
			lcTechDto.setFormCd(mstDto.getFormCd());

			listNuTblLinkConsentInfoDto.add(lcTechDto);

		}

		return listNuTblLinkConsentInfoDto;
	}

	private String getNowTime() {
		SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String strNowTime = dayTime.format(new Date(System.currentTimeMillis()));
		return strNowTime;
	}

	private int getImageIndex(String imageFileName) {
		String[] fileNameSplit = imageFileName.toLowerCase().replace(".jpg", "").replace(".png", "").trim().split("_");
		int imageIndex = Integer.parseInt(fileNameSplit[fileNameSplit.length - 1]) + 1;
		return imageIndex;
	}

	private File createImageDirectory(String imageFilePath) {
		File imageFileDir = new File(imageFilePath);
		logger.info("createImageDirectory execute");

		if (!(imageFileDir.exists() && imageFileDir.isDirectory())) {
			logger.info("createImageDirectory Make Directory");
			imageFileDir.mkdirs();
		}

		return imageFileDir;
	}

}
