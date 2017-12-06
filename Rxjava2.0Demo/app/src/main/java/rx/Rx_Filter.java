package rx;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by 97952 on 2017/12/6.
 */
//TODO 过滤操作符
public class Rx_Filter {
    public static final String TAG="rxjava";
    //TODO 过滤操作符Filter
    private void rx_Filter(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                //发送5个事件
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                e.onNext(5);
            }
            //采用filter()变换操作符
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                //根据test的返回值对被观察者发送的事件进行过滤&筛选
                //返回true则继续发送
                //返回false则不发送(即过滤)
                return integer>3;
                //本例子=过滤了整数<=3的事件
            }
        }).subscribe(new Observer<Integer>() {
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
    //TODO 过滤特定数据类型的数据ofType()
    private void rx_OfType(){
        Observable.just(1,"Zhu","3",4)
                .ofType(Integer.class)//筛选出整形数据
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG,"获取到的整形事件元素是："+integer);
                    }
                });
    }
    //TODO 跳过某个事件skip()/skipLast()
    private void rx_Skip(){
        Observable.just(1,2,3,4,5)
                .skip(1)//跳过正序的前1项
                .skipLast(2)//跳过正序的后2项
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG,"获取到的整形事件元素是："+integer);
                    }
                });
        //根据时间跳过数据项
        //发送事件特点：发送数据0-5，每隔1s发送一次，每次递增1，第1次发送延迟0s
        Observable.intervalRange(0,5,0,1, TimeUnit.SECONDS)
                .skip(1,TimeUnit.SECONDS)//跳过第1s发送的数据
                .skipLast(1,TimeUnit.SECONDS)//跳过最后1秒发送的数据
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG,"获取到的整形事件元素是："+aLong);
                    }
                });
    }
    //TODO distinct()/distinctUntilChanged 过滤事件序列中重复的事件、连续重复的事件
    private void rx_Distinct(){
        Observable.just(1,2,3,1,2)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG,"不重复的整形事件元素是："+integer);
                    }
                });
        //过滤事件序列中，连续重复的事件
        //下面序列中，连续重复的事件=3,4
        Observable.just(1,2,3,1,2,3,3,4,4)
                .distinctUntilChanged()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG,"不连续重复的整形事件元素是："+integer);
                    }
                });
    }
    //TODO 通过设置指定的事件数量，仅发送特定数量的事件 take()&takeLast()
    private void rx_Take(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                e.onNext(5);
            }
        }).take(2)//采用take()变换操作符，指定了观察者只能接收2个事件
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.e(TAG,"过滤后得到的事件是："+value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    //TODO takeLast()指定观察者只能接收到被观察者发送的最后几个事件
    private void rx_TakeLast(){
        Observable.just(1,2,3,4,5)
                .takeLast(3)//指定观察者只能接收到被观察者发送的后3个事件
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.e(TAG,"过滤后得到的事件是："+value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    //TODO throttleFirst()/throttleLast()，在某段时间内，只发送该段时间内第1次事件/最后1次事件
    //如1段时间内连续点击按钮，但只执行第一次的点击操作
    private void rx_ThrottleFirst(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                //隔断事件发送时间
                e.onNext(1);
                Thread.sleep(500);
                e.onNext(2);
                Thread.sleep(400);
                e.onNext(3);
                Thread.sleep(300);
                e.onNext(4);
                Thread.sleep(300);
                e.onNext(5);
                Thread.sleep(400);
                e.onNext(6);
                Thread.sleep(300);
                e.onNext(7);
                Thread.sleep(300);
                e.onNext(8);
                Thread.sleep(300);
                e.onComplete();
            }
        }).throttleFirst(1,TimeUnit.SECONDS)//每1秒钟采用数据
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.e(TAG,"接收到了事件："+value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //在某段时间内，只发送该段时间内最后1次事件
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                //隔段事件发送时间
                e.onNext(1);
                Thread.sleep(500);

                e.onNext(2);
                Thread.sleep(400);

                e.onNext(3);
                Thread.sleep(300);

                e.onNext(4);
                Thread.sleep(300);

                e.onNext(5);
                Thread.sleep(300);

                e.onNext(6);
                Thread.sleep(400);

                e.onNext(7);
                Thread.sleep(300);
                e.onNext(8);

                Thread.sleep(300);
                e.onNext(9);

                Thread.sleep(300);
                e.onComplete();
            }
        }).throttleLast(1,TimeUnit.SECONDS)//每1秒钟采用数据
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
    //TODO Sample()在某段时间内，只发送该段时间内最新（最后）1次事件，与throttleLast()操作符类似
    //TODO throttleWithTimeOut()/debounce()发送数据事件时，若2次发送事件的间隔<指定时间，就会丢弃前一次的数据，直到指定时间内都没有数据发射时才会发送最后一次的数据
    private void rx_ThrottleWithTimeOut(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                // 隔段事件发送时间
                e.onNext(1);
                Thread.sleep(500);
                e.onNext(2); // 1和2之间的间隔小于指定时间1s，所以前1次数据（1）会被抛弃，2会被保留
                Thread.sleep(1500);  // 因为2和3之间的间隔大于指定时间1s，所以之前被保留的2事件将发出
                e.onNext(3);
                Thread.sleep(1500);  // 因为3和4之间的间隔大于指定时间1s，所以3事件将发出
                e.onNext(4);
                Thread.sleep(500); // 因为4和5之间的间隔小于指定时间1s，所以前1次数据（4）会被抛弃，5会被保留
                e.onNext(5);
                Thread.sleep(500); // 因为5和6之间的间隔小于指定时间1s，所以前1次数据（5）会被抛弃，6会被保留
                e.onNext(6);
                Thread.sleep(1500); // 因为6和Complete实践之间的间隔大于指定时间1s，所以之前被保留的6事件将发出

                e.onComplete();
            }
        }).throttleWithTimeout(1,TimeUnit.SECONDS)//每1秒钟采用数据
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.e(TAG,"接收到了事件"+value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //TODO 根据指定事件位置过滤事件，firstElement()/lastElement()，仅选取第1个元素/最后一个元素
    private void rx_FirstElement(){
        Observable.just(1,2,3,4,5)
                .firstElement()//仅选取第一个元素
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                    }
                });
        Observable.just(1,2,3,4,5)
                .lastElement()//仅选取最后一个数据
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                    }
                });
    }

    //TODO elementAt() 指定接收某个元素(通过索引值确定)
    private void rx_ElementAt(){
        //获取位置索引=2的元素
        //位置索引从0开始
        Observable.just(1,2,3,4,5)
                .elementAt(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG,"获取到的事件元素："+integer);
                    }
                });
        //获取的位置索引>发送事件序列长度时，设置默认参数
        Observable.just(1,2,3,4,5)
                .elementAt(6,10)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG,"获取到的事件元素是："+integer);
                    }
                });
    }

    //TODO elementAtOrError() 在elementAt()的基础上，当出现越界情况（即获取的位置索引>发送事件序列长度时就会抛出异常）
    private void rx_ElementAtOrError(){
        Observable.just(1,2,3,4,5)
                .elementAtOrError(6)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG,"获取到的事件元素是： "+ integer);
                    }
                });
    }



}
