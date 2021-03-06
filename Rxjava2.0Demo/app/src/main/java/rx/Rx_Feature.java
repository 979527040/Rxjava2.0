package rx;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.operators.observable.ObservableTake;
import mode.dexloaderclass.R;

/**
 * Created by 97952 on 2017/11/29.
 */
//TODO 功能操作符
public class Rx_Feature extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rx_retryWhen();
    }
/*
// 1. 指定延迟时间
// 参数1 = 时间；参数2 = 时间单位
   delay(long delay,TimeUnit unit)


// 2. 指定延迟时间 & 调度器
// 参数1 = 时间；参数2 = 时间单位；参数3 = 线程调度器
   delay(long delay,TimeUnit unit,mScheduler scheduler)

// 3. 指定延迟时间  & 错误延迟
// 错误延迟，即：若存在Error事件，则如常执行，执行后再抛出错误异常
// 参数1 = 时间；参数2 = 时间单位；参数3 = 错误延迟参数
   delay(long delay,TimeUnit unit,boolean delayError)

// 4. 指定延迟时间 & 调度器 & 错误延迟
// 参数1 = 时间；参数2 = 时间单位；参数3 = 线程调度器；参数4 = 错误延迟参数
   delay(long delay,TimeUnit unit,mScheduler scheduler,boolean delayError): 指定延迟多长时间并添加调度器，错误通知可以设置是否延迟*/
    //TODO 延迟操作
    private void rx_delay(){
        Observable.just(1,2,3)
                .delay(3, TimeUnit.SECONDS)//延迟3s再发送
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

    //TODO do操作符，在某个生命周期中调用
    private void rx_do(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new Throwable("发生错误了"));
            }
            //当Observable每发送1次数据事件就会调用1次
        }).doOnEach(new Consumer<Notification<Integer>>() {
            @Override
            public void accept(Notification<Integer> integerNotification) throws Exception {
                Log.e("TAG","doOnEach:"+integerNotification.getValue());
            }
            //执行Next事件前调用
        }).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TAG","doOnNext:"+integer);
            }
            //执行Next事件后调用
        }).doAfterNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e("TAG","doAfterNext:"+integer);
            }
            //Observable正常发送事件完毕后调用
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                Log.e("TAG","donOnComplete:");
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e("TAG","doOnError:"+throwable.getMessage());
            }
            //观察者订阅时调用
        }).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                Log.e("TAG","doOnSubscribe:");
            }
            //Observable发送事件完毕后调用，无论正常发送完毕/异常终止
        }).doAfterTerminate(new Action() {
            @Override
            public void run() throws Exception {
                Log.e("TAG","doAffterTerminate");
            }
            //最后执行
        }).doFinally(new Action() {
            @Override
            public void run() throws Exception {
                Log.e("TAG","doFinally");
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
    //TODO onErrorReturn()遇到错误时，发送1个特殊事件&正常终止
    private void rx_OnErrorReturn(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new Throwable("发生错误了"));
            }
        }).onErrorReturn(new Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable throwable) throws Exception {
                //捕捉错误异常
                Log.e("TAG","在onErrorReturn处理了错误："+throwable.toString());
                //发生错误事件后，发送一个666事件，最终正常结束
                return 666;
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
    //TODO onErrorResumeNext(),遇到错误时，发送1个新的Observable
    private void rx_OnErrorResumeNext(){
        //onErrorResumeNext()拦截的错误=Throwable;若需拦截Exception请用onExceptionResumeNext()
        //若onErrorResumeNext()拦截的错误=Exception,则会将错误传递给观察者的onError方法
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Throwable("发生错误了"));
            }
        }).onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> apply(Throwable throwable) throws Exception {
                //捕捉错误异常
                Log.e("TAG","在onErrorReturn处理了错误："+throwable.toString());
                //发生错误事件后，发送一个新的被观察者&发送事件序列
                return Observable.just(11,22);
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
    //TODO onExceptionResumeNext(),遇到错误时，发送1个新的Observable
    private void rx_onExceptionResumeNext(){
    // onExceptionResumeNext（）拦截的错误 = Exception；若需拦截Throwable请用onErrorResumeNext（）
    // 若onExceptionResumeNext（）拦截的错误 = Throwable，则会将错误传递给观察者的onError方法
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Exception("发生错误了"));
            }
        }).onExceptionResumeNext(new Observable<Integer>() {
            @Override
            protected void subscribeActual(Observer<? super Integer> observer) {
                observer.onNext(11);
                observer.onNext(12);
                observer.onComplete();
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

    //todo retry()重试，即当出现错误时，让被观察者重新发射数据
    private void rx_retry(){
        //接收到onError()时，重新订阅&发送事件
        //Throwable和Exception都可拦截
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Exception("发生错误了"));
                e.onNext(3);
            }
        }).retry()//遇到错误时，让被观察者重新发射数据(若一直错误，则一直重新发送)
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
        //TODO retry(long time)，作用：出现错误时，让观察者重新发送数据(具备重试次数限制)
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Exception("发生错误了"));
                e.onNext(3);
            }
        }).retry(3)//设置重试次数=3次
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
        //TODO retry(Predicate predicate),作用：出现错误后，判断是否需要重新发送数据，如果需要重新发送&持续遇到错误，则持续重试
        //TODO 参数=判断逻辑
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Exception("发生错误了"));
                e.onNext(3);
            }
            //拦截错误后，判断是否需要重新发送请求
        }).retry(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception {
                //捕获异常
                Log.e("TAG","retry错误："+throwable.toString());
                //返回false=不重新发送数据&调用观察者的OnError()结束
                //返回true=重新发送请求（若持续遇到错误，就持续重新发送）
                return true;
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
        //TODO retry(new BiPredicate<Integer,Throwable>
        //TODO 作用：出现错误后，判断是否需要重新发送数据，(若需要重新发送&持续遇到错误，则持续重试)
        //TODO 参数=判断逻辑（传入当前重试次数&异常错误信息）
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Exception("发生错误了"));
                e.onNext(3);
            }
            //拦截错误后，判断是否需要重新发送请求
        }).retry(new BiPredicate<Integer, Throwable>() {
            @Override
            public boolean test(Integer integer, Throwable throwable) throws Exception {
                //捕获异常
                Log.e("TAG","异常错误="+throwable.toString());
                //获取当前重试次数
                Log.e("TAG","当前重试次数"+integer);
                //返回false=不重新发送数据&调用观察者的onError结束
                //返回true=重新发送请求，若持续遇到错误，就持续重新发送
                return true;
            }}).subscribe(new Observer<Integer>() {
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
        //TODO 出现错误后，判断是否需要重新发送数据，具备重试次数限制
        //c参数=设置重置次数&判断逻辑
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Exception("发生错误了"));
                e.onNext(3);
            }
            //拦截错误后，判断是否需要重新发送请求
        }).retry(3, new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception {
                //捕获异常
                Log.e("TAG","retry错误："+throwable.toString());
                //返回false=不重新发送数据&调用观察者onError()结束
                //返回true=重新发送请求（最多重新发送3次）
                return true;
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

/*
  <-- 1. retry（） -->
// 作用：出现错误时，让被观察者重新发送数据
// 注：若一直错误，则一直重新发送

<-- 2. retry（long time） -->
// 作用：出现错误时，让被观察者重新发送数据（具备重试次数限制
// 参数 = 重试次数

<-- 3. retry（Predicate predicate） -->
// 作用：出现错误后，判断是否需要重新发送数据（若需要重新发送& 持续遇到错误，则持续重试）
// 参数 = 判断逻辑

<--  4. retry（new BiPredicate<Integer, Throwable>） -->
// 作用：出现错误后，判断是否需要重新发送数据（若需要重新发送 & 持续遇到错误，则持续重试
// 参数 =  判断逻辑（传入当前重试次数 & 异常错误信息）

<-- 5. retry（long time,Predicate predicate） -->
// 作用：出现错误后，判断是否需要重新发送数据（具备重试次数限制
// 参数 = 设置重试次数 & 判断逻辑*/
    //TODO retryWhen()遇到错误时，将发生的错误传递给一个新的被观察者（Observable）并决定是否需要重新订阅原始观察者&发送事件
    private void rx_retryWhen(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Exception("发生错误了"));
                e.onNext(3);
            }
        }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                // 参数Observable<Throwable>中的泛型 = 上游操作符抛出的异常，可通过该条件来判断异常的类型
                // 返回Observable<?> = 新的被观察者 Observable（任意类型）
                //此处有两种情况：
                //1.若新的被观察者Observable发送的事件=Error事件，那么原始Observable则不重新发送事件
                //2. 若新的被观察者 Observable发送的事件 = Next事件 ，那么原始的Observable则重新发送事件
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        //1.若返回的Observable发送的事件=Error事件，则原始的Observable不重新发送事件
                        //该异常错误信息可在观察者中的onError()中获得
                        return Observable.error(new Throwable("retryWhen终止啦"));
                        //2.若返回的Observable发送的事件=Next事件，则原始的Obsrvable重新发送事件（若持续遇到错误，则持续重试）
