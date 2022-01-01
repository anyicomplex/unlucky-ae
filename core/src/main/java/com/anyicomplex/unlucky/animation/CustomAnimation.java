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

package com.anyicomplex.unlucky.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Extension of the LibGDX Animation class which replaces the stateTime argument with various methods.
 *
 * @author Ivan Vinski
 * @since 1.0
 */
public class CustomAnimation extends Animation {

    private float stateTime;
    private boolean playing;

    public CustomAnimation(float frameDuration, Array keyFrames) {
        super(frameDuration, keyFrames);
    }

    public CustomAnimation(float frameDuration, Array keyFrames, PlayMode playMode) {
        super(frameDuration, keyFrames, playMode);
    }

    public CustomAnimation(float frameDuration, Object[] keyFrames) {
        super(frameDuration, keyFrames);
    }

    public void play() {
        if (!playing) {
            playing = true;
        }
    }

    public void pause() {
        if (playing) {
            playing = false;
        }
    }

    public void stop() {
        if (stateTime != 0f) {
            stateTime = 0f;
        }
        if (playing) {
            playing = false;
        }
    }

    public void update(float delta) {
        if (playing) {
            stateTime += delta;
        }
    }

    public void reset() {
        if (stateTime != 0) stateTime = 0;
    }

    public TextureRegion getKeyFrame(boolean looping) {
        return (TextureRegion) getKeyFrame(stateTime, looping);
    }

    public TextureRegion getKeyFrame() {
        return (TextureRegion) getKeyFrame(stateTime);
    }

    public int getKeyFrameIndex() {
        return getKeyFrameIndex(stateTime);
    }

    public boolean isAnimationFinished() {
        return isAnimationFinished(stateTime);
    }

    public boolean isPlaying() {
        return playing;
    }

}