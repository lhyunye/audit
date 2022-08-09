package com.rmxc.pulldata.passengerFlow.utils;

import com.rmxc.pulldata.passengerFlow.utils.enums.Key;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class WriteTxt {

    private static String path = Key.DATAPATH.getValue();

    /**
     * @Param fileName
     * @Author lihao
     * 获取文件写入流
     * */
    public static BufferedWriter getbw(String fileName) throws IOException {
        //创建存储路径
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        //删除同名文件
        File file = new File(path+fileName);
        if(file.exists()){
            file.delete();
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path+fileName),true));
        return bw;
    }
    /**
     * @Param fileName
     * @Author lihao
     * 获取文件写入流
     * */
    public static BufferedWriter getbwnodel(String fileName) throws IOException {
        //创建存储路径
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        //删除同名文件
        File file = new File(path+fileName);


        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path+fileName),true));
        return bw;
    }

    /**
     * @Param fileName
     * @Param values 写入数据
     * @Author lihao
     * 循环写入数据
     * */
    public static void writeTxt(String fileName,BufferedWriter bw,Object... values){
        try {
            String rowdata= "";
            for (Object value:values){
                rowdata=rowdata+value+"\t";
            }
            bw.write(rowdata+"\n");
        }catch (Exception e){
           log.error(fileName+"数据写入异常！！！！！");
        }


    }


}
