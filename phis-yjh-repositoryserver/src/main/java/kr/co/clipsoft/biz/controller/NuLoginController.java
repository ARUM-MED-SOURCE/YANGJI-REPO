package kr.co.clipsoft.biz.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import kr.co.clipsoft.biz.model.NuUserDto;
import kr.co.clipsoft.biz.service.NuUserService;
import kr.co.clipsoft.repository.session.AuthenticationUtility;
import kr.co.clipsoft.repository.session.SessionInfoUtility;
import kr.co.clipsoft.repository.web.ClipHttpHeadersFactory;
import kr.co.clipsoft.repository.web.ClipResponseEntityFactory;

@Controller
@RequestMapping(value = "/biz/nu/auth", produces = "application/json; charset=UTF-8;")
public class NuLoginController {

	private static final Logger logger = LoggerFactory.getLogger(NuLoginController.class);

	/**
	 * 로그인 세션 유지 시간 (30분)
	 */
	private final int SESSION_INTERVAL = (1000 * 60) * 30;

	@Value("#{customerProperties['server.companyCode']}")
	private String companyCode;

	@Autowired
	private ClipHttpHeadersFactory clipHttpHeadersFactory;

	@Autowired
	private ClipResponseEntityFactory clipResponseEntityFactory;

	@Autowired
	private NuUserService nuUserService;

	@Autowired
	private AuthenticationUtility authenticationUtility;

	@RequestMapping(value = "/getCompanyCode", method = RequestMethod.POST)
	@ResponseBody
	public String getCompanyCode(HttpSession session) {
		JsonObject resultJson = new JsonObject();

		logger.debug("COMPANY CODE : " + companyCode);

		resultJson.addProperty("companyCode", companyCode);

		return resultJson.toString();
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public String loginNu(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam(value = "userId", required = true) String userId, @RequestParam(value = "password", required = true) String password) {

		JsonObject resultJson = new JsonObject();
		resultJson.addProperty("authentication", false);

		try {

			// TODO : NU 로그인 결과 판단
			boolean isValidAuth = nuUserService.loginNu(userId, password);

			// TODO : spring 인증 생성
			if (isValidAuth) {
				boolean isLogin = authenticationUtility.login(session, userId, password, true);
				resultJson.addProperty("authentication", isLogin);
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return resultJson.toString();
	}

	@RequestMapping(value = "/login/nu2", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> loginNu2(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam(value = "userId", required = true) String userId, @RequestParam(value = "password", required = true) String password) {

		try {

			NuUserDto resultDto = nuUserService.loginNu2(userId, password);
			if (resultDto == null) {
				throw new Exception("로그인에 실패하였습니다.");
			}

			// TODO : spring 인증 생성
			if (resultDto.isLoginResult()) {
				authenticationUtility.login(session, userId, password, true);
			}

			Gson gson = new GsonBuilder().create();
			HttpHeaders headers = clipHttpHeadersFactory.createCookieHeader(session);
			return clipResponseEntityFactory.create(gson.toJson(resultDto), headers, HttpStatus.OK);

		} catch (Exception e) {
			logger.error(e.toString());
			return clipResponseEntityFactory.createInternalServerError();
		}

	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	@ResponseBody
	public String logoutNu(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		JsonObject resultJson = new JsonObject();
		resultJson.addProperty("authentication", false);

		try {
			Cookie cookieSession = new Cookie("JSESSIONID", null);
			cookieSession.setMaxAge(0);
			response.addCookie(cookieSession);

			session.invalidate();
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return resultJson.toString();
	}

	@RequestMapping(value = "/continuation", method = RequestMethod.POST)
	@ResponseBody
	public String continuationNu(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		JsonObject resultJson = new JsonObject();

		String loginUserId = SessionInfoUtility.getLoginUserId(session);

		logger.debug("session id     : " + session.getId());
		logger.debug("session userid : " + loginUserId);
		logger.debug("session time   : " + session.getMaxInactiveInterval());

		session.setMaxInactiveInterval(SESSION_INTERVAL);
		HttpHeaders httpHeader = clipHttpHeadersFactory.createCookieHeader(session);
		response.setHeader("set-cookie", "JSESSIONID=" + session.getId());

		try {
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return resultJson.toString();
	}

}
