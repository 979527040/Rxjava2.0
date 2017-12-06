package main;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import mode.dexloaderclass.R;
import rx_retrofit_RepeatWhen.mode.Rxjava_Retrofit;
import rx_retrofit_RetryWhen.mode.Rxjava_Retrofit_RetryWhen;
import rx_retrofit_Zip.mode.Rx_Retrofit_Zip;
import rx_retrofit_recall.FlatMap.mode.NetRequest;

/**
 * Created by 97952 on 2017/12/5.
 */

public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_main);
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //有条件网络请求轮询
                new Rxjava_Retrofit().initRetrofit(MainActivity.this);
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //网络请求嵌套回调
                new NetRequest().initRetrofit(MainActivity.this);
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //合并数据源
                new Rx_Retrofit_Zip().initRetrofit(MainActivity.this);
            }
        });
        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Rxjava_Retrofit_RetryWhen().initRetrofit(MainActivity.this);
            }
        });
        //防抖操作
        RxView.clicks(findViewById(R.id.button7))
                .throttleFirst(2, TimeUnit.SECONDS)//发送2秒，第一次的点击事件
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object value) {
                        Toast.makeText(MainActivity.this, "网络请求", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
