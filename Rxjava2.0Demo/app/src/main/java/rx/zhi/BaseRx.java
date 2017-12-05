package rx.zhi;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.newp.IDynamic;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import mode.dexloaderclass.R;

public class BaseRx extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scheduMethod();
        rxBaseMethod();
    }
    //TODO 线程调度器
    private void scheduMethod(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.e("所在的线程",Thread.currentThread().getName());
                Log.e("发送到额数据",1+"");
                e.onNext(1);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        //TODO 如果subscribe只有一个参数那就代表下游只关心上游的onNext()方法
                        Log.e("所在的线程",Thread.currentThread().getName());
                        Log.e("接受到的数据","integer:"+integer);
                    }
                });
    }

    //TODO rxjava基础方法
    private void rxBaseMethod(){
        //TODO 上游
        Observable observable=Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        });
        //TODO 下游,下游的创建有两种方式
        //TODO 方法一：采用Observer接口
        Observer<Integer> observer=new Observer<Integer>(){
            private Disposable mDisposable;
            //TODO 观察者接收事件前，默认最先调用复写onSubscribe()
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable=d;
            }

            @Override
            public void onNext(Integer value) {
              Log.e("下游接收到的数据：",value+"");
              if(value==2){
                  //TODO 截断上游和下游，使下游无法接收到上游下发的值，但是上游(被观察者)依然可以继续发送事件
                  mDisposable.dispose();
              }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        //TODO 方法二:采用Subscriber抽象类,创建观察者Observer对象
        Subscriber<Integer> subscriber=new Subscriber<Integer>() {
            //TODO 观察者接收事件前，默认最先调用复写 onSubscribe（）
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(Integer integer) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };
        /*两种方式的区别，即Subscriber抽象类和Observer接口的区别*/
        //相同点：二者基本使用方式完全一致，实质上，Rxjava的subscribe过程中，Observer总是会先被转换成Subscriber再使用
        //不同点：Subscriber抽象类对Observer接口进行扩展，新增了两个方法：
        //1.onStart():在还未响应事件前调用，用于做一些初始化操作
        //2.unsubscribe():用于取消订阅，在该方法被调用后，观察者将不再接收响应事件
        //调用该方法之前，先使用isUnsubscribed()判断状态，确定被观察者Observable是否还持有观察者Subscribe的引用，如果引用不能及时释放，就会出现内存泄漏
        //TODO 将上游和下游链接起来，当Observable被订阅时，onSubscribe的call()方法会自动被调用，即事件序列就会依照设定依次被触发
        observable.subscribe(observer);
        //observable.subscribe(subscriber);
    }
    //TODO Rxjava基于事件流的链式调用
    private void rxBaseMethod2(){
       Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                //默认最先调用复写的onSubscribe()方法
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
    //TODO 简便式的观察者模式
    private void rxSimple(){
        //最多只能发送十个事件
        Observable.just("hellow").subscribe(new Consumer<String>() {
            //每次收到Observable的事件 都会调用Consumer.accept()
            @Override
            public void accept(String s) throws Exception {

            }
        });
    }
/*//    subscribe()有多个重载方法
    public final Disposable subscribe() {}
    // 表示观察者不对被观察者发送的事件作出任何响应（但被观察者还是可以继续发送事件）

    public final Disposable subscribe(Consumer<? super T> onNext) {}
    // 表示观察者只对被观察者发送的Next事件作出响应
    public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError) {}
    // 表示观察者只对被观察者发送的Next事件 & Error事件作出响应

    public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete) {}
    // 表示观察者只对被观察者发送的Next事件、Error事件 & Complete事件作出响应

    public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete, Consumer<? super Disposable> onSubscribe) {}
    // 表示观察者只对被观察者发送的Next事件、Error事件 、Complete事件 & onSubscribe事件作出响应

    public final void subscribe(Observer<? super T> observer) {}
    // 表示观察者对被观察者发送的任何事件都作出响应*/

}
