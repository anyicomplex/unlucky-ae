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
import com.anyicomplex.unlucky.resource.ResourceManager;
import com.anyicomplex.unlucky.resource.Util;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Stores and manages particles
 * Creates particle effects such as rain and snow
 *
 * @author Ming Li
 */
public class ParticleFactory {

    // type of particle being generated
    public int type;
    // max num of particles to generate
    public int numParticles;
    // velocity of particles
    public Vector2 velocity;

    // data
    public Array<Particle> particles;
    // pool
    public Pool<Particle> particlePool;

    private Vector2 particleVelocity = new Vector2();

    // camera viewport
    private int viewWidth;
    private int viewHeight;

    private OrthographicCamera cam;
    private final ResourceManager rm;

    public ParticleFactory(OrthographicCamera cam, final ResourceManager rm) {
        this.cam = cam;
        this.viewWidth = (int) cam.viewportWidth;
        this.viewHeight = (int) cam.viewportHeight;
        this.rm = rm;

        particles = new Array<Particle>();
        particlePool = new Pool<Particle>() {
            @Override
            protected Particle newObject() {
                return new Particle();
            }
        };
    }

    public void update(float dt) {
        // update all particles and free them if necessary
        for (int i = particles.size - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update(dt);
            if (p.shouldRemove) {
                if (p.deathAnim != null) {
                    if (p.deathAnim.currentAnimation.isAnimationFinished()) {
                        particles.removeIndex(i);
                        particlePool.free(p);
                        spawn();
                    }
                }
                else {
                    particles.removeIndex(i);
                    particlePool.free(p);
                    spawn();
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        // render all particles
        for (int i = 0; i < particles.size; i++) {
            particles.get(i).render(batch);
        }
    }

    /**
     * Resets the factory to generate a new type of particle
     *
     * @param type
     * @param numParticles
     */
    public void set(int type, int numParticles, Vector2 velocity) {
        particles.clear();
        particlePool.clear();
        this.type = type;
        this.numParticles = numParticles;
        this.velocity = velocity;
        populate();
    }

    /**
     * Spawns one particle of a certain type from the Pool
     */
    public void spawn() {
        Particle item = particlePool.obtain();
        Vector2 weatherParticleSpawnPosition = new Vector2(cam.position.x + MathUtils.random(-viewWidth / 2, viewWidth / 2),
                cam.position.y + MathUtils.random(-viewHeight / 2, viewHeight / 2));
        switch (type) {
            case Particle.RAINDROP:
                float rls = MathUtils.random(0.4f, 1.4f);
                particleVelocity.set(this.velocity.x,
                        Util.getDeviatedRandomValue((int) this.velocity.y, Util.RAINDROP_Y_DEVIATED));
                AnimationManager rainAnim = new AnimationManager(rm.raindropAnim16x16, 3, 0, 1 / 6f);
                item.init(type, weatherParticleSpawnPosition, particleVelocity, rls, rm.raindrop, rainAnim);
                break;
            case Particle.SNOWFLAKE:
                float sls = MathUtils.random(0.3f, 1.4f);
                particleVelocity.set(this.velocity.x,
                        Util.getDeviatedRandomValue((int) this.velocity.y, Util.SNOWFLAKE_Y_DEVIATED));
                item.init(type, weatherParticleSpawnPosition, particleVelocity, sls, rm.snowflake, null);
                break;
            case Particle.STATIC_RAINDROP:
                float srls = MathUtils.random(0.4f, 1.4f);
                particleVelocity.set(this.velocity.x,
                    Util.getDeviatedRandomValue((int) this.velocity.y, Util.RAINDROP_Y_DEVIATED));
                item.init(type, weatherParticleSpawnPosition, particleVelocity, srls, rm.raindrop, null);
                break;
        }
        particles.add(item);
    }

    /**
     * Populates the array of particles with an initial
     * numParticles amount of particles
     */
    public void populate() {
        for (int i = 0; i < numParticles; i++) {
            spawn();
        }
    }

}
