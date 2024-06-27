package com.github.lovept;

import com.github.lovept.commands.DownloadCommand;
import picocli.CommandLine;

/**
 * @author lovept :)
 * @date 2024/6/26 17:02
 * @description 主程序
 */
public class App 
{
    public static void main(String[] args) {
        // -u https://ys-api.mihoyo.com/event/download_porter/link/ys_cn/official/pc_default -o /Users/username/Downloads -p 6 -n yuanshen.exe
        //String url = "https://ys-api.mihoyo.com/event/download_porter/link/ys_cn/official/pc_default";
        new CommandLine(new DownloadCommand()).execute(args);
    }
}
