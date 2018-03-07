package com.example.xiewujie.dailyzhihu.mytool;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.net.PortUnreachableException;

/**
 * Created by xiewujie on 2018/1/29.
 */

public class MyApplication extends Application {
    static Context context;
    public static final int MOBILE_NET_2G = 0;
    public static final int MOBILE_NET_3G= 1;
    public static final int MOBILE_NET_4G = 2;
    public static final int ISNOTMOBILETYPE = 3;

    @Override
    public void onCreate() {
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
    public static NetworkInfo getNetworkInfo(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }
    public static int getMobileType(){
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        int netType = telephonyManager.getNetworkType();
        switch (netType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return MOBILE_NET_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return MOBILE_NET_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return MOBILE_NET_4G;
            default:
                break;
        }
        return ISNOTMOBILETYPE;
    }
}
