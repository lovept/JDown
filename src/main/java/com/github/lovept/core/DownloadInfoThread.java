package com.github.lovept.core;

import com.github.lovept.constants.Constant;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author lovept :)
 * @date 2024/6/26 17:02
 * @description 下载信息线程
 */
public class DownloadInfoThread implements Runnable {

    // 下载文件总大小
    private final long httpFileContentLength;

    // 本次累计下载的大小 强制线程从主内存读一下 (多线程读取)
    public static volatile LongAdder downSize = new LongAdder();

    public static int animeIndex = 0;

    public DownloadInfoThread(long httpFileContentLength) {
        this.httpFileContentLength = httpFileContentLength;
    }


    @Override
    public void run() {
        // 计算文件总大小
        double httpFileSize = httpFileContentLength / Constant.MB;
        String httpFileSizeInfo = String.format("%.2f", httpFileSize);

        // 已下载大小
        double downloadedSize = downSize.doubleValue() / Constant.MB;
        String currentFileSize = String.format("%.2f", downloadedSize);

        String downInfo = String.format(" %sMb/%sMb", currentFileSize, httpFileSizeInfo);

        int equalNum = Math.round((float) (downloadedSize / httpFileSize * 50));

        System.out.print("\r");
        System.out.print(printEqual(equalNum, downInfo));
    }

    private String printEqual(int equalNum, String downInfo) {
        return "Downloading " + animationChars(animeIndex++ % 4) + "[" +
                "=".repeat(Math.max(0, equalNum)) +
                ">" +
                " ".repeat(Math.max(0, 50 - equalNum)) +
                "]" +
                downInfo;
    }

    private String animationChars(int num) {
        return switch (num) {
            case 0 -> "|";
            case 1 -> "/";
            case 2 -> "-";
            default -> "\\";
        };
    }
}
