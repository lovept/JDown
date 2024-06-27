package com.github.lovept.core;


import cn.hutool.core.util.ObjectUtil;
import com.github.lovept.constants.Constant;
import com.github.lovept.utils.HttpUtils;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * @author lovept :)
 * @date 2024/6/26 17:02
 * @description 下载任务
 */
@Log4j2
public class DownloaderTask implements Callable<Boolean> {

    private final String url;

    private final String outputPath;

    private final String fileName;

    private final long startPos;

    private final long endPos;

    // 表示当前是第几部分
    private final int part;

    private final CountDownLatch countDownLatch;

    public DownloaderTask(String url, long startPos, long endPos, int part, CountDownLatch countDownLatch,
                          String outputPath, String fileName) {
        this.url = url;
        this.startPos = startPos;
        this.endPos = endPos;
        this.part = part;
        this.countDownLatch = countDownLatch;
        this.outputPath = outputPath;
        this.fileName = fileName;
    }

    @Override
    public Boolean call() throws Exception {
        // 获取文件名
        String httpFileName = HttpUtils.getHttpFileName(url);
        if (ObjectUtil.isNotEmpty(fileName)) {
            httpFileName = fileName;
        }
        // 分块的文件名
        httpFileName = httpFileName + Constant.TEMP + part;
        String filePath = outputPath + File.separator + httpFileName;

        // 获取分块下载的链接
        HttpURLConnection con = HttpUtils.getHttpUrlConnection(url, startPos, endPos);

        try (
                InputStream is = con.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                RandomAccessFile raf = new RandomAccessFile(filePath, "rw")
        ) {

            byte[] buffer = new byte[Constant.BYTE_SIZE];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                // 原子类
                DownloadInfoThread.downSize.add(len);
                raf.write(buffer, 0, len);
            }

        } catch (FileNotFoundException e) {
            log.error("下载文件不存在,{}", url);
            return false;
        } catch (Exception e) {
            log.error("下载出现异常");
            return false;
        } finally {
            con.disconnect();
            // 计数器 - 1
            countDownLatch.countDown();
        }

        return true;
    }
}
