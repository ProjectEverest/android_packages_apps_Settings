/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.network.telephony;

import static com.android.settings.core.BasePreferenceController.AVAILABLE;
import static com.android.settings.core.BasePreferenceController.UNSUPPORTED_ON_DEVICE;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.os.Looper;
import android.telephony.TelephonyManager;

import androidx.lifecycle.LifecycleOwner;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android.settingslib.core.lifecycle.Lifecycle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class CallsDefaultSubscriptionControllerTest {

    private static final int SUB_ID_1 = 1;

    // Copied from {@link com.android.settings.network.NetworkProviderCallsSmsFragment#KEY_PREFERENCE_CALLS}.
    private static final String KEY_PREFERENCE_CALLS = "provider_model_calls_preference";

    @Mock
    private Lifecycle mLifecycle;
    @Mock
    private LifecycleOwner mLifecycleOwner;
    @Mock
    private TelephonyManager mTelephonyManager;

    private Context mContext;
    private CallsDefaultSubscriptionController mController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        mContext = spy(ApplicationProvider.getApplicationContext());
        when(mContext.getSystemService(TelephonyManager.class)).thenReturn(mTelephonyManager);
        mController = new CallsDefaultSubscriptionController(mContext, KEY_PREFERENCE_CALLS,
                mLifecycle, mLifecycleOwner);
    }


    @Test
    public void getAvailabilityStatus_VoiceCapable_returnAvailable() {
        when(mTelephonyManager.isVoiceCapable()).thenReturn(true);

        assertThat(mController.getAvailabilityStatus(SUB_ID_1)).isEqualTo(AVAILABLE);
    }

    @Test
    public void getAvailabilityStatus_NotVoiceCapable_returnUnSupportedOnDevice() {
        when(mTelephonyManager.isVoiceCapable()).thenReturn(false);

        assertThat(mController.getAvailabilityStatus(SUB_ID_1)).isEqualTo(UNSUPPORTED_ON_DEVICE);
    }
}