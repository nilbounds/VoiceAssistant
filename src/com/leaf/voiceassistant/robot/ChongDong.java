
package com.leaf.voiceassistant.robot;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.text.Html;

import com.leaf.voiceassistant.IView;

public class ChongDong extends Robot {
    private String WebAPI_Path = "http://wap.unidust.cn/api/searchout.do?type=wap&ch=1001&info=";
    private HttpClient httpClient = null;

    public ChongDong(Context context, IRobotListener listener, IView v) {
        super(context, listener, v);
        this.enable = true;
    }

    private String getResult(String question) {
        String strResult = null;
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
        HttpConnectionParams.setSoTimeout(httpParams, 30000);
        httpClient = new DefaultHttpClient(httpParams);
        try {
            String strQuestion = WebAPI_Path + question;
            HttpGet httpRequest = new HttpGet(strQuestion);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                String str = EntityUtils.toString(httpResponse.getEntity());
                strResult = new String(str.getBytes("ISO-8859-1"), "UTF-8");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strResult;
    }

    @Override
    public String getAnswer(String question) {
        String result = getResult(question);
        int firstIndex = result.indexOf("<br/>") + 5;
        // int secondIndex=result.indexOf("<br/>", firstIndex)+5;
        int lastIndex = result.lastIndexOf("<br/>");
        /*
         * if(lastIndex>secondIndex) return
         * Html.fromHtml(result.substring(secondIndex,lastIndex)); else
         */
        return Html.fromHtml(result.substring(firstIndex, lastIndex)).toString();
    }

    @Override
    public void close() {

    }
}
