package com.lmah.services;


import com.lmah.models.AddOrderModel;
import com.lmah.models.BankDataModel;
import com.lmah.models.CategoryProductDataModel;
import com.lmah.models.Create_Order_Model;
import com.lmah.models.FavouriteDataModel;
import com.lmah.models.NotificationCount;
import com.lmah.models.NotificationDataModel;
import com.lmah.models.OrderDataModel;
import com.lmah.models.OrderModel;
import com.lmah.models.ProductDataModel;
import com.lmah.models.MainCategoryDataModel;
import com.lmah.models.PlaceGeocodeData;
import com.lmah.models.PlaceMapDetailsData;
import com.lmah.models.SettingModel;
import com.lmah.models.SingleProductDataModel;
import com.lmah.models.Slider_Model;
import com.lmah.models.TimesModel;
import com.lmah.models.UserModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {


    @GET("place/findplacefromtext/json")
    Call<PlaceMapDetailsData> searchOnMap(@Query(value = "inputtype") String inputtype,
                                          @Query(value = "input") String input,
                                          @Query(value = "fields") String fields,
                                          @Query(value = "language") String language,
                                          @Query(value = "key") String key
    );

    @GET("geocode/json")
    Call<PlaceGeocodeData> getGeoData(@Query(value = "latlng") String latlng,
                                      @Query(value = "language") String language,
                                      @Query(value = "key") String key);


    @FormUrlEncoded
    @POST("api/login")
    Call<UserModel> login(@Field("phone_code") String phone_code,
                          @Field("phone") String phone

    );

    @FormUrlEncoded
    @POST("api/logout")
    Call<ResponseBody> logout(@Header("Authorization") String user_token,
                              @Field("phone_token") String phone_token,
                              @Field("soft_type") String soft_type


    );

    @FormUrlEncoded
    @POST("api/update-firebase")
    Call<ResponseBody> updatePhoneToken(@Header("Authorization") String Authorization,
                                        @Field("phone_token") String phone_token,
                                        @Field("soft_type") String soft_type
    );

    @FormUrlEncoded
    @POST("api/register")
    Call<UserModel> signUpWithoutImage(
            @Field("name") String name,
            @Field("phone_code") String phone_code,
            @Field("phone") String phone,
            @Field("email") String email
    );

    @Multipart
    @POST("api/register")
    Call<UserModel> signUpWithImage(@Part("name") RequestBody name,
                                    @Part("email") RequestBody email,
                                    @Part("phone_code") RequestBody phone_code,
                                    @Part("phone") RequestBody phone,
                                    @Part MultipartBody.Part logo


    );

    @GET("api/sttings")
    Call<SettingModel> getSetting(
            @Header("lang") String lang

    );

    @GET("api/slider")
    Call<Slider_Model> get_slider();


    @GET("api/offers")
    Call<ProductDataModel> Search(@Query("pagination") String pagination,
                                  @Query("user_id") int user_id);

    @GET("api/get-most-sale")
    Call<ProductDataModel> getMostSeller(@Query("pagination") String pagination);


    @GET("api/get-box")
    Call<ProductDataModel> getFamilyBoxes(@Query("pagination") String pagination);

    @GET("api/category-product")
    Call<CategoryProductDataModel> getCategoryProducts(@Query("pagination") String pagination,
                                                       @Query("user_id") int user_id);

    @GET("api/genaral-search")
    Call<ProductDataModel> Search(@Query("pagination") String pagination,
                                  @Query("user_id") int user_id,
                                  @Query("search_name") String search_name,
                                  @Query("have_offer") String have_offer,
                                  @Query("product_type") String product_type,

                                  @Query("departemnt_id") String departemnt_id
    );

    @GET("api/genaral-search")
    Call<ProductDataModel> getOffersProducts(@Query("pagination") String pagination,
                                             @Query("departemnt_id") String departemnt_id,
                                             @Query("brand_id") String brand_id,
                                             @Query("have_offer") String have_offer,
                                             @Query("limit_per_page") String limit_per_page,
                                             @Query("page") int page
    );

    @GET("api/product")
    Call<SingleProductDataModel> Product_detials(@Query("product_id") String product_id);

    @GET("api/one-order")
    Call<OrderModel> order_detials(@Query("order_id") int order_id);

    @GET("api/brands")
    Call<MainCategoryDataModel> getBrands(
            @Query("pagination") String pagination
    );

    @GET("api/category")
    Call<MainCategoryDataModel> getCategory(
            @Query("pagination") String pagination
    );

    @GET("api/banks")
    Call<BankDataModel> getBanks();

    @FormUrlEncoded
    @POST("api/favorite-action")
    Call<ResponseBody> addFavoriteProduct(
            @Header("Authorization") String Authorization,
            @Field("product_id") String product_id)
            ;

    @GET("api/my-favorites")
    Call<FavouriteDataModel> getMyFavoriteProducts(
            @Header("Authorization") String Authorization,
            @Query("pagination") String pagination
    )
            ;

    @GET("api/my-notification")
    Call<NotificationDataModel> getNotification(
            @Query("pagination") String pagination
            , @Header("Authorization") String user_token


    );

    @FormUrlEncoded
    @POST("api/delete-notification")
    Call<ResponseBody> deleteNotification(@Header("Authorization") String user_token,
                                          @Field("notification_id") int notification_id
    );

    @GET("api/count-unread")
    Call<NotificationCount> getUnreadNotificationCount(@Header("Authorization") String user_token
    );

    @GET("api/my-orders")
    Call<OrderDataModel> getOrders(@Header("Authorization") String user_token,
                                   @Query("user_id") String user_id,
                                   @Query("pagination") String pagination,
                                   @Query("page") int page,
                                   @Query("limit_per_page") int limit_per_page

    );

    @POST("api/create-order")
    Call<OrderModel> createOrder(
            @Header("Authorization") String Authorization,
            @Body AddOrderModel addOrderModel)
            ;

    @FormUrlEncoded
    @POST("api/find-coupon")
    Call<SettingModel> getCouponValue(@Field("coupon_num") String coupon_num);

    @Multipart
    @POST("api/update-profile")
    Call<UserModel> editClientProfileWithImage(@Header("Authorization") String Authorization,
                                               @Part("name") RequestBody name,
                                               @Part("email") RequestBody email,
                                               @Part MultipartBody.Part logo

    );

    @Multipart
    @POST("api/update-profile")
    Call<UserModel> editClientProfileWithoutImage(@Header("Authorization") String Authorization,
                                                  @Part("name") RequestBody name,
                                                  @Part("email") RequestBody email
    );

    @FormUrlEncoded
    @POST("api/add-rate")
    Call<ResponseBody> rate(
            @Header("Authorization") String Authorization,
            @Field("product_id") String product_id,
            @Field("user_id") String user_id,
            @Field("rate") String rate
    );
    @FormUrlEncoded
    @POST("api/send-partner")
    Call<ResponseBody> bepartner(
            @Field("name") String name,
            @Field("phone") String phone,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("address") String address
    );
    @POST("api/send-order")
    Call<ResponseBody> accept_orders(
            @Header("Authorization") String Authorization,
            @Body Create_Order_Model create_order_model);

    @FormUrlEncoded
    @POST("api/place-dates")
    Call<TimesModel> getTimes(
            @Header("Authorization") String Authorization,
            @Field("product_id") String product_id);

}