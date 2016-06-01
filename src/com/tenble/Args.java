package com.tenble;

/**
 * Created by Ben on 1/06/2016.
 */
public class Args {

    String fileLoc;

    public Args(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("-file".equals(arg)) {
                i++;
                fileLoc = args[i];
            }
        }
    }

}
