package rx.zhi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import mode.dexloaderclass.R;

/**
 * Created by Administrator on 2017/10/30.
 */

public class MapRx extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flatMapUnicode();
    }
    //TODO map转换操作符
    private void mapUnicode(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
            }
        }).map(new Function<Integer,String>() {
            @Override
            public String apply(Integer o) throws Exception {
                return "更改输出的整数为字符串"+o;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e("接收：",s);

            }
        });
    }
    //TODo flatMap无序转换符
    private void flatMapUnicode(){
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                List<String> list=new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add(integer+"你好");
                }
                return Observable.fromIterable(list).timeout(3, TimeUnit.MILLISECONDS);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e("接收：",s);
            }
        });
    }
}
