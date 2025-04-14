package com.example.newsnow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsnow.api.NewsApiService;
import com.example.newsnow.model.NewsResponse;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String API_KEY = "3f5ce6caea384c049cccd660e9069ff4";
    private static final String BASE_URL = "https://newsapi.org/";
    
    private RecyclerView recyclerView;
    private List<com.example.newsnow.model.Article> articleList = new ArrayList<>();
    private NewsRecyclerAdapter adapter;
    private LinearProgressIndicator progressIndicator;
    private NewsApiService newsApiService;
    private SearchView searchView;
    private ChipGroup categoryChipGroup;
    private String currentCategory = "general";
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        setupRetrofit();
        setupSearchView();
        setupCategoryChips();
        getNews();
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.news_recycler_view);
        progressIndicator = findViewById(R.id.progress_bar);
        searchView = findViewById(R.id.searchView);
        categoryChipGroup = findViewById(R.id.categoryChipGroup);
        
        adapter = new NewsRecyclerAdapter(articleList, article -> {
            Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);
            intent.putExtra(NewsDetailActivity.EXTRA_NEWS_URL, article.getUrl());
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                getNews();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupCategoryChips() {
        categoryChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != View.NO_ID) {
                Chip chip = findViewById(checkedId);
                currentCategory = chip.getText().toString().toLowerCase();
                getNews();
            }
        });
    }

    private void setupRetrofit() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        newsApiService = retrofit.create(NewsApiService.class);
    }

    private void getNews() {
        progressIndicator.setVisibility(View.VISIBLE);
        
        newsApiService.getTopHeadlines(API_KEY, "en", currentCategory, currentQuery)
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        progressIndicator.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            articleList.clear();
                            articleList.addAll(response.body().getArticles());
                            adapter.notifyDataSetChanged();
                        } else {
                            showError("Error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        progressIndicator.setVisibility(View.GONE);
                        showError("Error: " + t.getMessage());
                    }
                });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}