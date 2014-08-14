
package com.leaf.voiceassistant.robot;

import android.content.Context;
import android.util.Log;

import com.leaf.voiceassistant.DialogParams;
import com.leaf.voiceassistant.IView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class XiaoI extends Robot {

    private String userId;
    private String robotId;
    private String sessionId;

    private Pattern userIdPattern = Pattern.compile("(?<=\"userId\":\")[^\"]*");
    private Pattern robotIdPattern = Pattern.compile("(?<=\"robotId\":\")[^\"]*");
    private Pattern sessionIdPattern = Pattern.compile("(?<=\"sessionId\":\")[^\"]*");
    private Pattern contentPattern = Pattern.compile("(?<=\"content\":\")[^\"]*");

    private String Webbot_Path = "http://i.xiaoi.com/robot/webrobot?&callback=__webrobot__processOpenResponse&data=%7B%22type%22%3A%22open%22%7D&ts=";
    private String open = "http://i.xiaoi.com/robot/webrobot?&callback=__webrobot_processMsg&data=%7B%22type%22%3A%22sessionopen%22%2C%22sessionId%22%3A%22{sessionId}%22%2C%22robotId%22%3A%22webbot%22%2C%22userId%22%3A%22{userId}%22%7D&ts=";
    private String alive = "http://i.xiaoi.com/robot/webrobot?&callback=__webrobot_processMsg&data=%7B%22type%22%3A%22keepalive%22%7D&ts=";
    private String send = "http://i.xiaoi.com/robot/webrobot?&callback=__webrobot_processMsg&data=%7B%22sessionId%22%3A%22{sessionId}%22%2C%22robotId%22%3A%22webbot%22%2C%22userId%22%3A%22{userId}%22%2C%22body%22%3A%7B%22content%22%3A%22{content}%22%7D%2C%22type%22%3A%22txt%22%7D&ts=";
    private HttpClient httpClient = null;
    private Map<String, String> cookies = new HashMap<String, String>();
    
    private String getCnonce(){
    	return String.valueOf((int)Math.ceil(Math.random()*899999)+100000);
    }
    private String h(String k) {
        return g(f(p(k)));
    }
    private int[] f(int[] K) {
        int[] H = K;
        int[] I = new int[80];
        int G = 1732584193;
        int F = -271733879;
        int E = -1732584194;
        int D = 271733878;
        int C = -1009589776;
        for (int z = 0; z < H.length; z += 16) {
            int B = G;
            int A = F;
            int y = E;
            int v = D;
            int k = C;
            for (int u = 0; u < 80; u++) {
                if (u < 16) {
                    I[u] = H[z + u];
                } else {
                    I[u] = l(I[u - 3] ^ I[u - 8] ^ I[u - 14] ^ I[u - 16], 1);
                }
                int J = q(q(l(G, 5), s(u, F, E, D)), q(q(C, I[u]), i(u)));
                C = D;
                D = E;
                E = l(F, 30);
                F = G;
                G = J;
            }
            G = q(G, B);
            F = q(F, A);
            E = q(E, y);
            D = q(D, v);
            C = q(C, k);
        }
        int[] ret = new int[5];
        ret[0] = G;
        ret[1] = F;
        ret[2] = E;
        ret[3] = D;
        ret[4] = C;
        return ret;
    }
    private int s(int u, int k, int w, int v) {
        if (u < 20) {
            return (k & w) | ((~k) & v);
        }
        if (u < 40) {
            return k ^ w ^ v;
        }
        if (u < 60) {
            return (k & w) | (k & v) | (w & v);
        }
        return k ^ w ^ v;
    }
    private int i(int k) {
        return (k < 20) ? 1518500249 : (k < 40) ? 1859775393 : (k < 60) ? -1894007588 : -899497514;
    }
    private int q(int k, int w) {
        int v = (k & 65535) + (w & 65535);
        int u = (k >> 16) + (w >> 16) + (v >> 16);
        return (u << 16) | (v & 65535);
    }
    private int l(int k,int u) {
        return (k << u) | (k >>> (32 - u));
    }
    private int[] p(String v) {
        int k = ((v.length() + 8) >> 6) + 1;
        int[] w = new int[k * 16];
        int u;
        for (u = 0; u < k * 16; u++) {
            w[u] = 0;
        }
        for (u = 0; u < v.length(); u++) {
            w[u >> 2] |=  ((int)v.charAt(u)) << (24 - (u & 3) * 8);
        }
        
        w[u >> 2] |= 128 << (24 - (u & 3) * 8);
        w[k * 16 - 1] = v.length() * 8;
        return w;
    }
    private String g(int[] v) {
        String u = "0123456789abcdef";
        String w = "";
        for (int k = 0; k < v.length * 4; k++) {
            w+= u.charAt((v[k >> 2] >> ((3 - k % 4) * 8 + 4)) & 15)+"" + u.charAt((v[k >> 2] >> ((3 - k % 4) * 8)) & 15);
        }
        return w.toString();
    }
    private String j(String u) {
        String w = "",
        x = "3.141592653589793I".substring(0, 7);
        for (int v = 0; v < x.length(); v++) {
            char y = x.charAt(v);
            if (y != '.') {
                w = y + w;
            }
        }
        return h(u + w);
    }
    private String getSig(String cnonce){
    	String r = cookies.get("nonce");
    	if(r == null)r = "";
    	return h(j(r) + cnonce);
    }
    
    private void setCookie(String cookie){
    	String[] headers = cookie.split(";");
    	for(String header:headers){
    		String[] keyValue = header.split("=");
    		if(keyValue.length != 2)continue;
    		cookies.put(keyValue[0].trim(), keyValue[1].trim());
    	}
    	String cnonce = getCnonce();
    	cookies.put("cnonce", cnonce);
    	String sig = getSig(cnonce);
    	cookies.put("sig", sig);
    	cookies.put("PATH", "/robot/");
    }
    private String getCookie(){
    	
    	String result="";
    	for(String key :cookies.keySet()){
    		result += key+"="+cookies.get(key)+";";
    	}
    	Log.d("cookie",result);
    	return result;
    }
    
    public XiaoI(Context c, IRobotListener listener, IView v) {
        super(c, listener, v);
        if (initialize()) enable = true;
        if (!enable) view.showDialog(new DialogParams("小I机器人初始化失败", true, null, null));
    }

    private boolean initialize() {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
        HttpConnectionParams.setSoTimeout(httpParams, 30000);
        httpClient = new DefaultHttpClient(httpParams);
        
        String strResult = getHtml(Webbot_Path + System.currentTimeMillis());
        if (strResult == null) return false;
        Log.d("xiaoi",strResult);
        robotId = getRobotId(strResult);
        if (robotId == null) {
        	Log.e("xiaoi", "robotId==null");
        	return false;
        }
        sessionId = getSessionId(strResult);
        if (sessionId == null) {
        	Log.e("xiaoi", "sessionId==null");
        	return false;
        }
        userId = getUserId(strResult);
        if (userId == null){
        	Log.e("xiaoi", "userId==null");
        	return false;
        }
        open = open.replace("{sessionId}", sessionId).replace("{userId}", userId).replace("{robotId}", robotId);
        send = send.replace("{sessionId}", sessionId).replace("{userId}", userId).replace("{robotId}", robotId);
        Log.d("xiaoi",open);
        String html = getHtml(open + System.currentTimeMillis());
        Log.d("xiaoi",html);
        getHtml(alive+System.currentTimeMillis());
        return true;
    }

    @Override
    public String getAnswer(String str) {
    	getHtml(alive+System.currentTimeMillis());
        str = URLEncoder.encode(str);
        String html = getHtml(send.replace("{content}", str) + System.currentTimeMillis());
        return getContent(html);
    }

    private String getHtml(String req) {
    	Log.d("xiaoi-req",req);
        try {
            HttpGet httpRequest = new HttpGet(req);
            httpRequest.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            httpRequest.addHeader("Accept-Encoding","gzip,deflate,sdch");
            httpRequest.addHeader("Accept-Language","zh-CN,zh;q=0.8");
            httpRequest.addHeader("Cache-Control","max-age=0");
            httpRequest.addHeader("Host", "i.xiaoi.com");
            httpRequest.addHeader("Referer","http://i.xiaoi.com/");
            httpRequest.addHeader("User-Agent","Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110Safari/537.36");
            httpRequest.addHeader("Connection","keep-alive");
            httpRequest.addHeader("Cookie",getCookie());
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                String strResult = EntityUtils.toString(httpResponse.getEntity());
                Header[] headers = httpResponse.getHeaders("Set-Cookie");
                if(headers!=null)for(Header header:headers){
                	setCookie(header.getValue());
                }
                return strResult;
            }else{
            	Log.d("link to xiao i error","response"+httpResponse.getStatusLine().getStatusCode());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        return null;
    }

    private String getContent(String str) {
        if (str == null) return "连接,小I服务器,出错";
        str = str.replaceAll("\\\\r\\\\n", "\n").replaceAll("\\[[^\\]]*\\]", "").replaceAll("\\\\u\\w{1,4}", "").replace("\\\"", "");
        Matcher m = contentPattern.matcher(str);
        if (m.find()) { return m.group(); }
        return "没有找到答案";
    }

    private String getUserId(String str) {
        if (str == null) return null;
        Matcher m = userIdPattern.matcher(str);
        if (m.find()) { return m.group(); }
        return null;
    }

    private String getSessionId(String str) {
        if (str == null) return null;
        Matcher m = sessionIdPattern.matcher(str);
        if (m.find()) { return m.group(); }
        return null;
    }

    private String getRobotId(String str) {
        if (str == null) return null;
        Matcher m = robotIdPattern.matcher(str);
        if (m.find()) { return m.group(); }
        return null;
    }

    @Override
    public void close() {

    }
}
