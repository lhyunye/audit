package com.rmxc.pulldata.passengerFlow.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
@Slf4j
public class ReadTxt {


    /**
     * @Param fileName
     * @Author lihao
     * 一次性读取文件内容
     * */
    public static String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
           log.error("未找到文件路径："+fileName);
        } catch (IOException e) {
            log.error("文件写入异常："+fileName);
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            log.error("The OS does not support " + encoding);
            return null;
        }
    }
}
