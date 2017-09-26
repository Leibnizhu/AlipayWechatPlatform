package com.turingdi.awp.util;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Random;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-26 17:32.
 */
public class NetworkUtils {
    private static final Logger LOG = Logger.getLogger(NetworkUtils.class);

    /**
     * 执行接口
     *
     * @param url  需要执行的链接,参数实体
     * @param data jsondata
     * @param type json或xml
     * @return 响应的结果
     * @author lws 2016-12-12
     */
    public static String postRequestWithData(String url, String data, String type, String encode, boolean getCookie) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httppost = new HttpPost(url);
        if (data != null) {
            StringEntity entity = new StringEntity(data);
            if ("json".equals(type)) {
                // 设置类型
                entity.setContentType("application/json;charset=" + encode);
            } else if ("xml".endsWith(type)) {
                entity.setContentType("application/xml;charset=" + encode);
            } else if ("form".endsWith(type)) {
                entity.setContentType("application/x-www-form-urlencoded");
            }
            httppost.setEntity(entity);
        }

        HttpResponse response = httpClient.execute(httppost);
        HttpEntity httpEntity = response.getEntity();
        if(getCookie){
            Header[] headers =  response.getHeaders("Set-Cookie");
            StringBuilder cookieTemp = new StringBuilder();
            for(Header header : headers){
                cookieTemp.append(header.getName()).append("=").append(header.getValue()).append(";");
            }
            return cookieTemp.toString();
        } else {
            return EntityUtils.toString(httpEntity, encode);
        }
    }

    public static String postRequestWithData(String url, String data, String type) throws IOException {
        return postRequestWithData(url, data, type, "utf-8", false);
    }

    public static String postRequestWithData(String url, String data, String type, boolean getCookie) throws IOException {
        return postRequestWithData(url, data, type, "utf-8", getCookie);
    }

    public static String getRequestWithoutData(String url) {
        return  getRequestWithoutData(url, null);
    }

    /**
     * 发送GET请求，获取响应内容String
     *
     * @param url 请求路径
     * @return 请求返回的内容
     * @author Leibniz
     */
    public static String getRequestWithoutData(String url, List<Cookie> cookies) {
        BufferedReader in = null;
        StringBuilder sbuf = new StringBuilder();
        try {
            URL reqURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) reqURL.openConnection(); // 进行连接，但是实际上getrequest要在下一句的connection.getInputStream() 函数中才会真正发到服务器
            if(cookies != null && cookies.size() > 0){
                StringBuilder cookieTemp = new StringBuilder();
                for(Cookie cookie : cookies){
                    cookieTemp.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
                }
                connection.setRequestProperty("Cookie", cookieTemp.toString());
            }
            connection.setDoOutput(false);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(200);
            connection.setDoInput(true);
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                sbuf.append(line).append("\n");
            }
        } catch (IOException e) {
            LOG.error("连接服务器'" + url + "'时发生错误：" + e.getMessage());
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sbuf.toString();
    }

    public static String getRandomIPAddress() {
        Random rand = new Random(System.currentTimeMillis());
        return new StringBuffer().append((rand.nextInt(128) + 126)).append(".")
                .append(rand.nextInt(255)).append(".")
                .append(rand.nextInt(255)).append(".")
                .append(rand.nextInt(255))
                .toString();
    }

    /**
     * 下载网络文件到指定文件
     *
     * @param url 网络文件地址
     * @param file 指定文件对象
     */
    public static void downloadFile(String url, File file) {
        byte[] fileBytes = getFileFromNetByUrl(url);
        if (null != fileBytes && fileBytes.length > 0) {
            LOG.info("读取到：" + fileBytes.length + " 字节");
            writeImageToDisk(fileBytes, file);
        } else {
            throw new RuntimeException("没有从该链接获得内容");
        }
    }

    /**
     * 将文件写入到磁盘
     * @param img 文件的字节数组
     * @param file 用于保存的文件
     */
    private static void writeImageToDisk(byte[] img, File file){
        try {
            FileOutputStream fops = new FileOutputStream(file);
            fops.write(img);
            fops.flush();
            fops.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据地址获得数据的字节流
     *
     * @param strUrl 网络连接地址
     * @return 读取到的字节数组
     */
    private static byte[] getFileFromNetByUrl(String strUrl){
        try {
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            InputStream inStream = conn.getInputStream();//通过输入流获取图片数据
            return readInputStream(inStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从输入流中获取数据
     *
     * @param inStream 输入流
     * @return 读取到的字节数组
     */
    private static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
}
