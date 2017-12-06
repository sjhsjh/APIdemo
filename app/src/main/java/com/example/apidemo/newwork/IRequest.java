package com.example.apidemo.newwork;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by codemao123 on 17/12/6.
 * 不能混淆
 */
//http://api.accuweather.com/locations/v1/cn/search
// ?q=%E5%8C%97%E4%BA%AC&apikey=af7408e9f4d34fa6a411dd92028d4630
public interface IRequest {

    @GET("locations/v1/cn/search")
    Call<ResponseBody> getWeather(@Query("q") String queryStr, @Query("apikey") String apikey);

//    @GET("/locations/v1/cities/geoposition/search.json")
//    Call<LocationEntity> getCityByCoordinate(@QueryMap Map<String, String> paramMap);
//
//    @GET("/locations/v1/{locationKey}.json")
//    Call<LocationEntity> getCityByLocationKey(@Path("locationKey") String locationKey, @QueryMap Map<String, String> paramMap);

}
