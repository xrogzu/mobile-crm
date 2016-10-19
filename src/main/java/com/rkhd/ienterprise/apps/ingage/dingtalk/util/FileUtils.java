package com.rkhd.ienterprise.apps.ingage.dingtalk.util;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by dell on 2015/12/24.
 */
public class FileUtils {

    /**
     * 向某个文件中写数据，
     * @param content    被写进的内容
     * @param saveFile    目标文件
     * @param isAppend     追加还是清空写
     * @throws IOException
     */
    public static void write2File(String  content,String saveFile,boolean isAppend) throws IOException {
        FileWriter fw = null;
        //追加写
        fw = new FileWriter(saveFile,isAppend);
        Long startTime = System.currentTimeMillis();
        fw.write(content);
        Long estimatedTime = System.currentTimeMillis() - startTime;
        fw.close();
    }

    /**
     * 别忘记关闭reader
     * @param f
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static BufferedReader getReader(File f) throws FileNotFoundException, IOException {
        BufferedReader reader = null;
        if (f.getName().endsWith(".gz")) {
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(f))));
        } else {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        }
        return reader;
    }
}
