package com.leongao.magtime.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SharedPreferenceHelper {
    private static SharedPreferences searchHistoryPreference = MyApplication.getAppContext()
            .getSharedPreferences("searchHistoryTags", Context.MODE_PRIVATE);

    private static SharedPreferences.Editor editor = searchHistoryPreference.edit();

    public static void putSearchHistoryTag(String keywords) {
        editor.putString(keywords, keywords);
        editor.apply();
    }

    /**
     * 获取所以历史搜索数据
     * @return
     */
    public static List<String> getAllSearchHistoryTags() {
        Map<String, ?> map = searchHistoryPreference.getAll();
        List<String> tagList = new ArrayList<>();
        for (String tag : map.keySet()) {
            tagList.add(tag);
        }
        return tagList;
    }

    /**
     * 清除所有历史搜索数据
     */
    public static void deleteALlSearchHistoryTags() {
        editor.clear();
        editor.apply();
    }
}
