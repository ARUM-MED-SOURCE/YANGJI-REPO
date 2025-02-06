package kr.co.clipsoft.biz.model;



import java.io.UnsupportedEncodingException;



public class NuTempDataDto {

	private Long dataRid;	
	public Long getDataRid()
	{
		return this.dataRid;
	}

	public void setDataRid(Long dataRid)
	{
		this.dataRid = dataRid;
	}	

	private Long dataIdx;
	public Long getDataIdx()
	{
		return this.dataIdx;
	}

	public void setDataIdx(Long dataIdx)
	{
		this.dataIdx = dataIdx;
	}

	
	private Long formRid;
	public Long getFormRid()
	{
		return this.formRid;
	}

	public void setFormRid(Long formRid)
	{
		this.formRid = formRid;
	}
	
	private String dataXml;
	public String getDataXml()
	{
		return this.dataXml;
	}

	public void setDataXml(String dataXml)
	{
		this.dataXml = dataXml;
	}	

	public byte[] getFormDataByteValue() {
		byte[] result = null;
		try {
			result = dataXml.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return result;
	}	
	public void setFormDataByteValue(byte[] formData) {
		try {
			this.dataXml = new String(formData, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.dataXml = "";
		}
	}
	private String completeYN;
	public String getCompleteYN()
	{
		return this.completeYN;
	}

	public void setCompleteYN(String completeYN)
	{
		this.completeYN = completeYN;
	}	

	private String lastYN;
	public String getLastYN()
	{
		return this.lastYN;
	}

	public void setLastYN(String lastYN)
	{
		this.lastYN = lastYN;
	}	

	private String createDateTime;
	public String getCreateDateTime()
	{
		return this.createDateTime;
	}

	public void setCreateDateTime(String createDateTime)
	{
		this.createDateTime = createDateTime;
	}	

	private String ocrTag;
	public String getOcrTag()
	{
		return this.ocrTag;
	}

	public void setOcrTag(String ocrTag)
	{
		this.ocrTag = ocrTag;
	}

	private String formId;
	public String getFormId()
	{
		return this.formId;
	}

	public void setFormId(String formId)
	{
		this.formId = formId;
	}

	private String formCode;
	public String getFormCode()
	{
		return this.formCode;
	}

	public void setFormCode(String formCode)
	{
		this.formCode = formCode;
	}

	private String formVersion;
	public String getFormVersion()
	{
		return this.formVersion;
	}

	public void setFormVersion(String formVersion)
	{
		this.formVersion = formVersion;
	}
	
	private String encoding;
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	private byte[] formDataByteArray;
	public byte[] getFormDataByteArray() {
		return formDataByteArray;
	}

	public void setFormDataByteArray(byte[] formDataByteArray) {
		this.formDataByteArray = formDataByteArray;
	}
	
	
}

