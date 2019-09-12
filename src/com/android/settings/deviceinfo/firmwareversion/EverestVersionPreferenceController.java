/*
 * Copyright (C) 2019 The EverestOS Project
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

package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.os.SystemProperties;
import android.text.TextUtils;

import androidx.annotation.VisibleForTesting;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;

public class EverestVersionPreferenceController extends BasePreferenceController {

    @VisibleForTesting
    static final String EVEREST_VERSION_PROPERTY = "ro.everest.base.version";
    static final String EVEREST_BUILDTYPE_PROPERTY = "ro.everest.buildtype";
    static final String EVEREST_EDITION_PROPERTY = "ro.everest.edition";

    public EverestVersionPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
    }

    @Override
    public int getAvailabilityStatus() {
        return !TextUtils.isEmpty(SystemProperties.get(EVEREST_VERSION_PROPERTY)) && !TextUtils.isEmpty(SystemProperties.get(EVEREST_BUILDTYPE_PROPERTY)) && !TextUtils.isEmpty(SystemProperties.get(EVEREST_EDITION_PROPERTY))
                ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public CharSequence getSummary() {
        String everestVersion = SystemProperties.get(EVEREST_VERSION_PROPERTY);
        String everestBuildType = SystemProperties.get(EVEREST_BUILDTYPE_PROPERTY);
        String everestEdition = SystemProperties.get(EVEREST_EDITION_PROPERTY);
        if (!everestVersion.isEmpty() && !everestBuildType.isEmpty() && !everestEdition.isEmpty()) {
            return everestVersion + " | " + everestBuildType + " | " + everestEdition;
        } else {
            return
                mContext.getString(R.string.device_info_default);
        }
    }
}
