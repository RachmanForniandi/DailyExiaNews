package rachmanforniandi.com.dailyexianews.ApiUtility;

import rachmanforniandi.com.dailyexianews.Models.News;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiCommand {

    @GET("top-headlines")
    Call<News> getNewsData(
            @Query("country") String country,
            @Query("apiKey")String apiKey
    );
}
