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
import com.heytap.databaseengine.apiv3.DataReadRequest;
import com.heytap.databaseengine.apiv3.data.DataPoint;
import com.heytap.databaseengine.apiv3.data.DataSet;
import com.heytap.databaseengine.apiv3.data.DataType;
import com.heytap.databaseengine.apiv3.data.Element;
import com.heytap.databaseengine.model.proxy.UserDeviceInfoProxy;
import com.heytap.databaseengine.model.proxy.UserInfoProxy;
import com.heytap.databaseengine.utils.HLog;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding mBinding;
    private IHeytapHealthApi mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        long initStart = System.currentTimeMillis();
        HeytapHealthApi.init(this);
        HeytapHealthApi.setLoggable(true);
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
        long startTime = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endTime = LocalDate.now().plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;

        mBinding.buttonDailyActivityStat.setOnClickListener(view -> {
            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .read(DataType.TYPE_DAILY_ACTIVITY_COUNT)
                    .setTimeRange(startTime, endTime)
                    .build();
            long start = System.currentTimeMillis();
            try {
                mApi.dataApi().read(readRequest, new HResponse<List<DataSet>>() {
                    @Override
                    public void onSuccess(List<DataSet> sportDataStatList) {
                        runOnUiThread(() -> {
                            mBinding.resultScreen.append("Start print data\n");
                            for (DataSet sportDataStat: sportDataStatList) {
                                showDataSet(sportDataStat);
                            }
                            long end = System.currentTimeMillis();
                            mBinding.resultScreen.append("interface work time: " + (end - start) + " ms\n");
                            mBinding.resultScreen.append("Print end.\n\n");
                        });
                    }

                    @Override
                    public void onFailure(int i) {
                        runOnUiThread(() -> {
                            mBinding.resultScreen.append("read data fail, error code: " + i + "\n");
                            HLog.e(TAG, "read data fail, error code: " + i + "\n");
                        });
                    }
                });
            } catch (Exception e) {
                HLog.e(TAG, e.toString());
            }
        });

        mBinding.buttonHeartRateStat.setOnClickListener(view -> {
            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .read(DataType.TYPE_HEART_RATE_COUNT)
                    .setTimeRange(startTime, endTime)
                    .build();
            long start = System.currentTimeMillis();
                try {
                        mApi.dataApi().read(readRequest, new HResponse<List<DataSet>>() {
                            @Override
                            public void onSuccess(List<DataSet> heartRateDataStatList) {
                                runOnUiThread(() -> {
                                    mBinding.resultScreen.append("Start print data\n");
                                    for (DataSet heartRateDataStat : heartRateDataStatList) {
                                        showDataSet(heartRateDataStat);
                                    }
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
                } catch (Exception e) {
                    HLog.e(TAG, e.toString());
                }
        });
        mBinding.buttonDailyActivityDetail.setOnClickListener(view -> {
            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .read(DataType.TYPE_DAILY_ACTIVITY)
                    .setTimeRange(startTime, endTime)
                    .build();
            long start = System.currentTimeMillis();
                try {
                        mApi.dataApi().read(readRequest, new HResponse<List<DataSet>>() {
                            @Override
                            public void onSuccess(List<DataSet> sportDataStatList) {
                                    runOnUiThread(() -> {
                                        mBinding.resultScreen.append("Start print data\n");
                                        for (DataSet sportDataStat : sportDataStatList) {
                                            showDataSet(sportDataStat);
                                        }
                                        long end = System.currentTimeMillis();
                                        mBinding.resultScreen.append("interface work time: " + (end - start) / 1000 / 60 + " mins\n");
                                        mBinding.resultScreen.append("Print end.\n\n");
                                    });
                            }

                            @Override
                            public void onFailure(int i) {
                                runOnUiThread(() -> {
                                    mBinding.resultScreen.append("read data fail, error code: " + i + "\n");
                                    HLog.i(TAG,"continuous request data success");
                                    HLog.e(TAG, "read data fail, error code: " + i + "\n");
                                });
                            }
                        });
                } catch (Exception e) {
                    HLog.e(TAG, e.toString());
                }
        });
        mBinding.buttonHeartRateDetail.setOnClickListener(view -> {
            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .read(DataType.TYPE_HEART_RATE)
                    .setTimeRange(startTime, endTime)
                    .build();
            long start = System.currentTimeMillis();
            mApi.dataApi().read(readRequest, new HResponse<List<DataSet>>() {
                @Override
                public void onSuccess(List<DataSet> heartRateDataStatList) {
                    runOnUiThread(() -> {
                        mBinding.resultScreen.append("Start print data\n");
                        for (DataSet heartRateDataStat: heartRateDataStatList) {
                            showDataSet(heartRateDataStat);
                        }
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
        mBinding.buttonUserInfo.setOnClickListener(view -> {
            long start = System.currentTimeMillis();
            mApi.userInfoApi().query(new HResponse<List<UserInfoProxy>>() {
                @Override
                public void onSuccess(List<UserInfoProxy> userInfoList) {
                    runOnUiThread(() -> {
                        mBinding.resultScreen.append("Start print data\n");
                        mBinding.resultScreen.append(userInfoList + "\n");
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
//            mApi.authorityApi().request(this, "www.baidu.com");
            runOnUiThread(() -> {
                long end = System.currentTimeMillis();
                mBinding.resultScreen.append("interface work time: " + (end - start) +" ms\n\n");
            });
        });

        mBinding.buttonGetDeviceInfo.setOnClickListener(v -> mApi.deviceApi().deviceInfoApi()
                .queryBoundDevice(new HResponse<List<UserDeviceInfoProxy>>() {
            @Override
            public void onSuccess(List<UserDeviceInfoProxy> userDeviceInfoProxies) {
                runOnUiThread(() ->
                        mBinding.resultScreen.append("Devices are " + userDeviceInfoProxies + "\n"));
            }

            @Override
            public void onFailure(int i) {
                runOnUiThread(() ->
                        mBinding.resultScreen.append("err " + i + "\n"));
            }
        }));

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

    private void showDataSet(DataSet dataSet) {
        HLog.i(TAG, "time end: " + System.currentTimeMillis());
        for (DataPoint dataPoint : dataSet.getDataPoints()) {
            HLog.i(TAG, "data type name: " + dataPoint.getDataType().getName());
            mBinding.resultScreen.append("Date:\t" + Instant.ofEpochMilli(dataPoint.getStartTimeStamp())
                    .atZone(ZoneId.systemDefault()).toLocalDateTime() + "\n");
            for (Element element : dataPoint.getDataType().getElements()) {
                HLog.i(TAG, "field name: " + element.getName());
                HLog.i(TAG, "field format: " + element.getFormat());
                HLog.i(TAG, "value: " + dataPoint.getValue(element));
                mBinding.resultScreen.append(element.getName() + ":\t" + dataPoint.getValue(element) + "\n");
            }
            mBinding.resultScreen.append("\n");
        }
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