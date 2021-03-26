package com.example.booklisting;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    public static List<Book> fetchBookData(String requestUrl) {

        URL url=createUrl(requestUrl);

        String jsonResponse=null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG,"Problem making HTTP request ",e);
        }
        return extractFeatureFromJson(jsonResponse);
    }

    private static URL createUrl(String stringUrl) {
        URL url=null;
        try{
            url=new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG,"Problem building the URL ",e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse="";
        if(url==null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection=null;
        InputStream inputStream=null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode()==200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code" + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection!=null) {
                urlConnection.disconnect();
            }
            if (inputStream!=null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output= new StringBuilder();
        if(inputStream!=null) {
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line!=null) {
                output.append(line);
                line= reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<Book> extractFeatureFromJson(String bookJson) {

        if(TextUtils.isEmpty(bookJson)) {
            return null;
        }

        List<Book> books = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(bookJson);
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            for(int i=0;i<bookArray.length();i++)
            {
                JSONObject currentBook = bookArray.getJSONObject(i);
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                String title = volumeInfo.getString("title");

                String author;

                if(volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");

                    if(!volumeInfo.isNull("authors")) {
                        author = (String) authors.get(0);
                    } else {
                        author = "Unknown author";
                    }
                } else {
                    author = "Author info unavailable";
                }

                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");

                String coverImageUrl = imageLinks.getString("smallThumbnail");
                StringBuilder stringBuilder=new StringBuilder();

                Pattern p = Pattern.compile("id=(.*?)");
                Matcher m = p.matcher(coverImageUrl);

                if(m.matches()) {
                    String id=m.group(1);
                    coverImageUrl = String.valueOf(stringBuilder.append("https://books.google.com/books/content/images/frontcover/").append(id).append("?fife=w300"));
                } else {
                    Log.i(LOG_TAG, "Issue with cover");
                }

                JSONObject saleInfo = currentBook.getJSONObject("saleInfo");

                String buyLink = (String) saleInfo.get("buyLink");

                JSONObject retailPrice = saleInfo.getJSONObject("retailPrice");
                String priceInfo = retailPrice.getString("amount");
                String currencyInfo = retailPrice.getString("currencyCode");


                Book bookItem = new Book(title,author,buyLink, coverImageUrl, currencyInfo, priceInfo);

                books.add(bookItem);

            }

        } catch (JSONException e) {
            Log.e(LOG_TAG,"Problem parsing JSON results ", e);
        }
        return books;
    }

}
