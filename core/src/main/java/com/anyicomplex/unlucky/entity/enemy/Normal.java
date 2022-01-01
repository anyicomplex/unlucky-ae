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

package com.anyicomplex.unlucky.entity.enemy;

import com.anyicomplex.unlucky.animation.AnimationManager;
import com.anyicomplex.unlucky.map.TileMap;
import com.anyicomplex.unlucky.resource.ResourceManager;
import com.anyicomplex.unlucky.resource.Util;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * A normal enemy that can either be an elite or regular version
 *
 * @author Ming Li
 */
public class Normal extends Enemy {

    // chance for enemy to be elite which has higher stats than normal
    public boolean isElite = false;

    public Normal(String id, Vector2 position, TileMap tileMap, ResourceManager rm) {
        super(id, position, tileMap, rm);
    }

    public Normal(String id, Vector2 position, TileMap tileMap, ResourceManager rm,
                  int worldIndex, int startIndex, int numFrames, float delay) {
        this(id, position, tileMap, rm);

        // create tilemap animation
        am = new AnimationManager(rm.sprites16x16, worldIndex, startIndex, numFrames, delay);
        // create battle scene animation
        bam = new AnimationManager(rm.battleSprites96x96, worldIndex, startIndex, 2, delay);

        // determine if elite
        isElite = Util.isSuccess(Util.ELITE_CHANCE);
        if (isElite) this.id = "[ELITE] " + id;
    }

    @Override
    public boolean isElite() {
        return isElite;
    }

    @Override
    public boolean isBoss() {
        return false;
    }

    @Override
    public void setStats() {
        // if the enemy is an elite then its stats are multiplied by an elite multiplier
        float eliteMultiplier = MathUtils.random(Util.MIN_ELITE_MULTIPLIER, Util.MAX_ELITE_MULTIPLIER);

        // hp is scaled polynomially with curve MHP = level ^ 2.0 + 25 as a seed then a value is chosen from deviation
        int mhpSeed = (int) (Math.pow(level, 2) + 25);
        int mhp = Util.getDeviatedRandomValue(mhpSeed, 4);

        int minDmg = MathUtils.random(Util.ENEMY_INIT_MIN_MINDMG, Util.ENEMY_INIT_MAX_MINDMG);
        int maxDmg = MathUtils.random(Util.ENEMY_INIT_MIN_MAXDMG, Util.ENEMY_INIT_MAX_MAXDMG);

        for (int i = 0; i < this.level - 1; i++) {
            int dmgMean = MathUtils.random(Util.ENEMY_MIN_DMG_INCREASE, Util.ENEMY_MAX_DMG_INCREASE);
            int minDmgIncrease = (dmgMean - MathUtils.random(2));
            int maxDmgIncrease = (dmgMean + MathUtils.random(2));

            minDmg += minDmgIncrease;
            maxDmg += maxDmgIncrease;
        }

        // sets a random accuracy initially
        this.setAccuracy(MathUtils.random(Util.ENEMY_MIN_ACCURACY, Util.ENEMY_MAX_ACCURACY));

        // finalize stats
        this.setMaxHp(isElite ? (int) (eliteMultiplier * mhp) : mhp);
        this.setMinDamage(isElite ? (int) (eliteMultiplier * minDmg) : minDmg);
        this.setMaxDamage(isElite ? (int) (eliteMultiplier * maxDmg) : maxDmg);
    }

}
