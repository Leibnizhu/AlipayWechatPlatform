package com.turingdi.awp.util.common;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-26 17:32.
 */
public class NetworkUtils {
    private static final Logger LOG = LoggerFactory.getLogger(NetworkUtils.class);
    private static HttpClient client;

    public static void asyncPostJson(Vertx vertx, String url, Handler<JsonObject> callback) throws IOException {
        checkHttpClient(vertx);
        client.requestAbs(HttpMethod.POST, url, resp -> {
            resp.bodyHandler(buf -> {
               callback.handle(buf.toJsonObject());
            });
        }).end();
    }

    public static void asyncPostString(Vertx vertx, String url, Handler<String> callback) throws IOException {
        checkHttpClient(vertx);
        client.requestAbs(HttpMethod.POST, url, resp -> {
            resp.bodyHandler(buf -> {
               callback.handle(buf.toString());
            });
        }).end();
    }

    private static void checkHttpClient(Vertx vertx) {
        if(client == null){
            client = vertx.createHttpClient(new HttpClientOptions().setLogActivity(false));
        }
    }


    //TODO GET方法请求两个方法的改成异步

    /**
     * 下载网络文件到指定文件
     *
     * @param url  网络文件地址
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
     *
     * @param img  文件的字节数组
     * @param file 用于保存的文件
     */
    private static void writeImageToDisk(byte[] img, File file) {
        FileOutputStream fops = null;
        try {
            fops = new FileOutputStream(file);
            fops.write(img);
            fops.flush();
            fops.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fops != null){
                try {
                    fops.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据地址获得数据的字节流
     *
     * @param strUrl 网络连接地址
     * @return 读取到的字节数组
     */
    private static byte[] getFileFromNetByUrl(String strUrl) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
    private static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
}
