/**
 * A HTTP plugin for Cordova / Phonegap
 */
package com.synconset;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Map;

import javax.net.ssl.SSLHandshakeException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;
import org.apache.cordova.file.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class CordovaHttpDownload extends CordovaHttp implements Runnable {
	
	public static final String TAG = "CordovaHttpDownload";
	
    private String filePath;
    
    public CordovaHttpDownload(String urlString, Map<?, ?> params, Map<String, String> headers, CallbackContext callbackContext, String filePath) {    	  	
        super(urlString, params, headers, callbackContext);
        this.filePath = filePath;
        LOG.i(TAG, "urlString : " + urlString);
        LOG.i(TAG, "params : " + params);
        LOG.i(TAG, "headers : " + headers);
    	LOG.i(TAG, "filePath : " + filePath);
    }
    
    @Override
    public void run() {
        try {
            HttpRequest request = HttpRequest.get(this.getUrlString(), this.getParams(), true);
            this.setupSecurity(request);
            request.acceptCharset(CHARSET);
            request.headers(this.getHeaders());
            int code = request.code();
            
            JSONObject response = new JSONObject();
            this.addResponseHeaders(request, response);
            response.put("status", code);
            LOG.i(TAG, "code : " + code);
            if (code >= 200 && code < 300) {
                URI uri = new URI(filePath);
                File file = new File(uri);
                LOG.i(TAG, "fileName : " + file.getName());
                request.receive(file);
                JSONObject fileEntry = FileUtils.getFilePlugin().getEntryForFile(file);
                response.put("file", fileEntry);
                this.getCallbackContext().success(response);
            } else {
            	LOG.i(TAG, "File Download Fail!!!!!!");
                response.put("error", "There was an error downloading the file");
                this.getCallbackContext().error(response);
            }
        } catch(URISyntaxException e) {
        	LOG.i(TAG, "File Download Error!!!!!!");
            this.respondWithError("There was an error with the given filePath");
        } catch (JSONException e) {
        	LOG.i(TAG, "File Download Error!!!!!!");
            this.respondWithError("There was an error generating the response");
        } catch (HttpRequestException e) {
        	LOG.i(TAG, "File Download Error!!!!!!");
            if (e.getCause() instanceof UnknownHostException) {
                this.respondWithError(0, "The host could not be resolved");
            } else if (e.getCause() instanceof SSLHandshakeException) {
                this.respondWithError("SSL handshake failed");
            } else {
                this.respondWithError("There was an error with the request");
            }
        }
    }
}
