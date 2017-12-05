package rx_retrofit_Zip.Interface;

import io.reactivex.Observable;
import retrofit2.http.GET;
import rx_retrofit_Zip.bean.Translation1;
import rx_retrofit_Zip.bean.Translation2;

/**
 * Created by 97952 on 2017/12/5.
 */

public interface GetRequest_Interface {
    //网络请求1
    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20register")
    Observable<Translation1> getCall_1();
    //网络请求2
    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20login")
    Observable<Translation2> getCall_2();
    //getCall()是接收网络请求数据的方法
}
