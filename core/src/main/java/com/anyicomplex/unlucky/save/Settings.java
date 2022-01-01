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

package com.anyicomplex.unlucky.save;

import com.anyicomplex.unlucky.Unlucky;

/**
 * Stores the settings of the game including volume of music
 * and sfx, animation toggles, etc.
 *
 * @author Ming Li
 */
public class Settings {

    // volume
    // values between 0.f and 1.f (min and max)
    public volatile float musicVolume = 1.f;
    public volatile float sfxVolume = 1.f;
    public volatile boolean muteMusic = false;
    public volatile boolean muteSfx = false;

    // toggles
    public volatile boolean showEnemyLevels = false;
    public volatile boolean showWeatherAnimations = true;
    public volatile boolean showFps = false;

    // desktop
    public volatile boolean fullscreen = false;
    public volatile boolean maximized = false;
    public volatile int width = Unlucky.V_WIDTH * Unlucky.V_SCALE;
    public volatile int height = Unlucky.V_HEIGHT * Unlucky.V_SCALE;
    public volatile int posX = Integer.MIN_VALUE;
    public volatile int posY = Integer.MIN_VALUE;
    public volatile int lastWidth, lastHeight, lastPosX, lastPosY;

}
