package com.rmxc.pulldata.passengerFlow.utils.util;

import com.rmxc.pulldata.passengerFlow.utils.Client;
import com.rmxc.pulldata.passengerFlow.utils.Request;
import com.rmxc.pulldata.passengerFlow.utils.Response;
import com.rmxc.pulldata.passengerFlow.utils.constant.Constants;
import com.rmxc.pulldata.passengerFlow.utils.constant.ContentType;
import com.rmxc.pulldata.passengerFlow.utils.constant.HttpHeader;
import com.rmxc.pulldata.passengerFlow.utils.constant.HttpSchema;
import com.rmxc.pulldata.passengerFlow.utils.enums.Method;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class KeLiuHttpUtils {

	/**
	 * get
	 * @param host
	 * @param path
	 * @param appKey
	 * @param appSecret
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String host, String path, String appKey, String appSecret)
            throws Exception {

		Map<String, String> headers = new HashMap<String, String>();
		//（必填）根据期望的Response内容类型设置
		headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");
		headers.put("a-header1", "header1Value");
		headers.put("b-header2", "header2Value");

		Request request = new Request(Method.GET, HttpSchema.HTTP + host, path, appKey, appSecret, Constants.DEFAULT_TIMEOUT);
		request.setHeaders(headers);

		//调用服务端
		Response response = Client.execute(request);

		return response.getBody();
    }

	/**
	 * post form
	 * @param host
	 * @param path
	 * @param appKey
	 * @param appSecret
	 * @param bodys
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String host, String path, String appKey, String appSecret, Map<String, String> bodys)
            throws Exception {

		Map<String, String> headers = new HashMap<String, String>();
		//（必填）根据期望的Response内容类型设置
		headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");

		Request request = new Request(Method.POST_FORM, HttpSchema.HTTP + host, path, appKey, appSecret, Constants.DEFAULT_TIMEOUT);
		request.setHeaders(headers);

		request.setBodys(bodys);

		//调用服务端
		Response response = Client.execute(request);

		return response.getBody();
    }

	/**
	 * Post String
	 * @param host
	 * @param path
	 * @param appKey
	 * @param appSecret
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String host, String path, String appKey, String appSecret, String body)
            throws Exception {

		Map<String, String> headers = new HashMap<String, String>();
		//（必填）根据期望的Response内容类型设置
		headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");
		//（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
		headers.put(HttpHeader.HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMD5(body));
		//（POST/PUT请求必选）请求Body内容格式
		headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);

		Request request = new Request(Method.POST_STRING, HttpSchema.HTTP + host, path, appKey, appSecret, Constants.DEFAULT_TIMEOUT);
		request.setHeaders(headers);
		request.setStringBody(body);

		//调用服务端
		Response response = Client.execute(request);
		return response.getBody();
    }

	/**
	 * Post stream
	 * @param host
	 * @param path
	 * @param appKey
	 * @param appSecret
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static String doPostbyStream(String host, String path, String appKey, String appSecret, String body)
            throws Exception {
		//Body内容
		byte[] bytesBody = body.getBytes(Constants.ENCODING);

		Map<String, String> headers = new HashMap<String, String>();
		//（必填）根据期望的Response内容类型设置
		headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");
		//（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
		headers.put(HttpHeader.HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMD5(bytesBody));
		//（POST/PUT请求必选）请求Body内容格式
		headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);

		Request request = new Request(Method.POST_BYTES, HttpSchema.HTTP + host, path, appKey, appSecret, Constants.DEFAULT_TIMEOUT);
		request.setHeaders(headers);
		request.setBytesBody(bytesBody);

		//调用服务端
		Response response = Client.execute(request);
		return response.getBody();
    }
	
	/**
	 * Put String
	 * @param host
	 * @param path
	 * @param method
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static String doPut(String host, String path, String appKey, String appSecret, String body)
            throws Exception {

		Map<String, String> headers = new HashMap<String, String>();
		//（必填）根据期望的Response内容类型设置
		headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");
		//（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
		headers.put(HttpHeader.HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMD5(body));
		//（POST/PUT请求必选）请求Body内容格式
		headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);

		Request request = new Request(Method.POST_STRING, HttpSchema.HTTP + host, path, appKey, appSecret, Constants.DEFAULT_TIMEOUT);
		request.setHeaders(headers);
		request.setStringBody(body);

		//调用服务端
		Response response = Client.execute(request);

		return response.getBody();
    }
	
	/**
	 * Put stream
	 * @param host
	 * @param path
	 * @param method
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doPut(String host, String path, String method,
                                     Map<String, String> headers,
                                     Map<String, String> querys,
                                     byte[] body)
            throws Exception {    	
    	HttpClient httpClient = wrapClient(host);

    	HttpPut request = new HttpPut(buildUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
        	request.addHeader(e.getKey(), e.getValue());
        }

        if (body != null) {
        	request.setEntity(new ByteArrayEntity(body));
        }

        return httpClient.execute(request);
    }
	
	/**
	 * Delete
	 *  
	 * @param host
	 * @param path
	 * @param method
	 * @param headers
	 * @param querys
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doDelete(String host, String path, String method,
                                        Map<String, String> headers,
                                        Map<String, String> querys)
            throws Exception {    	
    	HttpClient httpClient = wrapClient(host);

    	HttpDelete request = new HttpDelete(buildUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
        	request.addHeader(e.getKey(), e.getValue());
        }
        
        return httpClient.execute(request);
    }
	
	private static String buildUrl(String host, String path, Map<String, String> querys) throws UnsupportedEncodingException {
    	StringBuilder sbUrl = new StringBuilder();
    	sbUrl.append(host);
    	if (!StringUtils.isBlank(path)) {
    		sbUrl.append(path);
        }
    	if (null != querys) {
    		StringBuilder sbQuery = new StringBuilder();
        	for (Map.Entry<String, String> query : querys.entrySet()) {
        		if (0 < sbQuery.length()) {
        			sbQuery.append("&");
        		}
        		if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
        			sbQuery.append(query.getValue());
                }
        		if (!StringUtils.isBlank(query.getKey())) {
        			sbQuery.append(query.getKey());
        			if (!StringUtils.isBlank(query.getValue())) {
        				sbQuery.append("=");
        				sbQuery.append(URLEncoder.encode(query.getValue(), "utf-8"));
        			}        			
                }
        	}
        	if (0 < sbQuery.length()) {
        		sbUrl.append("?").append(sbQuery);
        	}
        }
    	
    	return sbUrl.toString();
    }
	
	private static HttpClient wrapClient(String host) {
		HttpClient httpClient = new DefaultHttpClient();
		if (host.startsWith("https://")) {
			sslClient(httpClient);
		}
		
		return httpClient;
	}
	
	private static void sslClient(HttpClient httpClient) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] xcs, String str) {
                	
                }
                public void checkServerTrusted(X509Certificate[] xcs, String str) {
                	
                }
            };
            ctx.init(null, new TrustManager[] { tm }, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = httpClient.getConnectionManager();
            SchemeRegistry registry = ccm.getSchemeRegistry();
            registry.register(new Scheme("https", 443, ssf));
        } catch (KeyManagementException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
        	throw new RuntimeException(ex);
        }
    }
}
