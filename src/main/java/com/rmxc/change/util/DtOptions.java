package com.rmxc.change.util;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
public class DtOptions {

    public static Options generateOp() {
        final Options options = new Options();
        options.addOption(new Option("ant", "ant", false, "command help"));
        options.addOption(new Option("t", "type", true, "exec type:convert|dataformat"));
        options.addOption(new Option("e", "environment", false, "database environment : dev|prod"));
        options.addOption(new Option("p", "prefix", false, "table prefix"));
        options.addOption(new Option("i", "inputpath", true, "file input path ，default：./dataFile/"));
        return options;
    }
}
