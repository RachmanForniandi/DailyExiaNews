package rachmanforniandi.com.dailyexianews;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String API_KEY ="7a3c63889ea440b5813ed9b60d974764";
    @BindView(R.id.list_item_news) RecyclerView listItemNews;
    @BindView(R.id.swipe_reload) SwipeRefreshLayout swipeReload;
    @BindView(R.id.txt_top_headlines)
    TextView txtTopHeadlines;
    private List<Article>articles = new ArrayList<>();
    private NewsAdapter newsAdapter;
    private ApiCommand apiCommand;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog loading;
    private String TAG = MainActivity.class.getSimpleName();
    String country,language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        country = Utils.getCountry();
        language =Utils.getLanguage();


        loading = new ProgressDialog(MainActivity.this);
        apiCommand = ApiCenter.obtainApiClient().create(ApiCommand.class);
        //newsAdapter = new NewsAdapter(articles,MainActivity.this);
        swipeReload.setOnRefreshListener(MainActivity.this);
        swipeReload.setColorSchemeResources(R.color.colorAccent);

        layoutManager = new LinearLayoutManager(MainActivity.this);

        listItemNews.setLayoutManager(layoutManager);
        listItemNews.setItemAnimator(new DefaultItemAnimator());
        listItemNews.setNestedScrollingEnabled(false);

        onLoadingSwipeRefresh("");
    }

    public void loadDataNews(String keyWord) {
        //String country = Utils.getCountry();
        loading = ProgressDialog.show(MainActivity.this, null, "Loading...",true,false);
        txtTopHeadlines.setVisibility(View.INVISIBLE);
        swipeReload.setRefreshing(true);
        Call<News>call;

        if (keyWord.length()>0){
            loading.dismiss();
            call = apiCommand.searchForNews(keyWord,language,"published At",API_KEY);
        }else {
            call= apiCommand.getNewsData(country,API_KEY);
        }
        call.enqueue(new Callback<News>() {
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

                    txtTopHeadlines.setVisibility(View.VISIBLE);
                    swipeReload.setRefreshing(false);
                }else {
                    txtTopHeadlines.setVisibility(View.INVISIBLE);
                    swipeReload.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "No news available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                txtTopHeadlines.setVisibility(View.INVISIBLE);
                swipeReload.setRefreshing(false);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        final SearchView searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search for the Latest News...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() >2){
                    onLoadingSwipeRefresh(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchMenuItem.getIcon().setVisible(false,false);

        return true;
    }

    @Override
    public void onRefresh() {
        loadDataNews("");
    }

    private void onLoadingSwipeRefresh(final String data){
        swipeReload.post(new Runnable() {
            @Override
            public void run() {
                loadDataNews(data);
            }
        });
    }
}
