package com.example.accountbook.viewmodel;

import android.app.Activity;

import androidx.lifecycle.ViewModel;

import com.example.accountbook.model.SaveMoneyModel;

public class SaveMoneyViewModel extends ViewModel {
    private SaveMoneyModel model;

    public void setSaveMoneyViewModel(Activity activity) {
        model = new SaveMoneyModel(activity);
    }

    public void deleteAll() {
        model.deleteAll();
    }
}
