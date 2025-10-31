package com.example.tasbi;

import static ir.tapsell.sdk.advertiser.TapsellAdActivity.ZONE_ID;

import android.app.Application;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Tapsell.initialize(this, "kgdblbqbshnksqftckljhdhedhekpjdmbttclsosrqbqnmqcoidsscclgfpfaerqlrpaam");

    }

}