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

package com.anyicomplex.unlucky.effects;

import com.anyicomplex.unlucky.animation.AnimationManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

/**
 * A particle that has velocity
 * Can be animated or non-animated
 *
 * @author Ming Li
 */
public class Particle implements Pool.Poolable {

    // types of particles
    public static final byte RAINDROP = 0;
    public static final byte SNOWFLAKE = 1;
    public static final byte STATIC_RAINDROP = 2;

    // a particle's position relative to a camera
    public Vector2 position;
    // a particle's velocity
    public Vector2 velocity;

    public int type;
    public boolean animated;

    // for static particles
    public TextureRegion sprite;
    // for animated particles
    public AnimationManager anim;

    // a particle's "death" animation which is played after the particle should be removed
    public AnimationManager deathAnim;

    // a particle's lifespan
    public float lifespan;
    // end particle's life
    public boolean shouldRemove = false;

    private float stateTime = 0;

    /**
     * Default constructor
     */
    public Particle() {}

    /**
     * Initializes a non-animated particle's components
     *
     * @param position
     * @param velocity
     * @param lifespan
     */
    public void init(int type, Vector2 position, Vector2 velocity, float lifespan, TextureRegion sprite, AnimationManager deathAnim) {
        shouldRemove = false;
        this.type = type;
        this.position = position;
        this.velocity = velocity;
        this.lifespan = lifespan;
        animated = false;
        this.sprite = sprite;
        this.deathAnim = deathAnim;
    }

    /**
     * Initializes an animated particle's components
     *
     * @param type
     * @param position
     * @param velocity
     * @param lifespan
     * @param numFrames
     * @param delay
     */
    public void init(int type, Vector2 position, Vector2 velocity, float lifespan, int numFrames, float delay) {
        init(type, position, velocity, lifespan, null, deathAnim);
        animated = true;
        if (anim == null) anim = null;
    }

    /**
     * Resets the properties of the particle each time it's freed
     * and put back into the Pool
     */
    @Override
    public void reset() {
        shouldRemove = false;
        this.position.set(0, 0);
        this.velocity.set(0, 0);
        this.lifespan = 0;
    }

    public void update(float dt) {
        // update position
        if (!shouldRemove) {
            position.x += velocity.x * dt;
            position.y += velocity.y * dt;
        }

        stateTime += dt;
        if (stateTime >= lifespan) {
            stateTime = 0;
            shouldRemove = true;
        }

        if (shouldRemove) {
            if (deathAnim != null) deathAnim.update(dt);
        }
        else {
            if (animated) anim.update(dt);
        }
    }

    public void render(SpriteBatch batch) {
        if (shouldRemove) {
            if (deathAnim != null) batch.draw(deathAnim.getKeyFrame(false), position.x, position.y);
        }
        else {
            if (animated && anim != null) batch.draw(anim.getKeyFrame(true), position.x, position.y);
            else {
                if (sprite != null) batch.draw(sprite, position.x, position.y);
            }
        }
    }

}