//                        return Observable.just(1);
                    }
                });

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
    //TODO  repeat()&repeatWhen()重复发送,重复不断地发送被观察者事件，可设置重复创建次数
    private void rx_repeat(){
        Observable.just(1,2,3,4)
                .repeat(3)//重复创建次数=3次
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
        //TODO repeatWhen(),有条件的重复的发送被观察者事件
        //将原始 Observable 停止发送事件的标识（Complete（） /  Error（））转换成1个 Object 类型数据传递给1个新被观察者（Observable），以此决定是否重新订阅 & 发送原来的 Observable
        //若新被观察者返回1个Complete/Error事件，则不重新订阅&发送原来的Observable
        //若新被观察者返回其余事件时，则重新订阅&发送原来的Observable
        Observable.just(1,2,4).repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            // 在Function函数中，必须对输入的 Observable<Object>进行处理，这里我们使用的是flatMap操作符接收上游的数据
            @Override
            public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                //将原始 Observable 停止发送事件的标识（Complete（） /  Error（））转换成1个 Object 类型数据传递给1个新被观察者（Observable）
                // 以此决定是否重新订阅 & 发送原来的 Observable
                //此处有两种情况
                //1.若新被观察者（Observable）返回1个Complete（） /  Error（）事件，则不重新订阅 & 发送原来的 Observable
                //2.若新被观察者（Observable）返回其余事件，则重新订阅 & 发送原来的 Observable
                return objectObservable.flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        // 情况1：若新被观察者（Observable）返回1个Complete（） /  Error（）事件，则不重新订阅 & 发送原来的 Observable
                        return Observable.empty();
                        // Observable.empty() = 发送Complete事件，但不会回调观察者的onComplete（）
                        // return Observable.error(new Throwable("不再重新订阅事件"));
                        // 返回Error事件 = 回调onError（）事件，并接收传过去的错误信息。

                        // 情况2：若新被观察者（Observable）返回其余事件，则重新订阅 & 发送原来的 Observable
                        // return Observable.just(1);
                        // 仅仅是作为1个触发重新订阅被观察者的通知，发送的是什么数据并不重要，只要不是Complete（） /  Error（）事件

                    }
                });
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

}
