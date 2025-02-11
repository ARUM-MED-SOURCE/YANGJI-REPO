package kr.co.clipsoft.biz.db;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class BizNuRoutingDataSource extends AbstractRoutingDataSource {

	private Pattern bizNuPattern = Pattern.compile(".*\\/biz\\/nu\\/member\\/viewer/.*");
	
	@Override
	protected Object determineCurrentLookupKey() {
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();
			
			String url = request.getRequestURL().toString(); 
			
			Matcher bizNuMatcher = bizNuPattern.matcher(url);
			
			if (bizNuMatcher.find()) {
				return "bizNuDataSource";
			} 
			
			return "mainDataSource";
		} catch (Exception e) {
			return "mainDataSource";
		}
	}
}
