package com.github.lovept.core;


import com.github.lovept.constants.Constant;
import com.github.lovept.utils.HttpUtils;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author lovept :)
 * @date 2024/6/26 17:02
 * @description 下载器
 */
@Log4j2
public class Downloader {
    private final String url;
    private final String outputPath;
    private final String fileName;
    private final Integer partNum;

    // 线程池
    public ThreadPoolExecutor threadPoolExecutor;

    private final CountDownLatch countDownLatch;

    public ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public Downloader(String url, String outputPath, String fileName, int partNum) {
        this.url = url;
        this.outputPath = outputPath;
        this.fileName = fileName;
        this.partNum = partNum;
        this.threadPoolExecutor = new ThreadPoolExecutor(partNum, partNum, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(partNum));
        this.countDownLatch = new CountDownLatch(partNum);
    }

    public void download() {
        String filePath = outputPath + File.separator + fileName;

        // 获取本地文件大小
        File file = new File(filePath);
        long localFileLength = file.exists() && file.isFile() ? file.length() : 0;


        // 获取连接对象
        HttpURLConnection httpUrlConnection = null;
        DownloadInfoThread downloadInfoThread;
        try {
            httpUrlConnection = HttpUtils.getHttpUrlConnection(url);
            // 文件总大小
            int contentLength = httpUrlConnection.getContentLength();
            if (localFileLength == contentLength) {
                log.info("Download completed, no need to download again");
                return;
            }

            // 创建获取下载信息的任务对象
            downloadInfoThread = new DownloadInfoThread(contentLength);
            // 将任务交给线程执行
            scheduledExecutorService.scheduleAtFixedRate(downloadInfoThread, 100, 300, TimeUnit.MILLISECONDS);

            // 分块计算
            List<Future<Boolean>> futures = new ArrayList<>();
            spilt(url, futures);

            countDownLatch.await();
            // 当计数器置零后才会继续往后执行
            if (merge(filePath)) {
                cleanTempFile(filePath);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            scheduledExecutorService.shutdownNow();
            threadPoolExecutor.shutdown();
            if (httpUrlConnection != null) {
                httpUrlConnection.disconnect();
            }
            System.out.print("\r");
            System.out.println("Download completed!                                                                                                 ");
        }
    }

    public boolean merge(String filePath) {
        byte[] buffer = new byte[Constant.BYTE_SIZE];
        int len;
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) {
            for (int i = 0; i < partNum; i++) {
                try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(Paths.get(filePath + Constant.TEMP + (i + 1))))) {
                    while ((len = bis.read(buffer)) != -1) {
                        raf.write(buffer, 0, len);
                    }
                }
            }
            cleanTempFile(filePath);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void cleanTempFile(String filePath) {
        for (int i = 0; i < partNum; i++) {
            File file = new File(filePath + Constant.TEMP + (i + 1));
            file.delete();
        }
    }

    public void spilt(String url, List<Future<Boolean>> futures) {
        try {
            // 获取文件大小
            long length = HttpUtils.getHttpFileContentLength(url);

            // 计算切分后的文件大小
            long size = length / partNum;

            // 计算分块个数
            for (int i = 0; i < partNum; i++) {
                // 计算起始位置
                DownloaderTask task = getDownloaderTask(url, i, size, countDownLatch);

                // 提交到线程池中
                Future<Boolean> future = threadPoolExecutor.submit(task);

                futures.add(future);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private DownloaderTask getDownloaderTask(String url, int i, long size, CountDownLatch countDownLatch) {
        long startPos = i * size;

        // 计算结束位置
        long endPos;

        if (i == partNum - 1) {
            // 最后一块
            endPos = 0;
        } else {
            endPos = startPos + size;
        }

        // 如果不是第一块, 起始位置 + 1
        if (startPos != 0) {
            startPos++;
        }

        return new DownloaderTask(url, startPos, endPos, i + 1, countDownLatch, outputPath, fileName);
    }
}
