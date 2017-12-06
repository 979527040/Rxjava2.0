package rx_retrofit_RetryWhen.Interface;

import io.reactivex.Observable;
import retrofit2.http.GET;
import rx_retrofit_RepeatWhen.bean.Translation;

/**
 * Created by 97952 on 2017/12/5.
 */

public interface GetRequest_Interface {
    // 采用Observable<...>接口
    // getCall()是接受网络请求数据的方法
    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20world")
    Observable<Translation>getCall();
}
