package com.example.accountbook.item;

import com.example.accountbook.dto.CategoryDTO;
import com.example.accountbook.dto.MoneyDTO;
import com.example.accountbook.dto.UserInfoDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {
    /* user info */
    @GET("user/{userId}")
    Call<UserInfoDTO> getUserInfo(@Path("userId") String userId);

    @PUT("user/nickname/{userSeq}")
    Call<Integer> nicknameChange(
            @Path("userSeq") int userSeq,
            @Query("userNickname") String userNickname
    );

    @PUT("user/pw/{userSeq}")
    Call<Integer> pwChange(
            @Path("userSeq")int userSeq,
            @Query("pw") String pw
    );

    /* category info */
    @GET("category/{userSeq}")
    Call<List<CategoryDTO>> getCategoryInfo(@Path("userSeq") int userSeq);

    @POST("category/insert")
    Call<Integer> insertCategoryInfo(
            @Query("userSeq") int userSeq,
            @Query("code") String code,
            @Query("category01") String category01,
            @Query("category02") String category02,
            @Query("contents") String contents,
            @Query("payDay") String payDay
    );

    @DELETE("category/delete/{seq}")
    Call<Integer> deleteCategoryInfo(@Path("seq") int seq);


    /* save money info */
    @GET("money/search/{userSeq}")
    Call<List<MoneyDTO>> getMoneyInfo(
            @Path("userSeq") int userSeq,
            @Query("dateCondition") String dateCondition
    );

    @DELETE("money/delete/all/{userSeq}")
    Call<Integer> deleteMoneyAll(@Path("userSeq") int userSeq);
}
