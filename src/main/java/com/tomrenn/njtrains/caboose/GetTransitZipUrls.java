package com.tomrenn.njtrains.caboose;

import com.squareup.okhttp.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

/**
 *
 */
public class GetTransitZipUrls implements Observable.OnSubscribe<NJTransitZipUrls> {
    static final String LOGIN_URL = "https://www.njtransit.com/mt/mt_servlet.srv?hdnPageAction=MTDevLoginSubmitTo";
    OkHttpClient httpClient;
    String username;
    String password;

    public GetTransitZipUrls(OkHttpClient httpClient, String username, String password) {
        this.httpClient = httpClient;
        this.username = username;
        this.password = password;
    }

    @Override
    public void call(final Subscriber<? super NJTransitZipUrls> subscriber) {
        RequestBody formData = new FormEncodingBuilder()
                .add("userName", username)
                .add("password", password)
                .build();

        Request postLogin = new Request.Builder()
                .url(LOGIN_URL)
                .post(formData)
                .build();

        httpClient.newCall(postLogin).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                subscriber.onError(e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    subscriber.onNext(findUrls(response));
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new RuntimeException("Bad response " + response));
                }
            }
        });

    }


    /** Parse response and find the urls to the transit data zip files */
    public NJTransitZipUrls findUrls(Response response) throws IOException{
        String url = response.request().urlString();
        Document postDocument = Jsoup.parse(response.body().string(), url);
        String baseUri = postDocument.baseUri();

        String railZipUrl = "";
        String busZipUrl = "";

        for (Element anchor : postDocument.body().getElementsByTag("a")){
            String anchorText = anchor.text().toLowerCase();
            switch (anchorText){
                case "rail data":
                    railZipUrl = buildNewLink(baseUri, anchor.attr("href"));
                    break;
                case "bus data":
                    busZipUrl = buildNewLink(baseUri, anchor.attr("href"));
                    break;
            }
        }
        return new NJTransitZipUrls(railZipUrl, busZipUrl);
    }


    public static String buildNewLink(String currentUrl, String relativePath){
        HttpUrl url = HttpUrl.parse(currentUrl);
        HttpUrl.Builder builder = url.newBuilder();
        // remove all current query params
        for (String paramName : url.queryParameterNames()){
            builder.removeAllQueryParameters(paramName);
        }
        builder.removePathSegment(url.pathSize()-1);
        // gross, unaware of a cleaner method
        return builder.build() + "/" + relativePath;
    }
}
