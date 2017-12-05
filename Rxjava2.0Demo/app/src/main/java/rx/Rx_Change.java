package rx;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import mode.dexloaderclass.R;

/**
 * Created by 97952 on 2017/11/29.
 */
//TODO Rxjava变换操作符
public class Rx_Change extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rx_Buffer();
    }
    //TODO map()对被观察者发送的每1个事件都通过指定的函数处理，从而变换成另外一种事件
    //如下是对事件的参数从整形变换成字符串类型
    private void rx_map(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
            }
            //使用map变换操作符中的Function函数对被观察者发送的事件进行统一变换，整形变换成字符串类型
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "使用Map变换操作符将事件"+integer+"的参数从整形"+integer+"变换成字符串类型"+integer;
            }
        }).subscribe(new Consumer<String>() {
            //观察者接收事件时，是接收到变换后的事件==字符串类型
            @Override
            public void accept(String s) throws Exception {
                Log.e(":",s);
            }
        });
    }
    //TODO 将被观察者发送的事件序列进行拆分&单独转换，再合并成一个新的事件序列，最后再进行发送
    //为事件序列中每个事件都创建一个Observable对象
    //将对每个原始事件转换后的新事件都放入到对应的Observable对象
    //将新建的每个Observable都合并到一个新建的，总的Observable对象
    //新建的，总的Observable对象将新合并的事件序列发送给观察者Observer
    //应用场景：无序的将被观察者发送的整个事件序列进行变换
    private void rx_Flatmap(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
            }
            //采用flatMap()变换操作符
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                final List<String> list=new ArrayList<String>();
                for (int i = 0; i < 3; i++) {
                    list.add("我是事件"+integer+"拆分后的子事件"+i);
                    //通过flatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为 一个新的发送三个String事件
                    //最终合并，在发送给观察者
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                //新合并生成的事件序列顺序是无序的，即与旧序列发送事件的顺序无关
                Log.e("",s);
            }
        });
    }
    //TODO ConcatMap()操作符和flatMap()操作符类似
    //TODO 区别在于拆分&重新合并生成的事件序列的顺序=被观察者旧序列生产的顺序
    //应用场景：有序的将被观察者发送的整个事件序列进行变换
    private void rx_ContcatMap(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                final List<String> list=new ArrayList<String>();
                for (int i = 0; i < 3; i++) {
                    list.add("我是事件"+integer+"拆分后的子事件"+i);
                    //通过concatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String的事件
                    //最终合并，再发送给观察者
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                //新合并生成的事件序列顺序是有序的，即严格按照旧序列发送事件的顺序
                Log.e("",s);
            }
        });
    }
    //TODO Buffer(),定期从被观察者需要发送的事件中获取一定数量的事件&放到缓存中，最终发送
    //应用场景：缓存被观察者发送的事件
    private void rx_Buffer(){
        Observable.just(1,2,3,4,5)
                .buffer(3,2)//设置缓存区大小&步长,缓存区大小=每次从被观察者中获取的事件数量，步长=每次获取新事件的数量
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Integer> value) {
                        Log.e("Tag","缓存区里的事件数量="+value.size());
                        for(Integer i:value){
                            Log.e("","事件="+value);
                        }
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
