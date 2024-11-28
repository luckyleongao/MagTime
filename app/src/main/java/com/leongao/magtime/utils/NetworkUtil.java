package com.leongao.magtime.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.leongao.magtime.R;

public class NetworkUtil {

    public enum NETWORK_TYPE {
        ALL, WIFI, MOBILE
    }

    private static final int NETWORK_TYPE_UNAVAILABLE = -1;
    public static final int NETWORK_TYPE_UNKNOWN = 0;

    public static boolean isNetworkAvailable(Context context) {
        if (null == context) return false;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    public static boolean isNetworkAvailable(Context context, NETWORK_TYPE networkType) {
        if (null == context) return false;
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == manager) return false;
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (null == info) return false;
        switch (networkType) {
            case ALL:
                return info.isAvailable();
            case WIFI:
                return (info.getType() == ConnectivityManager.TYPE_WIFI)
                        && (info.isAvailable());
            case MOBILE:
                return (info.getType() == ConnectivityManager.TYPE_MOBILE)
                        && (info.isAvailable());
        }
        return false;
    }

    public static String getCurrentNetworkType(Context context) {
        int networkType = NETWORK_TYPE_UNKNOWN;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()
                && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return "Wi-Fi";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                // TODO: sdk >= 23,动态申请权限
                networkType = telephonyManager.getNetworkType();
                return getNetworkClassByType(networkType);
            }
        } else {
            networkType = NETWORK_TYPE_UNAVAILABLE;
        }

        return getNetworkClassByType(networkType);
    }

    private static String getNetworkClassByType(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_GSM:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
            case 19:
                return "4G";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "未知网络";
            default:
                return "无网络";
        }
    }
}
