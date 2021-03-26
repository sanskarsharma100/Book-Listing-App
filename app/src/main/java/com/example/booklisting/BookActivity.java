package com.example.booklisting;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {

    private BookAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private View loadingIndicator;
    private SearchView mSearchViewField;

    private static String BOOK_REQUEST_URL;

    private static final int BOOK_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_activity);

        ConnectivityManager cm=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        ListView bookListView = findViewById(R.id.list_view);

        mAdapter = new BookAdapter(this, new ArrayList<>());

        bookListView.setAdapter(mAdapter);

        mEmptyStateTextView = findViewById(R.id.emptyView);
        bookListView.setEmptyView(mEmptyStateTextView);

        loadingIndicator = findViewById(R.id.loading_spinner);

        Button mSearchButton = findViewById(R.id.search_button);

        mSearchViewField = findViewById(R.id.search_bar);
        mSearchViewField.onActionViewExpanded();
        mSearchViewField.setIconified(true);
        mSearchViewField.setQueryHint("Enter a book title");

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkConnection(cm)) {

                    updateQueryUrl(mSearchViewField.getQuery().toString());
                    restartLoader();


                } else {

                    mAdapter.clear();

                    loadingIndicator.setVisibility(View.GONE);

                    mEmptyStateTextView.setText(R.string.no_connection);
                }

            }
        });

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book currentBook=mAdapter.getItem(position);

                Uri bookUri=Uri.parse(currentBook.getUrl());

                Intent webIntent=new Intent(Intent.ACTION_VIEW, bookUri);

                startActivity(webIntent);
            }
        });

        if(checkConnection(cm)) {

            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText(R.string.no_connection);
        }
    }

    private boolean checkConnection(ConnectivityManager cm) {
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void updateQueryUrl(String searchValue) {
        if(searchValue.contains(" ")) {
            searchValue = searchValue.replace(" ", "+");
        }

        BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=" + searchValue + "&filter=paid-ebooks&maxResults=40";
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new BookLoader(this, BOOK_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.books_not_found);

        mAdapter.clear();

        if(books!=null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    public void restartLoader() {
        mEmptyStateTextView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(BOOK_LOADER_ID, null, BookActivity.this);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }


}