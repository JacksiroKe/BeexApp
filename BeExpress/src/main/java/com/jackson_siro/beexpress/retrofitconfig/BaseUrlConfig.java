package com.jackson_siro.beexpress.retrofitconfig;

public class BaseUrlConfig {
    //http://192.168.0.103/com.jackson_siro.beexpress-items/
    public static final String BASE_URL = "http://192.168.43.233/beexpress/"; // change this url with your base url
    //public static final String BASE_URL = "http://192.168.5.110/beexpress/"; // change this url with your base url
    public static final String store_token = "includes/insert_token_from_app.php"; // change this url with your base url http://192.168.0.101/com.jackson_siro.beexpress-items
    public static final int Request_Load_More = 10;
    public static final String SQL_DB_PATH = "data/data/com.jackson_siro.beexpress.android/items/databases"; //don't change this
    public static final String YOUR_GOOGLE_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=";   //don't change this

    //register,login and comment user
    public static final String BASE_URL_IMAGE = "api/users/images_users/";    // change this url with your base url
    public static final String REGISTER = "api/users/register.php"; //don't change this
    public static final String LOGIN = "api/users/login.php";   //don't change this
    public static final String UPDATE = "api/users/update.php"; //don't change this
    public static final String UPDATE_IMAGE_USERS = "api/users/images_users/images.php";    //don't change this
    public static final String USER_COMMENT = "api/users/comment.php";
}
