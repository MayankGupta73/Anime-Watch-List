package com.mayank7319.mayankgupta.otakulist.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mayank Gupta on 25-04-2017.
 */

public class AuthObj {
    @SerializedName("access_token")
    String accessToken;

    public String getAccessToken() {
        return accessToken;
    }
}
