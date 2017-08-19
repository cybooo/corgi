package cz.wake.corgibot.utils;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class HttpUtils {

    public static final OkHttpClient CLIENT = new OkHttpClient.Builder().build();

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
}
