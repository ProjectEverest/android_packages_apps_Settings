/*
 * Copyright (C) 2017 The Android Open Source Project
 * Copyright (C) 2020 The "Best Improved Cherry Picked Rom" Project
 * Copyright (C) 2020 Project Fluid
 * Copyright (C) 2021 ShapeShiftOS
 * Copyright (C) 2024 SuperiorExtended-OS
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
package com.android.settings.utils;

import android.content.Context;
import android.os.SystemProperties;
import android.os.Environment;
import android.os.StatFs;
import android.view.Display;
import android.view.WindowManager;
import android.util.DisplayMetrics;
import android.graphics.Point;

import com.android.internal.os.PowerProfile;
import com.android.internal.util.MemInfoReader;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.android.settings.R;

public class EverestSpecUtils {
    private static final String DEVICE_NAME_MODEL_PROPERTY = "ro.product.system.model";
    private static final String EVEREST_CPU_MODEL_PROPERTY = "ro.everest.cpu";
    private static final String FALLBACK_CPU_MODEL_PROPERTY = "ro.board.platform";
    private static final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
    private static final BigDecimal GB2MB = new BigDecimal(1024);
    private static final int GB_APPROXIMATION_THRESHOLD = 512;

    public static String getProcessorModel(Context context) {
        // Use the custom string resource if available
        String customProcessorModel = context.getString(R.string.CUSTOM_CPU_MODEL);
        if (!customProcessorModel.isEmpty()) {
            return customProcessorModel;
        }

        // Fallback to fetching from system properties
        String cpuModelEverest = SystemProperties.get(EVEREST_CPU_MODEL_PROPERTY);
        String cpuModelFallback = SystemProperties.get(FALLBACK_CPU_MODEL_PROPERTY);

        if (!cpuModelEverest.isEmpty()) {
            return cpuModelEverest;
        } else if (!cpuModelFallback.isEmpty()) {
            return cpuModelFallback;
        } else {
            return "unknown";
        }
    }

    public static String getScreenRes(Context context) {
        // Use the custom string resource if available
        String customScreenRes = context.getString(R.string.CUSTOM_SCREEN_RESOLUTION);
        if (!customScreenRes.isEmpty()) {
            return customScreenRes;
        }

        // Fallback to fetching actual screen resolution
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y + getNavigationBarHeight(windowManager);
        return width + " x " + height;
    }

    public static String getBatteryInfo(Context context) {
        // Use the custom string resource if available
        String customBatteryInfo = context.getString(R.string.CUSTOM_BATTERY_INFO);
        if (!customBatteryInfo.isEmpty()) {
            return customBatteryInfo;
        }

        // Fallback to fetching battery capacity
        return getBatteryCapacity(context) + " mAh";
    }

    public static String getStorageInfo(Context context) {
        // Use the custom string resource if available
        String customStorageInfo = context.getString(R.string.CUSTOM_STORAGE_INFO);
        if (!customStorageInfo.isEmpty()) {
            return customStorageInfo;
        }

        // Fallback to fetching internal storage size
        return "Internal Storage: " + getTotalInternalMemorySize(context);
    }

    public static String getRAMInfo(Context context) {
        // Use the custom string resource if available
        String customRAMInfo = context.getString(R.string.CUSTOM_RAM_INFO);
        if (!customRAMInfo.isEmpty()) {
            return customRAMInfo;
        }

        // Fallback to fetching total RAM
        return "RAM: " + getTotalRAM(context);
    }
    
    public static String getStorageAndRAMInfo(Context context) {
    	// Use the custom string resource if available
    	String customStorageInfo = context.getString(R.string.CUSTOM_STORAGE_INFO);
    	String customRAMInfo = context.getString(R.string.CUSTOM_RAM_INFO);

    	if (!customStorageInfo.isEmpty() && !customRAMInfo.isEmpty()) {
       	    return customStorageInfo + " | " + customRAMInfo;
    	}

    	// Fallback to fetching internal storage size and total RAM
    	String storageInfo = getTotalInternalMemorySize(context);
    	String ramInfo = getTotalRAM(context);
    		return storageInfo + " ROM | " + ramInfo + " RAM";
	}

    private static int getBatteryCapacity(Context context) {
        Object powerProfile = null;

        try {
            powerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        double batteryCapacity = getBatteryCapacityValue(powerProfile);

        // If battery capacity is still 0, return a default value or handle accordingly
        if (batteryCapacity <= 0) {
            return getDefaultBatteryCapacity();
        }

        // Convert the capacity to an integer
        String str = Double.toString(batteryCapacity);
        String strArray[] = str.split("\\.");
        int batteryCapacityInt = Integer.parseInt(strArray[0]);

        return batteryCapacityInt;
    }

    private static double getBatteryCapacityValue(Object powerProfile) {
        if (powerProfile != null) {
            try {
                // Attempt to get battery capacity using getBatteryCapacity method
                double batteryCapacity = (Double) Class
                        .forName(POWER_PROFILE_CLASS)
                        .getMethod("getBatteryCapacity")
                        .invoke(powerProfile);

                if (batteryCapacity > 0) {
                    return batteryCapacity;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // If the initial attempt fails, try an alternative method (e.g., fallback property)
            try {
                double batteryCapacity = (Double) Class
                        .forName(POWER_PROFILE_CLASS)
                        .getMethod("getAveragePower", java.lang.String.class)
                        .invoke(powerProfile, "battery.capacity");

                if (batteryCapacity > 0) {
                    return batteryCapacity;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    private static int getDefaultBatteryCapacity() {
        // Provide a default value (e.g., 3000 mAh) or handle accordingly
        return 3000;
    }

    private static int getNavigationBarHeight(WindowManager wm) {
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        wm.getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight)
            return realHeight - usableHeight;
        else
            return 0;
    }

    private static String getTotalInternalMemorySize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        double totalGB = (totalBlocks * blockSize) / (1024.0 * 1024.0 * 1024.0);
        return approximateStorageSize(totalGB);
    }

    private static String approximateStorageSize(double totalGB) {
        if (totalGB > 0 && totalGB <= 16) {
            return "16 GB";
        } else if (totalGB <= 32) {
            return "32 GB";
        } else if (totalGB <= 64) {
            return "64 GB";
        } else if (totalGB <= 128) {
            return "128 GB";
        } else if (totalGB <= 256) {
            return "256 GB";
        } else if (totalGB <= 512) {
            return "512 GB";
        } else {
            return "512+ GB";
        }
    }

    private static String getTotalRAM(Context context) {
        MemInfoReader memReader = new MemInfoReader();
        memReader.readMemInfo();
        long totalMem = memReader.getTotalSize();
        long totalMemMiB = totalMem / (1024 * 1024);
        BigDecimal rawVal = new BigDecimal(totalMemMiB);
        return rawVal.divide(GB2MB, 0, RoundingMode.UP) + " GB";
    }
}
