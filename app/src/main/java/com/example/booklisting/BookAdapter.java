package com.example.booklisting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Activity context,ArrayList<Book>Books) {
        super(context,0,Books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_items,parent,false
            );
        }
        Book currentBook = getItem(position);

        TextView titleName = listItemView.findViewById(R.id.title);
        titleName.setText(currentBook.getTitle());

        TextView authorName = listItemView.findViewById(R.id.author);
        authorName.setText(currentBook.getAuthor());

        ImageView image= listItemView.findViewById(R.id.book_image);
        Glide.with(listItemView).load(currentBook.getImage()).into(image);

        TextView currencyType = listItemView.findViewById(R.id.currency_type);
        currencyType.setText(currentBook.getCurrency());

        TextView price = listItemView.findViewById(R.id.book_price);
        price.setText(currentBook.getPrice());
        
        return listItemView;

    }
}
