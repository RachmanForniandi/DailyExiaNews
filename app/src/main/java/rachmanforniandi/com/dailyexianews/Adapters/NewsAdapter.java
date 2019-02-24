package rachmanforniandi.com.dailyexianews.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.internal.Util;
import rachmanforniandi.com.dailyexianews.Models.Article;
import rachmanforniandi.com.dailyexianews.R;
import rachmanforniandi.com.dailyexianews.supportUtils.Utils;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {

    private List<Article> articles;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public NewsAdapter(List<Article> articles, Context context) {
        this.articles = articles;
        this.context = context;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public NewsHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news,parent,false);
        return new NewsHolder(view,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(NewsHolder newsHolder, int position) {
        final NewsHolder holder = newsHolder;
        Article model = articles.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(context)
                .load(model.getUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressLoadingImg.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressLoadingImg.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imgPict);

        holder.titleNews.setText(""+ model.getTitle());
        holder.descNews.setText(""+ model.getDescription());
        holder.sourceNews.setText(""+ model.getSource().getName());
        holder.txtTime.setText(" \u2022 " +Utils.DateToTimeFormat(model.getPublishedAt()));
        holder.publishDate.setText(""+Utils.DateFormat(model.getPublishedAt()));
        holder.txtAuthorNews.setText(""+model.getAuthor());

    }

    public class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.img_pict)ImageView imgPict;
        @BindView(R.id.title_news)TextView titleNews;
        @BindView(R.id.desc_news)TextView descNews;
        @BindView(R.id.txt_author_news) TextView txtAuthorNews;
        @BindView(R.id.publish_date)TextView publishDate;
        @BindView(R.id.source_news)TextView sourceNews;
        @BindView(R.id.txt_time)TextView txtTime;
        @BindView(R.id.progress_loading_img) ProgressBar progressLoadingImg;
        OnItemClickListener onItemClickListener;

        public NewsHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v,getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
}
