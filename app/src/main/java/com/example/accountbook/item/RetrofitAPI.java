package com.example.accountbook.item;

import com.example.accountbook.dto.CategoryDTO;
import com.example.accountbook.dto.MoneyDTO;
import com.example.accountbook.dto.TransferMoneyDTO;
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
    Call<UserInfoDTO> getUserInfo(
            @Path("userId") String userId,
            @Query("userPw") String userPw
    );

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
            @Query("endDay") int endDay
    );

    @PUT("category/update/{seq}")
    Call<Integer> updateCategoryInfo(
            @Path("seq") int seq,
            @Query("code") String code,
            @Query("category01") String category01,
            @Query("category02") String category02,
            @Query("contents") String contents,
            @Query("endDay") int endDay
    );

    @DELETE("category/delete/{seq}")
    Call<Integer> deleteCategoryInfo(@Path("seq") int seq);


    /* save money info */
    @GET("money/search/{userSeq}")
    Call<List<MoneyDTO>> getMoneyInfo(
            @Path("userSeq") int userSeq,
            @Query("dateCondition") String dateCondition
    );

    @GET("money/transfer/search/{userSeq}")
    Call<List<TransferMoneyDTO>> getTransferMoneyInfo(
            @Path("userSeq") int userSeq,
            @Query("date") String date
    );

    @POST("money/insert")
    Call<Integer> insertMoneyInfo (
            @Query("userSeq") int userSeq,
            @Query("settingsSeq") int settingsSeq,
            @Query("bankSeq") int bankSeq,
            @Query("in_sp") String in_sp,
            @Query("date") String date,
            @Query("money") String money,
            @Query("memo") String memo
    );

    @POST("money/transfer/insert")
    Call<Integer> insertTransferMoneyInfo (
            @Query("userSeq") int userSeq,
            @Query("incomeBankSeq") int incomeBankSeq,
            @Query("expandingBankSeq") int expandingBankSeq,
            @Query("date") String date,
            @Query("money") String money,
            @Query("memo") String memo,
            @Query("incomeBank") String incomeBank,
            @Query("expandingBank") String expandingBank,
            @Query("incomeSettingsSeq") int incomeSettingsSeq,
            @Query("expandingSettingsSeq") int expandingSettingsSeq
    );

    @PUT("money/modify/{seq}")
    Call<Integer> modifyMoneyInfo(
            @Path("seq") int seq,
            @Query("settingsSeq") int settingsSeq,
            @Query("bankSeq") int bankSeq,
            @Query("in_sp") String in_sp,
            @Query("date") String date,
            @Query("money") String money,
            @Query("memo") String memo
    );

    @DELETE("money/delete/all/{userSeq}")
    Call<Integer> deleteMoneyAll(@Path("userSeq") int userSeq);

    @DELETE("money/delete/{moneySeq}")
    Call<Integer> deleteMoneyInfo(@Path("moneySeq") int moneySeq);

    @DELETE("money/transfer/delete/{seq}")
    Call<Integer> deleteTransferMoneyInfo(@Path("seq") int seq);
}
