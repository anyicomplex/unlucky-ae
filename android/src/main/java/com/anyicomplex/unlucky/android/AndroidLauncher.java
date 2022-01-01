/*
 *   Copyright (C) 2021 Yi An
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *   Original project's License:
 *
 *   MIT License
 *
 *   Copyright (c) 2018 Ming Li
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package com.anyicomplex.unlucky.android;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import com.anyicomplex.unlucky.Unlucky;
import com.anyicomplex.unlucky.save.PlayerAccessor;
import com.anyicomplex.unlucky.save.Settings;
import com.anyicomplex.unlucky.util.PlatformSupport;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidPreferences;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {

	protected Settings settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updateImmersiveMode();
		AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
		configuration.useAccelerometer = false;
		configuration.useCompass = false;
		try {
			ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), 0);
			Unlucky.APP_NAME = (String) getPackageManager().getApplicationLabel(applicationInfo);
			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			Unlucky.VERSION = packageInfo.versionName;
			Unlucky.VERSION_CODE = packageInfo.versionCode;
			if (Unlucky.VERSION_CODE < 10) Unlucky.VERSION_CODE_STRING = "00" + Unlucky.VERSION_CODE;
			else if (Unlucky.VERSION_CODE < 100) Unlucky.VERSION_CODE_STRING = "0" + Unlucky.VERSION_CODE;
			Unlucky.TITLE = Unlucky.APP_NAME + " v" + Unlucky.VERSION + " [Build " + Unlucky.VERSION_CODE_STRING + "]";
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		Unlucky.DISABLE_FULLSCREEN = Build.VERSION.SDK_INT < 19;
		Unlucky.DISABLE_PAD = false;
		Unlucky.DISABLE_CURSOR = true;
		Unlucky unlucky = new Unlucky();
		PlatformSupport.setHandler(new AndroidPlatformSupport(this));
		Settings settings = null;
		try {
			Preferences wrapper = new AndroidPreferences(getSharedPreferences("save", MODE_PRIVATE));
			String jsonString = wrapper.getString("save", "NODATA");
			if (!jsonString.equals("NODATA")) {
				PlayerAccessor psave = new Json().fromJson(PlayerAccessor.class, Base64Coder.decodeString(jsonString));
				settings = psave.settings;
			}
		}
		catch (Exception ignored) {
		}
		if (settings == null) settings = new Settings();
		unlucky.preLoadSettings = settings;
		this.settings = settings;
		initialize(unlucky, configuration);
	}

	@SuppressLint("NewApi")
	public void updateImmersiveMode() {
		if (Build.VERSION.SDK_INT >= 19) {
			try {
				// Sometime NullPointerException happens here
				getWindow().getDecorView().setSystemUiVisibility(
						settings.fullscreen ?
								View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
										View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
										View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
										View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
										View.SYSTEM_UI_FLAG_FULLSCREEN |
										View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
								:
								0 );
			} catch (Exception ignored) {

			}
		}
	}

	@Override
	public void onWindowFocusChanged( boolean hasFocus ) {

		super.onWindowFocusChanged( hasFocus );

		if (hasFocus) {
			updateImmersiveMode();
		}
	}

}