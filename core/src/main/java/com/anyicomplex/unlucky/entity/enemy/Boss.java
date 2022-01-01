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
 * A boss enemy with special attributes
 *
 * @author Ming Li
 */
public class Boss extends Enemy {

    // the unique identifier for bosses
    public int bossId;

    public Boss(String id, Vector2 position, TileMap tileMap, ResourceManager rm) {
        super(id, position, tileMap, rm);
    }

    public Boss(String id, int bossId, Vector2 position, TileMap tileMap, ResourceManager rm,
                int worldIndex, int startIndex, int numFrames, float delay) {
        this(id, position, tileMap, rm);
        this.bossId = bossId;

        // create tilemap animation
        am = new AnimationManager(rm.sprites16x16, worldIndex, startIndex, numFrames, delay);
        // create battle scene animation
        bam = new AnimationManager(rm.battleSprites96x96, worldIndex, startIndex, 2, delay);
    }

    @Override
    public boolean isElite() {
        return false;
    }

    @Override
    public boolean isBoss() {
        return true;
    }

    @Override
    /**
     * Sets the stats of the boss based on level and the boss index
     */
    public void setStats() {
        int mhp = 0;
        int minDmg = 0;
        int maxDmg = 0;

        switch (bossId) {
            case 0: // king slime
                // has lower hp because its passive compensates for it
                int mhpSeed0 = (int) (Math.pow(level, 2.1) + 15);
                mhp = Util.getDeviatedRandomValue(mhpSeed0, 1);
                minDmg = MathUtils.random(5, 9);
                maxDmg = MathUtils.random(10, 15);
                for (int i = 0; i < level - 1; i++) {
                    minDmg += MathUtils.random(2, 4) - MathUtils.random(1);
                    maxDmg += MathUtils.random(2, 4) + MathUtils.random(1);
                }
                break;
            case 1: // red reaper
                int mhpSeed1 = (int) (Math.pow(level, 2) + 14);
                mhp = Util.getDeviatedRandomValue(mhpSeed1, 3);
                minDmg = MathUtils.random(3, 8);
                maxDmg = MathUtils.random(9, 15);
                for (int i = 0; i < level - 1; i++) {
                    minDmg += MathUtils.random(1, 2) - MathUtils.random(2);
                    maxDmg += MathUtils.random(1, 2) + MathUtils.random(2);
                }
                break;
            case 2: // ice golem
                int mhpSeed2 = (int) (Math.pow(level, 2.3) + 25);
                mhp = Util.getDeviatedRandomValue(mhpSeed2, 150);
                minDmg = MathUtils.random(1, 4);
                maxDmg = MathUtils.random(5, 8);
                for (int i = 0; i < level - 1; i++) {
                    minDmg += MathUtils.random(1, 2) - 1;
                    maxDmg += MathUtils.random(1, 2) + 1;
                }
                break;
        }

        this.setMaxHp(mhp);
        this.setMinDamage(minDmg);
        this.setMaxDamage(maxDmg);
        this.setAccuracy(MathUtils.random(Util.ENEMY_MIN_ACCURACY, Util.ENEMY_MAX_ACCURACY));
    }

    /**
     * Returns a description of a boss's passive based on bossId
     *
     * @return
     */
    public String getPassiveDescription() {
        switch (bossId) {
            // king slime
            case 0: return "Slime Revival (Respawns after death with half health points up to 4 times).";
            // red reaper
            case 1: return "Phantom Presence (Causes the player's accuracy to be decreased by 40% for all attacks).";
            // ice golem
            case 2: return "Lifesteal (Heals for 20% of damage from each attack).";
        }
        return "";
    }

}
