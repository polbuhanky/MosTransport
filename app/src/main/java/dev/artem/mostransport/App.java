package dev.artem.mostransport;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import com.yandex.mapkit.MapKitFactory;

import java.lang.ref.WeakReference;

public class App extends Application {
    private static WeakReference<Context> mContext;
    public static DisplayMetrics displayMetrics;

    private static String host;
    private final String MAPKIT_API_KEY = "c546d5ee-42d6-4ca9-bc8c-14b1c43d2b9d";

    @Override
    public void onCreate() {
        super.onCreate();
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
    }

}
