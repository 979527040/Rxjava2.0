package rx_retrofit_recall.FlatMap.bean;

import android.util.Log;

/**
 * Created by 97952 on 2017/12/5.
 */

public class Translation1 {
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
    public void show() {
        Log.d("RxJava", "翻译内容 = " + content.out);
    }
}
