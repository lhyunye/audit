package com.rmxc.audit.util;

import com.rmxc.pulldata.passengerFlow.utils.WriteTxt;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class demotest {

    public static void main(String[] args) throws IOException {

        //数据制造
        String table = "demo_3_5_first";
        String sqlHead = "insert into "+table+" values ";
        String sqlData = "";
        ArrayList<String> data = new ArrayList();
        ArrayList<Integer> id1 = new ArrayList();
        ArrayList<Integer> id2 = new ArrayList();
        Random rd = new Random();
        int id= 151541;
        for (int i = 0; i < 36; i++) {
            id = id + rd.nextInt(100);
            id1.add(new Integer(id));
            id = id + rd.nextInt(100);
            id2.add(new Integer(id));
        }
        for (int i = 0; i < 2500; i++) {
            String dd = "("+id1.get(rd.nextInt(36))+","+id2.get(rd.nextInt(36))+","+"1";
            String ddz ="";
            for (int j = 0; j < 40; j++) {
                ddz=ddz+","+rd.nextInt(135);
            }
            data.add(dd+ddz+")");
        }
        for (int i = 0; i < 2500; i++) {
            String dd = "("+id1.get(rd.nextInt(36))+","+id2.get(rd.nextInt(36))+","+"2";
            String ddz ="";
            for (int j = 0; j < 40; j++) {
                ddz=ddz+","+rd.nextInt(135);
            }
            data.add(dd+ddz+")");
        }
        for (int i = 0; i < 2500; i++) {
            String dd = "("+id1.get(rd.nextInt(36))+","+id2.get(rd.nextInt(36))+","+"3";
            String ddz ="";
            for (int j = 0; j < 40; j++) {
                ddz=ddz+","+rd.nextInt(135);
            }
            data.add(dd+ddz+")");
        }
        for (int i = 0; i < 2500; i++) {
            String dd = "("+id1.get(rd.nextInt(36))+","+id2.get(rd.nextInt(36))+","+"4";
            String ddz ="";
            for (int j = 0; j < 40; j++) {
                ddz=ddz+","+rd.nextInt(135);
            }
            data.add(dd+ddz+")");
        }
         sqlData = data.stream().collect(Collectors.joining(","));

        BufferedWriter bw = WriteTxt.getbw("./sql43.txt");
        bw.write(sqlHead+sqlData);
        bw.close();
    }
}
