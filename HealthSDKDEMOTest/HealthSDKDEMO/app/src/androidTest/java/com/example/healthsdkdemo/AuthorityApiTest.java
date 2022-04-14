package com.example.healthsdkdemoforauth;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.heytap.databaseengine.HeytapHealthApi;
import com.heytap.databaseengine.apiv2.HResponse;
import com.heytap.databaseengine.apiv2.auth.IAuthorityApi;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AuthorityApiTest {
    private static final String TAG = "AuthorityApiTest";
    private String[] authList = new String[]{"READ_DAILY_ACTIVITY", "READ_HEART_RATE"};
    private static Context appContext;
    private static IAuthorityApi mApi;

    @BeforeClass
    public static void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        HeytapHealthApi.init(appContext);
        mApi = HeytapHealthApi.getInstance().authorityApi();
    }

    @Test
    public void PackageNameTest() {
        // Context of the app under test.
        assertEquals("com.example.healthsdkdemo", appContext.getPackageName());
    }

    @Test
    public void authorityApiValidTest() {
        MutableLiveData<List<String>> testList = new MutableLiveData<>();
        mApi.valid(new HResponse<List<String>>() {
            @Override
            public void onSuccess(List<String> strings) {
                testList.postValue(strings);
            }

            @Override
            public void onFailure(int i) {
                Log.e(TAG, "authorityApi error code: " + i);
            }
        });

        CommonModule.ServerResponseDelay(1000);
        List<String> strings = testList.getValue();
        assertNotNull(strings);
        assertEquals(authList.length, strings.size());
        strings.sort(String::compareTo);
        String[] stringArray = new String[strings.size()];
        strings.toArray(stringArray);
        assertArrayEquals(authList, stringArray);
    }

    /*
    * 待授权列表启用后，进行测试
    */
//    @Test
//    public void authorityApiRevokeTest() {
//        mApi.revoke(new HResponse<List<Object>>() {
//            @Override
//            public void onSuccess(List<Object> objects) {
//                assertNotNull(objects);
//                assertEquals(0, objects.size());
//                mApi.valid(new HResponse<List<String>>() {
//                    @Override
//                    public void onSuccess(List<String> list) {
//                        throw new AssertionFailedError("revoke failed");
//                    }
//
//                    @Override
//                    public void onFailure(int i) {
//                        assertEquals(ErrorCode.ERR_PERMISSION_DENY, i);
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(int i) {
//                assertEquals(ErrorCode.ERR_PERMISSION_DENY, i);
//            }
//        });
//    }
}