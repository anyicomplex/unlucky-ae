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

package com.anyicomplex.unlucky.parallax;

import com.anyicomplex.unlucky.Unlucky;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Dynamic parallax background for battle scenes or other screens
 *
 * @author Ming Li
 */
public class Background {

    public TextureRegion image;
    private OrthographicCamera cam;
    public Vector2 scale;

    private float ax;
    private float ay;
    private int numDrawX;
    private int numDrawY;

    // bg movement
    private float dx;
    private float dy;

    public Background(OrthographicCamera cam, Vector2 scale) {
        this.cam = cam;
        this.scale = scale;
    }

    public Background(TextureRegion image, OrthographicCamera cam, Vector2 scale) {
        this.image = image;
        this.cam = cam;
        this.scale = scale;
        numDrawX = (Unlucky.V_WIDTH * 2) / image.getRegionWidth() + 1;
        numDrawY = (Unlucky.V_HEIGHT * 2) / image.getRegionHeight() + 1;

        fixBleeding(image);
    }

    public void setImage(TextureRegion image) {
        this.image = image;
        numDrawX = (Unlucky.V_WIDTH * 2) / image.getRegionWidth() + 1;
        numDrawY = (Unlucky.V_HEIGHT * 2) / image.getRegionHeight() + 1;
        fixBleeding(image);
    }

    /**
     * Fixes the slight 1 pixel offset when moving the background to create
     * a smooth cycling image
     *
     * @param region
     */
    public void fixBleeding(TextureRegion region) {
        float fix = 0.01f;

        float x = region.getRegionX();
        float y = region.getRegionY();
        float width = region.getRegionWidth();
        float height = region.getRegionHeight();
        float invTexWidth = 1f / region.getTexture().getWidth();
        float invTexHeight = 1f / region.getTexture().getHeight();
        region.setRegion((x + fix) * invTexWidth, (y + fix) * invTexHeight, (x + width - fix) * invTexWidth, (y + height - fix) * invTexHeight);
    }

    public void setVector(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void update(float dt) {
        ax += (dx * scale.x) * dt;
        ay += (dy * scale.y) * dt;
    }

    public void render(SpriteBatch batch) {
        // bg not moving
        if (dx == 0 && dy == 0) {
            batch.draw(image, 0, 0);
        }
        else {
            float x = ((ax + cam.viewportWidth / 2 - cam.position.x) * scale.x) % image.getRegionWidth();
            float y = ((ay + cam.viewportHeight / 2 - cam.position.y) * scale.y) % image.getRegionHeight();

            int colOffset = x > 0 ? -1 : 0;
            int rowOffset = y > 0 ? -1 : 0;

            for (int row = 0; row < numDrawY; row ++) {
                for (int col = 0; col < numDrawX; col ++) {
                    batch.draw(image,
                        x + (col + colOffset) * image.getRegionWidth(),
                        y + (row + rowOffset) * image.getRegionHeight());
                }
            }
        }
    }

}
