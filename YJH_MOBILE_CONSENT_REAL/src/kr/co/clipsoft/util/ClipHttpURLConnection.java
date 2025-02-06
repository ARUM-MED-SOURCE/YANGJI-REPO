package kr.co.clipsoft.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ClipHttpURLConnection {

	private static String TAG_NAME = "HTTP";
	private long startServiceCallTime;

	public String request(JSONObject commonParams, URL url) {
		String requestParams = "";
		String respone = "";

		System.out.println("commonParams :: " + commonParams.toString());

		HttpURLConnection con = null;
		startServiceCallTime = System.currentTimeMillis();
		Log.i(TAG_NAME, "=========== Request Info ==================");
		Log.i(TAG_NAME, "[ SEND REQUEST ]");
		try {
			Log.i(TAG_NAME, "EFormServer : HospitalServer , url: " + url);

			JSONObject reqParam = new JSONObject();
			reqParam.put("parameter", commonParams.toString());

			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true); // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
			con.setDoInput(true); // InputStream으로 서버로 부터 응답을 받겠다는 옵션.

			con.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
			con.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
			con.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Content-type", "application/json");
			con.setConnectTimeout(1000 * 10);

			Map<String, Object> params = new LinkedHashMap<>(); // 파라미터 세팅

			Log.i(TAG_NAME, "CLIPHTTPURLCONNECTION 진입했습니다 ");
			Log.i(TAG_NAME, "****************************************");

			String checkUrl = url.toString();
			StringBuilder postData = new StringBuilder();
			if (checkUrl.indexOf(".live?") > -1) {
				org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();
				Map<String, Object> map = new HashMap<String, Object>();
				map = mapper.readValue(commonParams.toString(), new TypeReference<Map<String, String>>() {
				});

				for (Map.Entry<String, Object> param : map.entrySet()) {
					if (postData.length() != 0)
						postData.append('&');
					postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
					postData.append('=');
					postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
				}
			} else {
				params.put("parameter", commonParams.toString());

				for (Map.Entry<String, Object> param : params.entrySet()) {
					if (postData.length() != 0)
						postData.append('&');

					postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
					postData.append('=');
					postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
					// postData.append(param.getValue());
				}
			}
			System.out.println("postData ::: 2"+postData.toString());
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes); // POST 호출

			System.out.println("응답코드 ::: " + conn.getResponseCode());

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuilder result = new StringBuilder();
			String inputLine;

			while ((inputLine = in.readLine()) != null) { // response 출력
				System.out.println("호출결과 ::: " + inputLine);
				result.append(inputLine);
			}

			in.close();
			Log.e(TAG_NAME, "respone  : " + result);
			respone = result.toString();

		} catch (Exception e) {
			e.printStackTrace();
			respone = e.toString();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}

		return respone;
	}

	public String request(JSONObject commonParam, JSONObject jsonParams) {
		// TODO Auto-generated method stub
		return null;
	}

	public String nonpay(JSONArray commonParams, URL url) throws JSONException {
		String requestParams = "";
		String respone = "";

		System.out.println("commonParams :: " + commonParams.toString());

		HttpURLConnection con = null;
		startServiceCallTime = System.currentTimeMillis();
		Log.i(TAG_NAME, "=========== Request Info ==================");
		Log.i(TAG_NAME, "[ SEND REQUEST ]");
		try {
			Log.i(TAG_NAME, "EFormServer : HospitalServer , url: " + url);

			JSONObject reqParam = new JSONObject();
			reqParam.put("parameter", commonParams.toString());

			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true); // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
			con.setDoInput(true); // InputStream으로 서버로 부터 응답을 받겠다는 옵션.

			con.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
			con.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
			con.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Content-type", "application/json");
			con.setConnectTimeout(1000 * 10);  

			Log.i(TAG_NAME, "CLIPHTTPURLCONNECTION 진입했습니다 ");
			Log.i(TAG_NAME, "****************************************");
 
			StringBuilder postData = new StringBuilder();
			org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();
			Map<String, Object> map = new HashMap<String, Object>();
			map = mapper.readValue(commonParams.toString(), new TypeReference<Map<String, String>>() {
			});

			for (Map.Entry<String, Object> param : map.entrySet()) {
				if (postData.length() != 0)
					postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes); // POST 호출

			System.out.println("응답코드 ::: " + conn.getResponseCode());

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuilder result = new StringBuilder();
			String inputLine;

			while ((inputLine = in.readLine()) != null) { // response 출력
				System.out.println("호출결과 ::: " + inputLine);
				result.append(inputLine);
			}

			in.close();
			Log.e(TAG_NAME, "respone  : " + result);
			respone = result.toString();

		} catch (Exception e) {
			e.printStackTrace();
			respone = e.toString();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}

		return respone;
	}

}
