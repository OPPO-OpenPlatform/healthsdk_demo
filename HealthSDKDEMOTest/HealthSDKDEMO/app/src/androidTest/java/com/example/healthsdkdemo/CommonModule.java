package com.example.healthsdkdemoforauth;

import android.util.Log;

public class CommonModule {
    private static final String TAG = "CommonModule";

    /**
     * 等待服务器数据处理
     */
    static void ServerResponseDelay(int millSeconds) {
        try {
            Thread.sleep(millSeconds);
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
        }
    }
}
