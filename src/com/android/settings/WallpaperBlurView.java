/*
 * Copyright (C) 2021 Project Radiant
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

package com.android.settings;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.util.AttributeSet;

public class WallpaperBlurView extends androidx.appcompat.widget.AppCompatImageView {

    private Context context;

    public WallpaperBlurView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    public WallpaperBlurView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public WallpaperBlurView(Context context) {
        super(context);
        initialize(context);
    }

    private void initialize(Context context) {
        this.context = context;
        setRenderEffect(RenderEffect.createBlurEffect(925f, 925f, Shader.TileMode.CLAMP));
        updateWallpaper();
    }

    private void updateWallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        Bitmap wallpaperBitmap = wallpaperManager.getBitmap();
        setImageBitmap(wallpaperBitmap);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateWallpaper();
    }
}

