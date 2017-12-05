package rx;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import mode.dexloaderclass.R;

/**
 * Created by 97952 on 2017/11/29.
 */
//TODO 合并操作符
public class Rx_Merge extends Activity {
    //组合多个被观察者&合并
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rx_Collect();
    }

    //TODO concat()/concatArray()
    //TODO 组合多个被观察者一起发送数据，合并后按发送顺序串行执行
    //两者区别：组合被观察者的数量，即concat()组合被观察者数量<=4个，而concatArray()则可>4个
    private void rx_Concat() {
        Observable.concat(Observable.just(1, 2, 3),
                Observable.just(4, 5, 6),
                Observable.just(7, 8, 9),
                Observable.just(10, 11, 12))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        //组合多个被观察者一起发送数据（可>4个）
        Observable.concatArray(Observable.just(1, 2, 3),
                Observable.just(4, 5, 6),
                Observable.just(7, 8, 9),
                Observable.just(10, 11, 12),
                Observable.just(13, 14, 15))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //TODO merge()/mergeArray(),组合多个被观察者一起发送数据，合并后按照时间线并行执行
    //TODO 两者区别：组合被观察者的数量，即merge()组合被观察者数量<=4个，而mergeArray()则可>4个
    //区别上述concat()操作符，同样是组合多个被观察者一起发送数据，但concat()操作符合并后是按发送顺序串行执行
    private void rx_merge() {
        Observable.merge(Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS),//从0开始发送，共发送3个数据，第1次事件延迟发送时间=1s,间隔时间=1s
                Observable.intervalRange(2, 3, 1, 1, TimeUnit.SECONDS)// 从2开始发送、共发送3个数据、第1次事件延迟发送时间 = 1s、间隔时间 = 1s
        ).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long value) {
                Log.e("Tag", value + "");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        //mergeArray()=组合4个以上的被观察者一起发送数据，此处不再演示
    }

    //TODO concatDelayError()/mergeDelayError()
    //TODO 使用concat()和merge()操作符时，如果多个被观察者其中一个被观察者发出onError事件，则会终止其他被观察者继续发送事件，如果希望onError事件推迟到其他被观察者发送事件结束后才触发就需要用到concatDelayError()和mergeDelayError()
    private void rx_concatDelayError() {
        Observable.concatArrayDelayError(Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new NullPointerException());//发送Error事件，因为使用了concatDelayError，所以第2个Observable将会发送事件，等发送完毕后，再发送错误事件
                e.onComplete();
            }
        }), Observable.just(4, 5, 6)).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer value) {
                Log.e("TAG", "接收到了事件" + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG", "我是Error");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    //TODO zip() 合并多个事件，该类型的操作符主要是对多个被观察者中的事件进行合并处理
    //事件组合方式=严格按照原先事件序列进行对位合并
    //最终合并的事件数量=多个被观察者中数量最少的数量
    private void rx_Zip() {
        //创建被观察者1
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.e("TAG", "被观察者1发送了事件1");
                e.onNext(1);
                Thread.sleep(2000);
                Log.e("TAG", "被观察者1发送了事件2");
                e.onNext(2);
                Thread.sleep(1000);
                Log.e("TAG", "被观察者1发送了事件3");
                e.onNext(3);
                Thread.sleep(1000);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());//设置被观察者1在工作线程1中进行
        //创建被观察者2
        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                Log.e("TAG", "被观察者2发送了事件A");
                e.onNext("A");
                Thread.sleep(1000);
                Log.e("TAG", "被观察者2发送了事件B");
                e.onNext("B");
                Thread.sleep(1000);
                Log.e("TAG", "被观察者2发送了事件C");
                e.onNext("C");
                Thread.sleep(1000);
                Log.e("TAG", "被观察者2发送了事件D");
                e.onNext("D");
                Thread.sleep(1000);
                e.onComplete();
            }

        }).subscribeOn(Schedulers.newThread());//设置被观察者2在工作线程2中工作
        // 假设不作线程控制，则该两个被观察者会在同一个线程中工作，即发送事件存在先后顺序，而不是同时发送
        //使用zip变换操作符进行事件合并
        //创建BiFunction对象传入的第3个参数=合并后数据的数据类型
        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String value) {
                Log.e("TAG", "最终接收到的事件 =  " + value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    //TODO combineLatest()
    //TODO 当两个Observables中的任何一个发送了数据后，将先发送了数据的Observables的最新（最后）一个数据与另外一个Observable发送的每个数据结合，最终基于该函数的结果发送数据
    //与zip()的区别，zip()=按个数合并，即1对1合并，combinelatest()=按时间合并，即在同一个时间点上合并
    private void rx_CombineLatest() {
        Observable.combineLatest(
                Observable.just(1L, 2L, 3L),//第1个发送数据事件的Observable
                Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS),//第2个发送数据事件的Observable,从0开始发送，共发送3个数据，第1次事件延迟发送1s,间隔发送时间1s
                new BiFunction<Long, Long, Long>() {
                    @Override
                    public Long apply(Long aLong, Long aLong2) throws Exception {
                        //aLong=第1个Observable发送的最新（最后）1个数据
                        //aLong2=第2个Observable发送的每1个数据
                        Log.e("TAG", "合并的第一个数据是：" + aLong + "合并的第二个数据是：" + aLong2);
                        return aLong + aLong2;
                        //合并的逻辑=相加
                        //即第1个Observabel发送的最后1个数据与第2个Observable发送的每1个数据进行相加
                    }
                }).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.e("TAG", "合并的结果是： " + aLong);
            }
        });
    }
    //TODO reduce(),把被观察者需要发送的事件聚合成1个事件&发送
    //聚合的逻辑根据需求撰写，但本质都是前两个数据聚合，然后与后1个数据继续进行聚合，依次类推
    private void rx_Reduce(){
        Observable.just(1,2,3,4)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    //在该方法中复写聚合的逻辑
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        Log.e("TAG","本次计算的数据是："+integer+"乘"+integer2);
                        return integer*integer2;
                        //本次聚合的逻辑是：全部数据相乘起来
                        //原理：第一次取前两个数据相乘，之后每次获取到的数据=返回的数据*原始下1个数据
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e("TAG","最终计算结果是："+integer);
            }
        });
    }
    //TODO collect()，被观察者Observable发送的数据事件收集到一个数据结构里
    private void rx_Collect(){
        Observable.just(1,2,3,4,5,6)
                //创建数据结构，用于收集被观察者发送的数据
                .collect(new Callable<ArrayList<Integer>>() {
                    @Override
                    public ArrayList<Integer> call() throws Exception {
                        return new ArrayList<Integer>();
                    }
                    //对发送的数据进行收集
                }, new BiConsumer<ArrayList<Integer>, Integer>() {
                    @Override
                    public void accept(ArrayList<Integer> list, Integer integer) throws Exception {
                        //list=容器，integer=后者数据
                        list.add(integer);
                    }
                }).subscribe(new Consumer<ArrayList<Integer>>() {
            @Override
            public void accept(ArrayList<Integer> integers) throws Exception {
                Log.e("TAG","本次发送的数据是："+integers);
            }
        });
    }
    //TODO startWith()/startWithArray()
    //TODO 在一个被观察者发送事件前，追加发送一些数据、一个新的被观察者
    private void rx_StartWith(){
        //追加数据顺序=后调用先追加
        Observable.just(4,5,6)
                .startWith(0)//追加单个数据=startWith()
                .startWithArray(1,2,3)//追加多个数据=startWithArray()
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        //TODO 在一个被观察者发送事件前，追加发送被观察者&发送数据
        Observable.just(4,5,6)
                .startWith(Observable.just(1,2,3))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    //TODO 统计发送事件数量
    private void rx_count(){
        Observable.just(1,2,3,4)
                .count()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e("TAG","发送事件数量"+aLong);
                    }
                });
    }

}
