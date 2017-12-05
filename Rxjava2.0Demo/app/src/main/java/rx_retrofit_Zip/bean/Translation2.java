package rx_retrofit_Zip.bean;

import android.util.Log;

/**
 * Created by 97952 on 2017/12/5.
 */

public class Translation2 {
    private int status;
    private content content;
    private static class content {
        private String from;
        private String to;
        private String vendor;
        private String out;
        private int errNo;
    }

    //定义 输出返回数据 的方法
    public String show() {

        return content.out;

    }
}
