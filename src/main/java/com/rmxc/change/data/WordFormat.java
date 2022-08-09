package com.rmxc.change.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.*;
public class WordFormat {

    public static String read(File file){

        StringBuffer sb = new StringBuffer();

        try {

            InputStream in = new FileInputStream(file);
            InputStreamReader inr = new InputStreamReader(in,"GB2312");
            BufferedReader fis = new BufferedReader(inr);

            String str;
            int num = 0;
            while (( str = fis.readLine() ) != null){
                sb.append(str+"\n");
                num++;
            }
            System.out.println("处理数据行数："+num);
            fis.close();
            in.close();



        } catch (FileNotFoundException e) {
            System.out.println("文件读取失败！！");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            return sb.toString();
        }

    }

    public static void write(File file, String date){


        try {
            OutputStream out = new FileOutputStream(file);
            OutputStreamWriter outs = new OutputStreamWriter(out,"UTF-8");
            BufferedWriter ff = new BufferedWriter(outs);
            PrintWriter pw = new PrintWriter(ff);
            pw.print(date);

            pw.close();
            ff.close();
            outs.close();
            out.close();
            System.out.println("写入完成");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("关闭失败！！");
            e.printStackTrace();
        }

    }

    public static void dealword(File inFile,File outFile){
        try {

            InputStream in = new FileInputStream(inFile);
            InputStreamReader inr = new InputStreamReader(in,"GB2312");
            BufferedReader fis = new BufferedReader(inr);

            OutputStream out = new FileOutputStream(outFile);
            OutputStreamWriter outs = new OutputStreamWriter(out,"UTF-8");
            BufferedWriter ff = new BufferedWriter(outs);
            PrintWriter pw = new PrintWriter(ff);



            String str;
            int num = 0;
            while (( str = fis.readLine() ) != null){
                pw.println(str);
                num++;
            }
            System.out.println("处理数据行数："+num);
            fis.close();
            in.close();

            pw.close();
            ff.close();
            outs.close();
            out.close();
            System.out.println("写入完成");


        } catch (FileNotFoundException e) {
            System.out.println("文件读取失败！！");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
    }

    public static void exec(String filename){

        //原文件全路径
        File inFile = new File("./"+filename);

        //目标文件全路径
        File outFile = new File("./new"+filename);
       dealword(inFile,outFile);

    }


}
