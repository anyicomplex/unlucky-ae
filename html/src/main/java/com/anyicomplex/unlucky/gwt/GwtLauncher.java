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

package com.anyicomplex.unlucky.gwt;

import com.anyicomplex.unlucky.Unlucky;
import com.anyicomplex.unlucky.util.OpenURI;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.GwtGraphics;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
		@Override
		public GwtApplicationConfiguration getConfig () {
			// Resizable application, uses available space in browser with no padding:
			GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(true);
			cfg.padVertical = 0;
			cfg.padHorizontal = 0;
			cfg.fullscreenOrientation = GwtGraphics.OrientationLockType.LANDSCAPE;
			cfg.antialiasing = false;
			return cfg;
			// If you want a fixed size application, comment out the above resizable section,
			// and uncomment below:
			//return new GwtApplicationConfiguration(640, 480);
		}

		@Override
		public ApplicationListener createApplicationListener () {
			OpenURI.setHandler(new GwtOpenURIHandler());
			Unlucky.DISABLE_PAD = !GwtApplication.isMobileDevice();
			Unlucky.DISABLE_CURSOR = GwtApplication.isMobileDevice();
			Unlucky.DISABLE_FULLSCREEN = false;
			Unlucky.APP_NAME = "Unlucky AE";
			Unlucky.VERSION = "1.1.0";
			Unlucky.VERSION_CODE_STRING = "10";
			Unlucky.VERSION_CODE = Integer.parseInt(Unlucky.VERSION_CODE_STRING);
			if (Unlucky.VERSION_CODE < 10) Unlucky.VERSION_CODE_STRING = "00" + Unlucky.VERSION_CODE;
			else if (Unlucky.VERSION_CODE < 100) Unlucky.VERSION_CODE_STRING = "0" + Unlucky.VERSION_CODE;
			Unlucky.TITLE = Unlucky.APP_NAME + " v" + Unlucky.VERSION + " [Build " + Unlucky.VERSION_CODE_STRING + "]";
			return new Unlucky();
		}

}
