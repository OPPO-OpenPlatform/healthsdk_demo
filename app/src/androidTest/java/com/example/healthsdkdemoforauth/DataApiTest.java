package com.example.healthsdkdemoforauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.heytap.databaseengine.HeytapHealthApi;
import com.heytap.databaseengine.apiv2.HResponse;
import com.heytap.databaseengine.apiv3.DataReadRequest;
import com.heytap.databaseengine.apiv3.business.IHealthDataApi;
import com.heytap.databaseengine.apiv3.data.DataPoint;
import com.heytap.databaseengine.apiv3.data.DataSet;
import com.heytap.databaseengine.apiv3.data.DataType;
import com.heytap.databaseengine.apiv3.data.Element;
import com.heytap.databaseengine.constant.ErrorCode;
import com.heytap.databaseengine.utils.HLog;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DataApiTest {
    private static final String TAG = "SportHealthApiTest";
    private static Context appContext;
    private static IHealthDataApi mApi;

    @BeforeClass
    public static void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        AndroidThreeTen.init(appContext);
        HeytapHealthApi.init(appContext);
        mApi = HeytapHealthApi.getInstance().dataApi();
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
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_DAILY_ACTIVITY_COUNT)
                .setTimeRange(startTime, endTime)
                .build();
        MutableLiveData<Integer> testFlag = new MutableLiveData<>();
        TestResponse<List<DataSet>> response = new TestResponse<>(testFlag);
        mApi.read(readRequest, response);

        //30天数据申请
        startTime += 1;
        readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_DAILY_ACTIVITY_COUNT)
                .setTimeRange(startTime, endTime)
                .build();
        MutableLiveData<Integer> testFlag2 = new MutableLiveData<>();
        response = new TestResponse<>(testFlag2);
        mApi.read(readRequest, response);

        CommonModule.ServerResponseDelay(1000);
        assertNotNull(testFlag.getValue());
        assertNotNull(testFlag2.getValue());
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
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_LAT_LONG)
                .setTimeRange(startTime, endTime)
                .build();
        MutableLiveData<Integer> testFlag = new MutableLiveData<>();
        TestResponse<List<DataSet>> response = new TestResponse<>(testFlag);
        mApi.read(readRequest, response);

        CommonModule.ServerResponseDelay(1000);
        assertNotNull(testFlag.getValue());
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
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_DAILY_ACTIVITY)
                .setTimeRange(startTime, endTime)
                .build();
        MutableLiveData<Integer> testFlag = new MutableLiveData<>();
        testFlag.postValue(0);
        for (int i = 0;i < 30;i++) {
            mApi.read(readRequest, new HResponse<List<DataSet>>() {
                @Override
                public void onSuccess(List<DataSet> list) {
                    testFlag.postValue(testFlag.getValue());
                }

                @Override
                public void onFailure(int i) {
                    Log.e(TAG, "test flag: " + testFlag.getValue());
                    assertNotNull(testFlag.getValue());
                    testFlag.postValue(testFlag.getValue() + i);
                }
            });
        }

        CommonModule.ServerResponseDelay(5000);
        Log.d(TAG, "test flag: " + testFlag.getValue());
        assertNotNull(testFlag.getValue());
        assertTrue(testFlag.getValue() < 1);
    }

    /**
     * 该项测试前提是账号中有30天份的满数据
     */
    @Test
    public void SportDataDetailTest() {
        long startTime = LocalDateTime.of(2021, 10, 1, 8, 0, 0,0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endTime = LocalDateTime.of(2021, 10, 30, 18, 0, 0,0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_DAILY_ACTIVITY)
                .setTimeRange(startTime, endTime)
                .build();
        long[] testTime = {endTime};
        MutableLiveData<Integer> testFlag = new MutableLiveData<>();
        testFlag.postValue(0);
        try {
            mApi.read(readRequest, new HResponse<List<DataSet>>() {
                @Override
                public void onSuccess(List<DataSet> sportDataDetailList) {
                    int dayCount = 0;
                    for (DataSet dataSet: sportDataDetailList) {
                        for (DataPoint dataPoint : dataSet.getDataPoints()) {
                            if (dayCount < 10) {
                                assertEquals(testTime[0] -= 60 * 60 * 1000L, dataPoint.getStartTimeStamp());
                                dayCount++;
                            } else {
                                assertEquals(testTime[0] -= 15 * 60 * 60 * 1000L, dataPoint.getStartTimeStamp());
                                dayCount = 0;
                            }
                            assertEquals(100, dataPoint.getValue(Element.ELEMENT_STEP).asInt());
                            assertEquals(0, dataPoint.getValue(Element.ELEMENT_DISTANCE).asInt());
                            assertEquals(1000, dataPoint.getValue(Element.ELEMENT_CALORIE).asInt());
                        }
                    }
                    testFlag.postValue(testFlag.getValue());
                }

                @Override
                public void onFailure(int i) {
                    Log.e(TAG, "test flag: " + testFlag.getValue());
                    assertNotNull(testFlag.getValue());
                    testFlag.postValue(testFlag.getValue() + i);
                }
            });
        } catch (Exception e) {
            HLog.e(TAG, e.toString());
        }

        CommonModule.ServerResponseDelay(5000);
        Log.d(TAG, "test flag: " + testFlag.getValue());
        assertNotNull(testFlag.getValue());
        assertTrue(testFlag.getValue() < 1);
    }

    private static class TestResponse<T> implements HResponse<T> {
        private final MutableLiveData<Integer> testFlag;

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