package com.hiwoo.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Configuration
public class SysHttpUtil {

    public static String sendHttpGet(String path,String token,Map<String, String> paramMap) throws Exception {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String responseContent = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(path);
            httpGet.addHeader("token", token);
            httpGet.addHeader("Content-Type", "application/json;charset=utf-8");

            List<NameValuePair> params = setHttpParams(paramMap);
            String param = URLEncodedUtils.format(params, "UTF-8");
            httpGet.setURI(URI.create(path + "?" + param));

            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");

        }catch (Exception exception){
            throw exception;
        }finally {
            if (response == null) throw new AssertionError();
            response.close();
            httpClient.close();
        }
        return responseContent;
    }

    public static List<NameValuePair> setHttpParams(Map<String, String> paramMap){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Set<Map.Entry<String, String>> set = paramMap.entrySet();
        for(Map.Entry<String, String> entry : set){
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return params;
    }
}
