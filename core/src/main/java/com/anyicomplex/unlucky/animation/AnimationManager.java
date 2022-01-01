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

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * This class manages animations CustomAnimations for entities that are able to move in all four directions-
 * up, down, left, right, and entities that have any number of animations. It retrieves animation states
 * and plays and stops animations accordingly.
 *
 * @author Ming Li
 */
public class AnimationManager {

    public float width;
    public float height;

    // animation holder instance
    public CustomAnimation currentAnimation;

    // for multi-directional animations
    // 0 - up, 1 - down, 2 - left, 3 - right
    private CustomAnimation[] animations;
    private TextureRegion[][] animationFrames;

    /**
     * Sets up for single animations
     *
     * @param sprites 2d array of sprites so that different sized animations can be used
     * @param numFrames the amount of frames in the single animation
     * @param delay
     */
    public AnimationManager(TextureRegion[][] sprites, int numFrames, int index, float delay) {
        TextureRegion[] frames = new TextureRegion[numFrames];

        width = sprites[index][0].getRegionWidth();
        height = sprites[index][0].getRegionHeight();

        for (int i = 0; i < numFrames; i++) {
            frames[i] = sprites[index][i];
        }

        currentAnimation = new CustomAnimation(delay, frames);
    }

    /**
     * Sets up for animations based on world index
     * Used for storing multiple animations on a single row sorted by worlds
     *
     * @param sprites
     * @param worldIndex
     * @param startIndex
     * @param numFrames
     * @param delay
     */
    public AnimationManager(TextureRegion[][] sprites, int worldIndex, int startIndex, int numFrames, float delay) {
        TextureRegion[] frames = new TextureRegion[numFrames];

        width = sprites[worldIndex][startIndex].getRegionWidth();
        height = sprites[worldIndex][startIndex].getRegionHeight();

        for (int i = startIndex; i < startIndex + numFrames; i++) {
            frames[i - startIndex] = sprites[worldIndex][i];
        }

        currentAnimation = new CustomAnimation(delay, frames);
    }

    /**
     * Specifically handles four directional animations animations
     *
     * @param sprites
     * @param index
     * @param delay
     */
    public AnimationManager(TextureRegion[][] sprites, int index, float delay) {
        animations = new CustomAnimation[4];
        animationFrames = new TextureRegion[4][4];

        width = sprites[index][0].getRegionWidth();
        height = sprites[index][0].getRegionHeight();

        // converting the animations frames row in the sprite texture to a 2d array to match the animation array
        for (int i = 0; i < sprites[index].length / 4; i++) {
            for (int j = 0; j < animationFrames[0].length; j++) {
                animationFrames[i][j] = sprites[index][(j % 4) + (i * 4)];
            }
        }
        for (int i = 0; i < animations.length; i++) {
            animations[i] = new CustomAnimation(delay, animationFrames[i]);
        }

        currentAnimation = animations[0]; // initially set frame to idle down facing frame
    }

    public void update(float dt) {
        currentAnimation.update(dt);
        currentAnimation.play();
    }

    /**
     * Sets currentAnimation to an animation established in the animation array
     *
     * @param index
     */
    public void setAnimation(int index) {
        currentAnimation = animations[index];
    }

    public void stopAnimation() {
        currentAnimation.stop();
    }

    public TextureRegion getKeyFrame(boolean looping) {
        return currentAnimation.getKeyFrame(looping);
    }


}
