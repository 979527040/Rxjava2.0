package rx_retrofit_RepeatWhen.mode;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

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
import rx_retrofit_RepeatWhen.bean.Translation;

/**
 * Created by 97952 on 2017/12/5.
 */
//TODO 有条件的轮询
public class Rxjava_Retrofit {
    Observable<Translation> observable;
    GetRequest_Interface request;
    int i = 0;

    public void initRetrofit(Activity context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/")
                .addConverterFactory(GsonConverterFactory.create())//使用Gson解析
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//设置支持Rxjava
                .build();
        //创建网络请求接口实例
        request = retrofit.create(GetRequest_Interface.class);
        //采用Observable对网络请求接口进行封装
        observable = request.getCall();
        requestNet(context);
    }

    public void requestNet(final Activity context) {
        //发送网络请求通过repeatWhen进行轮询
        //在Function函数中，必须对输入的Observable<Object>进行处理，此处用flatMap操作符接收上游的数据
        observable.repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                //将原始Observable停止发送时间的标识（Complete()/Error()）转换成1个Object类型数据传递给1个被观察者Observable
                //以此决定是否重新订阅&发送原来的Observable,即轮询
                //此处有两种情况
                //1.若返回1个Complete()/Error()事件，则不重新订阅&发送原来的Observable,即轮询结束
                //2.若返回其余事件，则重新订阅&发送原来的Observable，即继续轮询
                return objectObservable.flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        //加入判断条件：当轮询次数=5次后，停止轮询
                        if (i > 3) {
                            //此处选择发送OnError事件以结束轮询，因此可触发下游观察者的onError()方法回调
                            Toast.makeText(context,"轮询结束",Toast.LENGTH_SHORT).show();
                            return Observable.error(new Throwable("轮询结束"));
                        }
                        //若轮询次数<4次，则发送Next事件以继续轮询
                        //注：此处加入了delay操作符，作用=延迟一段时间发送(2s)，实现轮询间间隔设置
                        return Observable.just(1).delay(2000, TimeUnit.MILLISECONDS);
                    }
                });
            }
        }).subscribeOn(Schedulers.io())//切换到io线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread()) //切换回到主线程，处理请求结果
                .subscribe(new Observer<Translation>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Translation value) {
                        //接收服务器返回的数据
                        Log.e("TAG","接收到的数据"+value);
                        Toast.makeText(context,"第"+i+"次轮询",Toast.LENGTH_SHORT).show();
                        i++;
                    }

                    @Override
                    public void onError(Throwable e) {
                        //获取轮询结束信息
                        Log.e("TAG", e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
