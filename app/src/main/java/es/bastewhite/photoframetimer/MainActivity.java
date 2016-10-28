package es.bastewhite.photoframetimer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doAction();
            }
        });
    }

    private void doAction() {
        Handler handler = new Handler();
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                launchApp("be.wyseur.photo.buy", MainActivity.this);
                doAction2();
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void doAction2() {
        Handler handler = new Handler();
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                killApp(MainActivity.this, "be.wyseur.photo.buy");
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    /**
     * Launchs an app.
     * @param applicationId
     * @param activity
     */
    public static void launchApp(String applicationId, Activity activity) {
        if (checkAppInstalled(applicationId, activity)) {
            Intent intent = activity.getPackageManager().getLaunchIntentForPackage(applicationId);
            activity.startActivity(intent);
        } else {
            goToMarket(applicationId, activity);
        }
    }

    /**
     * Checks if an app is installed.
     * @param applicationId
     * @param context
     * @return
     */
    public static boolean checkAppInstalled(String applicationId, Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.getPackageInfo(applicationId, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignore) {
            /* ignored */
            return false;
        }
    }

    /**
     * Goes to market to a specific app.
     * @param applicationId
     * @param activity
     */
    public static void goToMarket(String applicationId, Activity activity) {
        try {
            Uri uri = Uri.parse("market://details?id=" + applicationId);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            activity.startActivity(intent);
        } catch (ActivityNotFoundException ignore) {
            /* ignored */
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + applicationId)));
        }
    }

    private void killApp(Context context, String packageName) {
        ActivityManager am = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            Method forceStopPackage = am.getClass().getDeclaredMethod("forceStopPackage", String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(am, packageName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
