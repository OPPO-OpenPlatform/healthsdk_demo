package com.example.healthsdkdemoforauth;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.heytap.databaseengine.HeytapHealthApi;
import com.heytap.databaseengine.apiv2.HResponse;
import com.heytap.databaseengine.apiv2.health.HeytapHealthParams;
import com.heytap.databaseengine.apiv2.health.business.ISportHealthApi;
import com.heytap.databaseengine.constant.ErrorCode;
import com.heytap.databaseengine.model.proxy.SportDataDetailProxy;
import com.heytap.databaseengine.model.proxy.SportDataStatProxy;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SportHealthApiTest {
    private static final String TAG = "SportHealthApiTest";
    private String[] authList = new String[]{"READ_DAILY_ACTIVITY", "READ_HEART_RATE"};
    private static Context appContext;
    private static ISportHealthApi mApi;

    @BeforeClass
    public static void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        AndroidThreeTen.init(appContext);
        HeytapHealthApi.init(appContext);
        mApi = HeytapHealthApi.getInstance().sportHealthApi();
    }

    @Test
    public void PackageNameTest() {
        // Context of the app under test.
        assertEquals("com.example.healthsdkdemo", appContext.getPackageName());
    }

    @Test
    public void timeScopeTest() {
        // 31天数据申请
        long startTime = LocalDate.now().atStartOfDay().minusDays(29)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;
        long endTime = LocalDate.now().atStartOfDay()
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        HeytapHealthParams params = new HeytapHealthParams
                .Builder(HeytapHealthParams.DAILY_ACTIVITY, HeytapHealthParams.MODE.STAT)
                .setTimeScope(startTime, endTime)
                .build();
        MutableLiveData<Integer> testFlag = new MutableLiveData<>();
        TestResponse<List<SportDataStatProxy>> response = new TestResponse<>(testFlag);
        mApi.query(params, response);

        //30天数据申请
        startTime += 1;
        params = new HeytapHealthParams
                .Builder(HeytapHealthParams.DAILY_ACTIVITY, HeytapHealthParams.MODE.STAT)
                .setTimeScope(startTime, endTime)
                .build();
        MutableLiveData<Integer> testFlag2 = new MutableLiveData<>();
        response = new TestResponse<>(testFlag2);
        mApi.query(params, response);

        CommonModule.ServerResponseDelay(1000);
        assertEquals(ErrorCode.ERR_PARAMETER_ERROR, testFlag.getValue().intValue());
        assertEquals(ErrorCode.SUCCESS, testFlag2.getValue().intValue());
    }

    @Test
    public void DataTypeTest() {
        // invalid data type test
        long startTime = LocalDate.now().atStartOfDay().minusDays(2)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;
        long endTime = LocalDate.now().atStartOfDay()
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        HeytapHealthParams params = new HeytapHealthParams
                .Builder("", HeytapHealthParams.MODE.STAT)
                .setTimeScope(startTime, endTime)
                .build();
        MutableLiveData<Integer> testFlag = new MutableLiveData<>();
        TestResponse<List<SportDataStatProxy>> response = new TestResponse<>(testFlag);
        mApi.query(params, response);

        CommonModule.ServerResponseDelay(1000);
        assertEquals(ErrorCode.ERR_DATA_TYPE_IS_NOT_SUPPORT, testFlag.getValue().intValue());
    }

    /**
     * 该项测试前提是账号中有30天份的满数据
     */
    @Test
    public void PressureTest() {
        long startTime = LocalDate.now().atStartOfDay().minusDays(28)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;
        long endTime = LocalDate.now().atStartOfDay()
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        HeytapHealthParams params = new HeytapHealthParams
                .Builder(HeytapHealthParams.DAILY_ACTIVITY, HeytapHealthParams.MODE.DETAIL)
                .setTimeScope(startTime, endTime)
                .build();
        MutableLiveData<Integer> testFlag = new MutableLiveData<>();
        testFlag.postValue(0);
        for (int i = 0;i < 30;i++) {
            mApi.query(params, new HResponse<List<SportDataDetailProxy>>() {
                @Override
                public void onSuccess(List<SportDataDetailProxy> list) {
                    testFlag.postValue(testFlag.getValue());
                }

                @Override
                public void onFailure(int i) {
                    Log.e(TAG, "test flag: " + testFlag.getValue().intValue());
                    testFlag.postValue(testFlag.getValue() + i);
                }
            });
        }

        CommonModule.ServerResponseDelay(5000);
        Log.d(TAG, "test flag: " + testFlag.getValue().intValue());
        assertTrue(testFlag.getValue().intValue() < 1);
    }

    private static class TestResponse<T> implements HResponse<T> {
        private MutableLiveData<Integer> testFlag;

        public TestResponse(MutableLiveData<Integer> testFlag) {
            this.testFlag = testFlag;
        }

        @Override
        public void onSuccess(T t) {
            testFlag.postValue(ErrorCode.SUCCESS);
        }

        @Override
        public void onFailure(int i) {
            testFlag.postValue(i);
        }
    }
}