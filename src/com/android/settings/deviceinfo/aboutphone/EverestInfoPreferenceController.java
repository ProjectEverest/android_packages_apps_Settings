/*
 * Copyright (C) 2020 Wave-OS
 * Copyright (C) 2021 ShapeShiftOS
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

package com.android.settings.deviceinfo.aboutphone;

import java.io.IOException;
import android.content.Context;
import android.os.SystemProperties;
import android.widget.TextView;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.utils.EverestSpecUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;
import com.android.settingslib.Utils;
import com.android.settings.core.PreferenceControllerMixin;

public class EverestInfoPreferenceController extends AbstractPreferenceController {

    private static final String KEY_EVEREST_INFO = "everest_info";

    public EverestInfoPreferenceController(Context context) {
        super(context);
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        final LayoutPreference everestInfoPreference = screen.findPreference(KEY_EVEREST_INFO);
        final TextView processor = (TextView) everestInfoPreference.findViewById(R.id.processor_message);
        final TextView storage = (TextView) everestInfoPreference.findViewById(R.id.storage_code_message);
        final TextView battery = (TextView) everestInfoPreference.findViewById(R.id.battery_type_message);
        final TextView infoScreen = (TextView) everestInfoPreference.findViewById(R.id.screen_message);
        processor.setText(EverestSpecUtils.getProcessorModel());
        storage.setText(String.valueOf(EverestSpecUtils.getTotalInternalMemorySize()) + "GB ROM + " + String.valueOf(EverestSpecUtils.getTotalRAM()) + "GB RAM");
        battery.setText(EverestSpecUtils.getBatteryCapacity(mContext) + " mAh");
        infoScreen.setText(EverestSpecUtils.getScreenRes(mContext));
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_EVEREST_INFO;
    }
}
