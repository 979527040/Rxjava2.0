package rx_retrofit_recall.FlatMap.mode;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx_retrofit_recall.FlatMap.bean.Translation1;
import rx_retrofit_recall.FlatMap.bean.Translation2;

/**
 * Created by 97952 on 2017/12/5.
 */
//TODO 网络请求嵌套回调
public class NetRequest {
    private  String TAG="BaseRx";
    private rx_retrofit_recall.FlatMap.Interface.GetRequest_Interface request;
    private Observable<Translation1> observable1;
    private Observable<Translation2> observable2;
    public void initRetrofit(Activity context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/")
                .addConverterFactory(GsonConverterFactory.create())//使用Gson解析
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//设置支持Rxjava
                .build();
        //创建网络请求接口实例
        request = retrofit.create(rx_retrofit_recall.FlatMap.Interface.GetRequest_Interface.class);
        //采用Observable对网络请求接口进行封装
        observable1 = request.getCall_1();
        observable2=request.getCall_2();
        netRequestUtil(context);
    }
    public void netRequestUtil(final Activity context){
        observable1.subscribeOn(Schedulers.io())//初始被观察者切换到IO线程进行网络请求1
                .observeOn(AndroidSchedulers.mainThread())//新观察者切换到主线程，处理网络请求1的结果
                .doOnNext(new Consumer<Translation1>() {
                    @Override
                    public void accept(Translation1 translation1) throws Exception {
                        Toast.makeText(context,"第一次网络请求成功",Toast.LENGTH_SHORT).show();
                        translation1.show();
                    }
                }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG,"注册失败");
            }
        }).observeOn(Schedulers.io())       //（新被观察者，同时也是新观察者），切换到IO线程去发起登录请求
                                            //因为flatMap是对初始被观察者作变换，所以对于旧被观察者，他是新观察者，所以通过ObserveOn切换线程
                                            //但对于初始观察者，他则是新的被观察者
                .flatMap(new Function<Translation1, ObservableSource<Translation2>>() {//做变换，即作嵌套网络请求
                    @Override
                    public ObservableSource<Translation2> apply(Translation1 translation1) throws Exception {
                       //将网络请求1转换成网络请求2，即发送网络请求2
                        return observable2;
                    }
                }).observeOn(AndroidSchedulers.mainThread())//(初始观察者)切换到主线程处理网络请求2的结果
                    .subscribe(new Consumer<Translation2>() {
                        @Override
                        public void accept(Translation2 translation2) throws Exception {
                            Toast.makeText(context,"第二次网络请求成功",Toast.LENGTH_SHORT).show();
                            translation2.show();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            System.out.println("登录失败");
                        }
                    });
    }

}
