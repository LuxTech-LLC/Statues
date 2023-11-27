package com.luxtechllc.statues.support;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

//From: https://stackoverflow.com/questions/2703161/how-to-ignore-ssl-certificate-errors-in-apache-httpclient-4-0
//Credit To: craftsmannadeem
public class GetRequest {

	public static JSONObject getURL(String url) throws ClientProtocolException, IOException {
		
		
		// Trust all certs
	    SSLContext sslcontext = null;	    
	    JSONObject output = null;
	    
		try {
			sslcontext = buildSSLContext();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	    // Allow TLSv1 protocol only
	    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
	            sslcontext,
	            SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	
	    CloseableHttpClient httpclient = HttpClients.custom()
	            .setSSLSocketFactory(sslsf)
	            .build();



		HttpGet httpget = new HttpGet(url);

        CloseableHttpResponse response = httpclient.execute(httpget);
        
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
			output = new JSONObject(IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));

		}
        response.close();
        httpclient.close();
	    
	    return output;
	
	}
    
	
	
	private static SSLContext buildSSLContext()
	        throws NoSuchAlgorithmException, KeyManagementException,
	        KeyStoreException {
	    SSLContext sslcontext = SSLContexts.custom()
	            .setSecureRandom(new SecureRandom())
	            .loadTrustMaterial(null, new TrustStrategy() {

	                public boolean isTrusted(X509Certificate[] chain, String authType)
	                        throws CertificateException {
	                    return true;
	                }
	            })
	            .build();
	    return sslcontext;
	}
}
