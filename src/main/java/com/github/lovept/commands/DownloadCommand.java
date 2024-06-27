package com.github.lovept.commands;

import com.github.lovept.core.Downloader;
import picocli.CommandLine;

/**
 * @author lovept :)
 * @date 2024/6/26 17:02
 * @description 下载命令
 */
@CommandLine.Command(name = "jdown",
        mixinStandardHelpOptions = true,
        requiredOptionMarker = '*',
        version = "0.0.1",
        header = "JDown - A java command line download tool",
        optionListHeading = "%nOption are: %n",
        footer = "%nDeveloped by ❤\uFE0Flovept :)"
)
public class DownloadCommand implements Runnable {

    @CommandLine.Option(names = {"-u", "--url"}, required = true, description = "download link")
    private String url;

    @CommandLine.Option(names = {"-o", "--output"}, required = true, description = "output path")
    private String outputPath;

    @CommandLine.Option(names = {"-n", "--name"}, description = "file name")
    private String fileName;

    @CommandLine.Option(names = {"-p", "--part-num"}, defaultValue = "5", description = "number of blocks")
    private Integer partNum;


    @Override
    public void run() {
        Downloader downloader = new Downloader(url, outputPath, fileName, partNum);
        downloader.download();
    }
}
