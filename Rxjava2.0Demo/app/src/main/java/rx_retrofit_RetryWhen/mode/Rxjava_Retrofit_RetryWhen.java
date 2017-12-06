package rx_retrofit_RetryWhen.mode;

import android.app.Activity;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx_retrofit_RepeatWhen.Interface.GetRequest_Interface;
import rx_retrofit_RetryWhen.bean.Translation;

/**
 * Created by 97952 on 2017/12/5.
 */

public class Rxjava_Retrofit_RetryWhen {
    private static final String TAG="Rxjava";
    //设置变量
    //可重试次数
    private int maxConnectCount=10;
    //当前已重试次数
    private int currentRetryCount=0;
    //重试等待时间
    private int waitRetryTime=0;
    private rx_retrofit_RetryWhen.Interface.GetRequest_Interface request;
    private Observable<rx_retrofit_RepeatWhen.bean.Translation> observable;

    public void initRetrofit(Activity context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/")
                .addConverterFactory(GsonConverterFactory.create())//使用Gson解析
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//设置支持Rxjava
                .build();
        //创建网络请求接口实例
        request = retrofit.create(rx_retrofit_RetryWhen.Interface.GetRequest_Interface.class);
        //采用Observable对网络请求接口进行封装
        observable = request.getCall();
        requestRetryWhen(context);
    }
    //发送网络请求&通过retryWhen()进行重试，主要异常才会回调retryWhen()进行重试
    private void requestRetryWhen(Activity context){
        observable.retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                //参数Observable<Throwable>中的泛型=上游操作符抛出的异常，可通过该条件来判断异常的类型
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        //输出异常信息
                        Log.e(TAG,"发生异常="+throwable.toString());
                        //根据异常信息选择是否选择重试
                        //即当发生的异常=网络异常=IO异常，才选择重试
                        if(throwable instanceof IOException){
                            //io异常，需要重试
                            //限制重试次数，即当已重试次数<设置的重试次数，才选择重试
                            if(currentRetryCount<maxConnectCount){
                                //记录重试次数
                                currentRetryCount++;
                                Log.e(TAG,"重试次数="+currentRetryCount);
                                //实现重试
                                //通过返回的Observable发送的事件=Next事件，从而使得retryWhen()重订阅，最终实现重试功能
                                //延迟1短时间再重试
                                //采用delay操作符=延迟一段时间再发送，以实现重试间间隔时间
                                //遇到的异常越多，时间越长
                                //在delay操作符的等待时间内设置=每重试1次，增多延迟重试时间1s
                                waitRetryTime=1000+currentRetryCount*1000;
                                return Observable.just(1).delay(waitRetryTime, TimeUnit.MILLISECONDS);
                            }else{
                                //若重试次数已>设置重试次数，则不重试
                                //通过发送error来停止重试(可在观察者的onError()中获取信息)
                                return Observable.error(new Throwable("重试次数已超过设置次数="+currentRetryCount+"即，不再重试"));
                            }
                        }else{
                            //若发生的异常不属于IO异常，则不重试
                            //通过返回的Observable发送的事件=Error事件实现（可在观察者的onError()中获取信息）
                            return Observable.error(new Throwable("发生了网络异常（非IO异常）"));
                        }
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<rx_retrofit_RepeatWhen.bean.Translation>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(rx_retrofit_RepeatWhen.bean.Translation value) {
                        //接收服务器返回的数据
                        Log.e(TAG,"发送成功");
                        value.show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        //获取停止重试的信息
                        Log.e(TAG,e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}





























