package rachmanforniandi.com.dailyexianews;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rachmanforniandi.com.dailyexianews.Adapters.NewsAdapter;
import rachmanforniandi.com.dailyexianews.ApiUtility.ApiCenter;
import rachmanforniandi.com.dailyexianews.ApiUtility.ApiCommand;
import rachmanforniandi.com.dailyexianews.Models.Article;
import rachmanforniandi.com.dailyexianews.Models.News;
import rachmanforniandi.com.dailyexianews.supportUtils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY ="7a3c63889ea440b5813ed9b60d974764";
    @BindView(R.id.list_item_news) RecyclerView listItemNews;
    private List<Article>articles = new ArrayList<>();
    private NewsAdapter newsAdapter;
    private ApiCommand apiCommand;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog loading;
    private String TAG = MainActivity.class.getSimpleName();
    String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        country = Utils.getCountry();

        loading = new ProgressDialog(MainActivity.this);
        apiCommand = ApiCenter.obtainApiClient().create(ApiCommand.class);
        //newsAdapter = new NewsAdapter(articles,MainActivity.this);

        layoutManager = new LinearLayoutManager(MainActivity.this);

        listItemNews.setLayoutManager(layoutManager);
        listItemNews.setItemAnimator(new DefaultItemAnimator());
        listItemNews.setNestedScrollingEnabled(false);

        loadDataNews();
    }

    public void loadDataNews() {
        //String country = Utils.getCountry();
        loading = ProgressDialog.show(MainActivity.this, null, "Loading...",true,false);
        apiCommand.getNewsData(country,API_KEY).enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                Log.e("_logResponse", response.toString() );
                if (response.isSuccessful() && response.body().getArticle() !=null){
                    loading.dismiss();
                    if (!articles.isEmpty()){
                        articles.clear();
                    }
                    articles = response.body().getArticle();
                    newsAdapter = new NewsAdapter(articles,MainActivity.this);
                    listItemNews.setAdapter(newsAdapter);
                    newsAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(MainActivity.this, "No news available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {

            }
        });
    }


}
