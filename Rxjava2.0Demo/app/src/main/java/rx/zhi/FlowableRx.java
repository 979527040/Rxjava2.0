package rx.zhi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import mode.dexloaderclass.R;

/**
 * Created by Administrator on 2017/10/30.
 */
public class FlowableRx extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flowableRx();
    }

    private void flowableRx(){
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 5; i++) {
                    e.onNext(i);
                }
            }
            //TODO BackpressureStrategy背压，相当于水缸，BackpressureStrategy.DROP，就是直接把存不下的事件丢弃
            //TODO BackpressureStrategy.LATEST就是只保留最新的事件
            //TODO BackpressureStrategy.ERROR就是128，也就是上游最多发128条事件
            //TODO BackpressureStrategy.BUFFER没有大小限制
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        //TODO request是下游的处理能力，必须加上，否则不会接收上游事件
                        s.request(7);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e("接收：",integer+"");
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
