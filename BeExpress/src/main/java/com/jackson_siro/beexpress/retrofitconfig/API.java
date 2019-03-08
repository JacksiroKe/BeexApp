package com.jackson_siro.beexpress.retrofitconfig;

import com.jackson_siro.beexpress.modal.Callback.CallbackBackgroundDrawer;
import com.jackson_siro.beexpress.modal.Callback.CallbackCategory;
import com.jackson_siro.beexpress.modal.Callback.CallbackCountComment;
import com.jackson_siro.beexpress.modal.Callback.CallbackItems;
import com.jackson_siro.beexpress.modal.Callback.CallbackItemsByCategory;
import com.jackson_siro.beexpress.modal.Callback.CallbackItemsDetail;
import com.jackson_siro.beexpress.modal.Callback.CallbackItemsSearch;
import com.jackson_siro.beexpress.modal.Callback.CallbackItemsSlider;
import com.jackson_siro.beexpress.modal.Callback.CallbackShowComment;
import com.jackson_siro.beexpress.modal.Callback.FeedbackModal;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {
    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "User-Agent: BeExpress";

    String GetItemsSearch = "api/get-items-search.php";
    String GetItemsSlider = "api/get-items-slider.php";
    String GetAllItems = "api/get-all-items.php";
    String GetAllCategory = "api/get-all-category.php";
    String GetAllItemsbyCategory = "api/get-items-by-category-id.php";
    String DetailItems = "api/get-items-detail.php";
    String Feedback = "includes/feedback.php";
    String ShowComment = "api/get-comment-by-id.php";
    String CountComment = "api/get-record-comment.php";
    String BackgroundDrawer = "api/get-bg-drawer.php";
    String AccessKeyString = "?accesskey=";
    String AccessKeyValue = "BeExpressKey"; // change accesskey with you want, this accesskey must same with your accesskey in admin panel

    @Headers({CACHE, AGENT})
    @GET(GetItemsSearch)
    Call<CallbackItemsSearch> getItemsSearch(
            @Query("keyword") String keyword
    );

    @Headers({CACHE, AGENT})
    @GET(GetAllCategory+AccessKeyString+AccessKeyValue)
    Call<CallbackCategory> getCategory();

    @Headers({CACHE, AGENT})
    @GET(GetItemsSlider+AccessKeyString+AccessKeyValue)
    Call<CallbackItemsSlider> getItemsSlider();

    @Headers({CACHE, AGENT})
    @GET(GetAllItems+AccessKeyString+AccessKeyValue)
    Call<CallbackItems> getAllItems(
            @Query("total") int total,
            @Query("page") int page
    );

    @Headers({CACHE, AGENT})
    @GET(DetailItems+AccessKeyString+AccessKeyValue)
    Call<CallbackItemsDetail> getAllItemsDetail(
            @Query("itemid") long itemid
    );

    @Headers({CACHE, AGENT})
    @GET(GetAllItemsbyCategory+AccessKeyString+AccessKeyValue)
    Call<CallbackItemsByCategory> getItemsCategory(
            @Query("category") int categoryid
    );

    @Headers({CACHE, AGENT})
    @GET(ShowComment+AccessKeyString+AccessKeyValue)
    Call<CallbackShowComment> getShowComment(
            @Query("itemid") int itemid
    );

    @Headers({CACHE, AGENT})
    @GET(CountComment+AccessKeyString+AccessKeyValue)
    Call<CallbackCountComment> getCountComment(
            @Query("itemid") long itemid
    );

    @Headers({CACHE, AGENT})
    @GET(BackgroundDrawer+AccessKeyString+AccessKeyValue)
    Call<CallbackBackgroundDrawer> getImageDrawer(

    );

    @FormUrlEncoded
    @POST(Feedback)
    Call<FeedbackModal> feedBack(
            @Field("full_name") String full_name,
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("gender") String gender,
            @Field("city") String city,
            @Field("country") String country,
            @Field("txt_feed") String txt_feed

    );
}
