package rx_retrofit_Zip.mode;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx_retrofit_Zip.Interface.GetRequest_Interface;
import rx_retrofit_Zip.bean.Translation1;
import rx_retrofit_Zip.bean.Translation2;

/**
 * Created by 97952 on 2017/12/5.
 */

public class Rx_Retrofit_Zip {
    private static final String TAG="M";
    //定义Observable接口类型的网络请求对象
    private Observable<Translation1> observable1;
    private Observable<Translation2> observable2;

    public void initRetrofit(Activity context){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/") // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                .build();
        GetRequest_Interface reqeust=retrofit.create(GetRequest_Interface.class);
        observable1=reqeust.getCall_1().subscribeOn(Schedulers.newThread());//新开线程进行网络请求1
        observable2=reqeust.getCall_2().subscribeOn(Schedulers.newThread());//新开线程进行网络请求2
        requestNetUtil(context);
    }
    private void requestNetUtil(final Activity context){
        //两个网络请求异步&同时发送
        Observable.zip(observable1, observable2, new BiFunction<Translation1, Translation2, String>() {
            //创建BiFunction对象传入的第3个参数=合并 后数据的数据类型
            @Override
            public String apply(Translation1 translation1, Translation2 translation2) throws Exception {
                return translation1.show()+"&"+translation2.show();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        //结合显示2个网络请求的数据结果
                        Log.e(TAG, "最终接收到的数据是：" + s);
                        Toast.makeText(context,"最终接收到的数据是：" + s,Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //网络请求错误时调用
                        System.out.println("登录失败");
                    }
                });
    }

}
