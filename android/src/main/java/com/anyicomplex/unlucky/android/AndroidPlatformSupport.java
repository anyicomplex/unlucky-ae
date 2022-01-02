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

import com.anyicomplex.unlucky.util.PlatformSupport;
import com.badlogic.gdx.Gdx;

public class AndroidPlatformSupport extends PlatformSupport {

    private final AndroidLauncher androidLauncher;

    public AndroidPlatformSupport(AndroidLauncher androidLauncher) {
        this.androidLauncher = androidLauncher;
    }

    @Override
    public void openURIFromString(String uri) {
        Gdx.net.openURI(uri);
    }

    @Override
    public void fullscreenMode() {
        androidLauncher.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                androidLauncher.updateImmersiveMode();
            }
        });
    }

    @Override
    public void windowedMode() {
        androidLauncher.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                androidLauncher.updateImmersiveMode();
            }
        });
    }

    @Override
    public boolean isFullscreenMode() {
        return androidLauncher.settings.fullscreen;
    }

}