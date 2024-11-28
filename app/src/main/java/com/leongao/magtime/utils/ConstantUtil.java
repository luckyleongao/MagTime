package com.leongao.magtime.utils;

public class ConstantUtil {
    public static final String BASE_URL = "http://192.168.2.178:1337/";
    public static final String API_TOKEN = "2318c5ee7e926d96d56c70d561cbd2c863b7a8c23228887eceafdd6f89e60907f2c6ecf2d5afb80674cc71fdceb0dced5b02a4c80610a27ee9e2b1311bd07d5dc763d3acbcbff01d80b3f4d6db86297777bb8255e72505fbe6f1332c6263f7112530d823995613b84c21c687869d32fca0598641fbc82e9862be846b9146172b";

    public static final String[] viewPagerNames = {"收藏", "下载"};

    public static final String DELETE = "删除";
    public static final String DONE = "完成";

    /*****Favorite Fragment Start****/
    public static final String DELETE_ALL = "全部删除";
    public static final String SELECT_SOME = "选择部分";
    public static final String CANCEL = "取消";
    public static final String DELETE_FAVORITE = "删除收藏";
    public static final String DELETE_DOWNLOAD = "删除下载";
    public static final String ALL_DELETE = "确定全部删除吗？";
    /*****Favorite Fragment End****/
    public static final int imageWidthPixels = 1024;
    public static final int imageHeightPixels = 1024;

    public enum DOWNLOAD_STATUS {
        PAUSE, IN_PROGRESS, COMPLETE
    }
}
