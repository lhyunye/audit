package com.rmxc.change;


import com.rmxc.change.data.DataFormat;
import com.rmxc.change.structure.Convert;
import com.rmxc.change.util.DtOptions;
import lombok.extern.slf4j.Slf4j;


import java.io.File;
@Slf4j
public class schedule {

    //文件读取路径
    private static final String directoryPath = "./dataFile/";

    //环境参数
    private static final String defaultEnvironment = "dev";


    public static void main(String[] args) {

//        DataFormat dataFormat = new DataFormat();   di 增长
//        dataFormat.exec(args[0]);
        Convert convert =new Convert();
        convert.exec(directoryPath,"ods_city_sa_1_mcht_db_","prod");

    }

//    /**
//     * 根据外部参数执行脚本
//     *
//     * @Param CommandLine: 参数列表
//     * @Author lihao
//     */
//    public static void dealArgs(CommandLine line) {
//        String type = "";
//        String environment = defaultEnvironment;
//        String inputpath = defaultDirectoryPath;
//        String prefix = "";
//        //执行类型
//        if (line.hasOption("t")) {
//            type = line.getOptionValue("t");
//        } else {
//            System.out.println("未选择执行类型,可使用java -jar x.jar -ant 命令来查看脚本参数 ");
//            return;
//        }
//        //数据输入路径
//        if (line.hasOption("i")) {
//            inputpath = line.getOptionValue("i");
//        }
//        //环境配置
//        if (line.hasOption("e")) {
//            environment = line.getOptionValue("e");
//        }
//        //数据表前缀
//        if (line.hasOption("p")) {
//            prefix = line.getOptionValue("p");
//        }
//
//        if ("convert".equals(type)) {
//            Convert convert = new Convert();
//            convert.exec(inputpath, prefix, environment);
//        } else if ("dataformat".equals(type)) {
//            DataFormat dataFormat = new DataFormat();
//            dataFormat.exec(inputpath);
//        } else {
//            System.out.println("类型选择错误，可使用java -jar x.jar -ant 命令来查看脚本参数");
//        }
//    }


}
