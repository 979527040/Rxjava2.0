package rx;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import mode.dexloaderclass.R;

/**
 * Created by Administrator on 2017/10/30.
 */
//TODO 创建操作符
//TODO 上下游默认是在同一个线程工作
public class Rx_Create extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thread_Scudle();
    }

    //TODo 在主线程发送事件，在主线程接收事件
    private void thread_Run() {
        //TODO 上游创建发送事件
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            //TODO subscribe事件发射器，定义需要发送的事件，向观察者发送事件
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onComplete();
                Log.e("上游发送事件所在线程：", Thread.currentThread().getName());
            }
        });

        //TODO 下游接收事件
        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e("下游接收事件所在线程：", Thread.currentThread().getName() + "接收到的值：" + integer);
            }
        };
        //TODO 当Observable被订阅时，OnSubscribe的call方法会自动被调用，即事件序列就会按照设定依次触发
        //TODO 即观察者会依次调用对应事件的复写方法从而响应事件
        //TODO 从而实现被观察者调用了观察者的回调方法，由被观察者向观察者的事件传递，即观察者模式
        observable.subscribe(consumer);
    }


    //TODO 在子线程发送事件，在主线程接收事件
    private void thread_Scudle() {
        //TODO 上游创建发送事件
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                Log.e("上游发送事件所在线程：", Thread.currentThread().getName());
            }
        });

        //TODO 下游接收事件
        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e("下游接收事件所在线程：", Thread.currentThread().getName() + "接收到的值：" + integer);
            }
        };
            /*多次指定上游的线程只有第一次指定的有效, 也就是说多次调用subscribeOn() 只有第一次的有效, 其余的会被忽略.
              多次指定下游的线程是可以的, 也就是说每调用一次observeOn() , 下游的线程就会切换一次.*/
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    //TODO 扩展：Rxjava提供了其他方法用于创建被观察者对象Observable
    //TODO 方法1:just(T...):直接将传入的参数依次发送出来
    private void ObservableEmitter() {
        //just只能发送十个以内的参数
        Observable observable = Observable.just("A", "B", "c");
        // 将会依次调用：
        // onNext("A");
        // onNext("B");
        // onNext("C");
        // onCompleted();
    }

    //TODO 方法2：from(T[])/from(Iterable<? extends T>):将传入的数组/Iterable拆分成具体对象后，依次发送出来
    private void ObservableEmitter2() {
        String[] words = {"A", "B", "c"};
        //fromArray发送十个以上的参数
        Observable obserable = Observable.fromArray(words);
        // 将会依次调用：
        // onNext("A");
        // onNext("B");
        // onNext("C");
        // onCompleted();
    }

    //TODO fromIterable()
    //TODO 快速创建1个被观察者对象，直接发送传入的集合list数据，可发送10个以上事件（集合形式）
    //TODO 集合元素遍历
    private void ObservableListEmitter() {
        //快速发送集合
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        //通过fromIterable()将集合中的对象/数据发送出去
        Observable.fromIterable(list)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        //集合中的数据元素value
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        //遍历结束
                    }
                });
    }

/*// 下列方法一般用于测试使用

<-- empty()  -->
// 该方法创建的被观察者对象发送事件的特点：仅发送Complete事件，直接通知完成
    Observable observable1=Observable.empty();
// 即观察者接收后会直接调用onCompleted（）

<-- error()  -->
// 该方法创建的被观察者对象发送事件的特点：仅发送Error事件，直接通知异常
// 可自定义异常
    Observable observable2=Observable.error(new RuntimeException())
// 即观察者接收后会直接调用onError（）

 <-- never()  -->
// 该方法创建的被观察者对象发送事件的特点：不发送任何事件
   Observable observable3=Observable.never();
// 即观察者接收后什么都不调用*/

        //TODO defer()
        //TODO 直到有观察者Observer时，才动态创建被观察者对象Observable&发送事件
        //TODO 通过Observable工厂方法创建被观察者对象
        //TODO 每次订阅后，都会得到一个刚创建的最新的Observable对象，这可以确保Observable对象里的数据是最新的
        private void rx_defer(){
            //第一次赋值
             int i=10;
            //通过defer定义被观察者对象，此时被观察者对象还没有创建
            Observable<Integer> observable=Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
                @Override
                public ObservableSource<? extends Integer> call() throws Exception {
//                    return Observable.just(i);
                    return Observable.just(10);
                }
            });
            //第二次赋值
            //此时，才会调用defer(),创建被观察者对象Observable
            i=15;
            observable.subscribe(new Observer<Integer>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(Integer value) {
                    Log.e("","接收到的整数是"+value);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
            //订阅时才创建，所以会输出第二次的值15
        }
        //TODO 快速创建一个被观察者对象，延迟指定时间后，发送1个数值0，相当于延迟指定时间后，调用一次onNext(0)
        private void rx_timer(){
            //该例子是，延时2s后，发送一个long类型数值
            Observable.timer(2, TimeUnit.SECONDS)
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Long value) {
                            Log.e("","接收到了事件");
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
            //timer操作符默认运行在一个新的线程上，也可自定义线程调度器（第三个参数）timer(long,TimeUnit,Scheduler)
        }
        //TODO 快速创建1个被观察者对象，每隔指定时间就发送事件，发送的事件序列相当于从0开始，无限递增1的整数序列
        private void rx_Interval(){
            //该例子发送的事件序列特点：延迟3s后发送事件，每隔1秒产生1个数字，从0开始递增1，无限个
            Observable.interval(3,1,TimeUnit.SECONDS)
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Long value) {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
            //interval默认在computation调度器上执行
            //也可以自定义指定线程调度器（第三个参数）：interval(long,TimeUnit,Scheduler)
        }
        //TODO 快速创建1个被观察者对象，每隔指定时间就发送事件，可指定发送的数据的数量
        //Todo 发送的事件序列相当于从0开始，无限递增1的整数序列，作用类似于interval()，但可指定发送的数据的数量
        //参数1=事件序列起始点，参数2=事件数量，参数3=第1次事件延迟发送时间，参数4=间隔时间数字，参数5=时间单位
    private void rx_intervalRange(){
        //从3开始，一共发送10个事件
        //第一次延迟2秒发送，之后每隔2秒产生1个数字（从0开始递增1，无限个）
        Observable.intervalRange(3,10,2,1,TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    //TODO 快速创建一个被观察者对象，连续发送1个事件序列，可指定范围
    //TODO 发送事件序列从0开始，无限递增1的整数序列，作用类似intervalRange()但区别在于无延迟发送事件
    private void rx_Range(){
        //从3开始发送，每次发送事件递增1，一共发送10个事件，如果设置为负数，则会抛出异常
        Observable.range(3,10)
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

}
