package rx.zhi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import mode.dexloaderclass.R;

/**
 * Created by Administrator on 2017/10/30.
 */

public class ZipRx extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zipRx();
    }
    //TODO zip通过一个函数将多个Observable发送的事件结合到一起（多根水管像下游发送数据），然后按照顺序发送给下游，它只发射与发射数据项最少的那个Observable一样多的数据。
    //TODo 最终下游收到的事件数量 是和上游中发送事件最少的那一根水管的事件数量 相同
    private void zipRx(){
        Observable<Integer> observable1=Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());//TODO 如果不加这行代码，那么多个Observable(水管)将会在同一个线程中向下游发送数据,顺序将会乱
        Observable<String> observable2=Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("第一");
                e.onNext("第二");
                e.onNext("第三");
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable2, observable1, new BiFunction<String,Integer, String>() {
            @Override
            public String apply(String s,Integer integer) throws Exception {
                String acceptStr=integer+s;
                return acceptStr;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e("接收：", s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e("异常",throwable.toString());
            }
        });

    }
}
