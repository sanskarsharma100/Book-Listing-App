package com.example.booklisting;

public class Book {
    private final String mTitle;
    private final String mAuthor;
    private final String mUrl;
    private final String mImage;
    private final String mCurrency;
    private final String mPrice;

    public Book(String title,String author,String url, String image, String currency, String price) {
        mTitle=title;
        mAuthor=author;
        mUrl=url;
        mImage = image;
        mCurrency=currency;
        mPrice = price;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getImage() {
        return mImage;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public String getPrice() {
        return mPrice;
    }

}
