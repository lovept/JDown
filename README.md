# JDown
一款命令行式网络文件下载器. File Downloader.

# 注意
本程序需要JDK 22 环境

# 开始使用
```
git clone https://github.com/nilaoda/BBDown.git
```
## 方式1
使用idea打开
配置入口程序`com.github.lovept.App`参数使用
```
-u https://ys-api.mihoyo.com/event/download_porter/link/ys_cn/official/pc_default -o your_path -p 8 -n yuanshen.exe
```
## 方式2
使用maven打包
运行命令：
```java
java -jar JDown-0.0.1-jar-with-dependencies.jar -u https://ys-api.mihoyo.com/event/download_porter/link/ys_cn/official/pc_default -o your_path -p 8 -n yuanshen.exe
```

目前所支持的命令行参数：
```
JDown - A java command line download tool
Usage: jdown [-hV] [-n=<fileName>] -o=<outputPath> [-p=<partNum>] -u=<url>

Option are:
  -h, --help                 Show this help message and exit.
  -n, --name=<fileName>      file name
* -o, --output=<outputPath>  output path
  -p, --part-num=<partNum>   number of blocks
* -u, --url=<url>            download link
  -V, --version              Print version information and exit.

Developed by ❤️lovept :)
```
