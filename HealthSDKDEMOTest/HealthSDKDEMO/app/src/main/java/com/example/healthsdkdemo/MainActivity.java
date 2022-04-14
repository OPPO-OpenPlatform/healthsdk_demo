package com.example.healthsdkdemoforauth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthsdkdemoforauth.databinding.ActivityMainBinding;

import com.heytap.databaseengine.HeytapHealthApi;
import com.heytap.databaseengine.apiv2.HResponse;
import com.heytap.databaseengine.apiv2.IHeytapHealthApi;
import com.heytap.databaseengine.apiv2._HeytapHealth;
import com.heytap.databaseengine.apiv2.common.util.InstallUtils;
import com.heytap.databaseengine.apiv2.health.HeytapHealthParams;
import com.heytap.databaseengine.model.UserInfo;
import com.heytap.databaseengine.model.proxy.HeartRateDataStatProxy;
import com.heytap.databaseengine.model.proxy.HeartRateProxy;
import com.heytap.databaseengine.model.proxy.SportDataDetailProxy;
import com.heytap.databaseengine.model.proxy.SportDataStatProxy;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding mBinding;
    private IHeytapHealthApi mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        long initStart = System.currentTimeMillis();
        HeytapHealthApi.init(this);
        while (true) {
            if (_HeytapHealth.hasInit()) {
                break;
            }
        }
        runOnUiThread(() -> {
            long initEnd = System.currentTimeMillis();
            mBinding.resultScreen.append("interface init time: " + (initEnd - initStart) +" ms\n");
        });
        mApi = HeytapHealthApi.getInstance();
        long startTime = LocalDateTime.of(2021, 4, 1, 0, 0, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endTime = LocalDateTime.of(2021, 4, 30, 23, 59, 59)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        mBinding.buttonDailyActivityStat.setOnClickListener(view -> {
            HeytapHealthParams params = new HeytapHealthParams
                    .Builder(HeytapHealthParams.DAILY_ACTIVITY, HeytapHealthParams.MODE.STAT)
                    .setTimeScope(startTime, endTime)
                    .build();
//            for (int i = 0; i < 1000; i++) {
                long start = System.currentTimeMillis();
                mApi.sportHealthApi().query(params, new HResponse<List<SportDataStatProxy>>() {
                    @Override
                    public void onSuccess(List<SportDataStatProxy> sportDataStatList) {
                        runOnUiThread(() -> {
                            mBinding.resultScreen.append("Start print data\n");
                            mBinding.resultScreen.append(sportDataStatList.toString() + "\n");
                            long end = System.currentTimeMillis();
                            mBinding.resultScreen.append("interface work time: " + (end - start) +" ms\n");
                            mBinding.resultScreen.append("Print end.\n\n");
                        });
                    }

                    @Override
                    public void onFailure(int i) {
                        runOnUiThread(() -> {
                            mBinding.resultScreen.append("read data fail, error code: " + i + "\n");
                            long end = System.currentTimeMillis();
                            mBinding.resultScreen.append("interface work time: " + (end - start) +" ms\n\n");
                        });
                    }
                });
//            }
        });
        mBinding.buttonHeartRateStat.setOnClickListener(view -> {
            HeytapHealthParams params = new HeytapHealthParams
                    .Builder(HeytapHealthParams.HEART_RATE, HeytapHealthParams.MODE.STAT)
                    .setTimeScope(startTime, endTime)
                    .build();
            long start = System.currentTimeMillis();
            mApi.sportHealthApi().query(params, new HResponse<List<HeartRateDataStatProxy>>() {
                @Override
                public void onSuccess(List<HeartRateDataStatProxy> heartRateDataStatList) {
                    runOnUiThread(() -> {
                        mBinding.resultScreen.append("Start print data\n");
                        mBinding.resultScreen.append(heartRateDataStatList.toString() + "\n");
                        long end = System.currentTimeMillis();
                        mBinding.resultScreen.append("interface work time: " + (end - start) +" ms\n");
                        mBinding.resultScreen.append("Print end.\n\n");
                    });
                }

                @Override
                public void onFailure(int i) {
                    runOnUiThread(() -> {
                        mBinding.resultScreen.append("read data fail, error code: " + i + "\n");
                        long end = System.currentTimeMillis();
                        mBinding.resultScreen.append("interface work time: " + (end - start) + " ms\n\n");
                    });
                }
            });
        });
        mBinding.buttonDailyActivityDetail.setOnClickListener(view -> {
            HeytapHealthParams params = new HeytapHealthParams
                    .Builder(HeytapHealthParams.DAILY_ACTIVITY, HeytapHealthParams.MODE.DETAIL)
                    .setTimeScope(startTime, endTime)
                    .build();
            for (int i = 0; i < 100; i++) {
                long start = System.currentTimeMillis();
                mApi.sportHealthApi().query(params, new HResponse<List<SportDataDetailProxy>>() {
                    @Override
                    public void onSuccess(List<SportDataDetailProxy> sportDataDetailList) {
                        runOnUiThread(() -> {
                            mBinding.resultScreen.append("Start print data\n");
                            mBinding.resultScreen.append(sportDataDetailList.toString() + "\n");
                            long end = System.currentTimeMillis();
                            mBinding.resultScreen.append("interface work time: " + (end - start) + " ms\n");
                            mBinding.resultScreen.append("Print end.\n\n");
                        });
                    }

                    @Override
                    public void onFailure(int i) {
                        runOnUiThread(() -> {
                            mBinding.resultScreen.append("read data fail, error code: " + i + "\n");
                            long end = System.currentTimeMillis();
                            mBinding.resultScreen.append("interface work time: " + (end - start) + " ms\n\n");
                        });
                    }
                });
            }
        });
        mBinding.buttonHeartRateDetail.setOnClickListener(view -> {
            HeytapHealthParams params = new HeytapHealthParams
                    .Builder(HeytapHealthParams.HEART_RATE, HeytapHealthParams.MODE.DETAIL)
                    .setTimeScope(startTime, endTime)
                    .build();

            long start = System.currentTimeMillis();
            mApi.sportHealthApi().query(params, new HResponse<List<HeartRateProxy>>() {
                @Override
                public void onSuccess(List<HeartRateProxy> heartRateList) {
                    runOnUiThread(() -> {
                        mBinding.resultScreen.append("Start print data\n");
                        mBinding.resultScreen.append(heartRateList.toString() + "\n");
                        long end = System.currentTimeMillis();
                        mBinding.resultScreen.append("interface work time: " + (end - start) + " ms\n");
                        mBinding.resultScreen.append("Print end.\n\n");
                    });
                }

                @Override
                public void onFailure(int i) {
                    runOnUiThread(() -> {
                        mBinding.resultScreen.append("read data fail, error code: " + i + "\n");
                        long end = System.currentTimeMillis();
                        mBinding.resultScreen.append("interface work time: " + (end - start) + " ms\n\n");
                    });
                }
            });
        });
        mBinding.buttonUserInfo.setOnClickListener(view -> {
            long start = System.currentTimeMillis();
            mApi.userInfoApi().query(new HResponse<List<UserInfo>>() {
                @Override
                public void onSuccess(List<UserInfo> userInfoList) {
                    runOnUiThread(() -> {
                        mBinding.resultScreen.append("Start print data\n");
                        mBinding.resultScreen.append(userInfoList.toString() + "\n");
                        long end = System.currentTimeMillis();
                        mBinding.resultScreen.append("interface work time: " + (end - start) +" ms\n");
                        mBinding.resultScreen.append("Print end.\n\n");
                    });
                }

                @Override
                public void onFailure(int i) {
                    runOnUiThread(() -> {
                        mBinding.resultScreen.append("read data fail, error code: " + i +"\n");
                        long end = System.currentTimeMillis();
                        mBinding.resultScreen.append("interface work time: " + (end - start) +" ms\n\n");
                    });
                }
            });
        });
        mBinding.buttonValid.setOnClickListener(view -> {
            long start = System.currentTimeMillis();
            mApi.authorityApi().valid(new HResponse<List<String>>() {
                @Override
                public void onSuccess(List<String> strings) {
                    runOnUiThread(() -> {
                        mBinding.resultScreen.append("Auth scope is " + strings + "\n");
                        long end = System.currentTimeMillis();
                        mBinding.resultScreen.append("interface work time: " + (end - start) +" ms\n\n");
                    });
                }

                @Override
                public void onFailure(int i) {
                    runOnUiThread(() -> {
                        mBinding.resultScreen.append("Auth valid failed, error code: " + i + "\n");
                        long end = System.currentTimeMillis();
                        mBinding.resultScreen.append("interface work time: " + (end - start) +" ms\n\n");
                    });
                }
            });
        });
        mBinding.buttonAuth.setOnClickListener(view -> {
            long start = System.currentTimeMillis();
            mApi.authorityApi().request(this);
            runOnUiThread(() -> {
                long end = System.currentTimeMillis();
                mBinding.resultScreen.append("interface work time: " + (end - start) +" ms\n\n");
            });
        });

        mBinding.downloadApp.setOnClickListener(view -> {
            long start = System.currentTimeMillis();
            InstallUtils.DownloadApp(this);
            runOnUiThread(() -> {
                long end = System.currentTimeMillis();
                mBinding.resultScreen.append("interface work time: " + (end - start) +" ms\n\n");
            });
        });
        mBinding.revokeAccess.setOnClickListener(view -> {
            long start = System.currentTimeMillis();
            mApi.authorityApi().revoke(new HResponse<List<Object>>() {
                @Override
                public void onSuccess(List<Object> objects) {
                    runOnUiThread(() -> {
                        mBinding.resultScreen.append("revoke access successfully!\n");
                        long end = System.currentTimeMillis();
                        mBinding.resultScreen.append("interface work time: " + (end - start) +" ms\n\n");
                    });
                }

                @Override
                public void onFailure(int i) {
                    runOnUiThread(() -> {
                        mBinding.resultScreen.append("revoke access failed! error code: " + i + "\n");
                        long end = System.currentTimeMillis();
                        mBinding.resultScreen.append("interface work time: " + (end - start) +" ms\n\n");
                    });
                }
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            runOnUiThread(() -> mBinding.resultScreen.append("auth finished successfully!\n"));
        } else {
            runOnUiThread(() -> mBinding.resultScreen.append("auth canceled\n"));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}