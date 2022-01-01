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

package com.anyicomplex.unlucky.battle;

import com.anyicomplex.unlucky.resource.ResourceManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * A Moveset is a set of 4 random moves that an Entity gets each battle
 *
 * @author Ming Li
 */
public class Moveset {

    private ResourceManager rm;

    /**
     * Index:
     * 0 - accurate
     * 1 - wide
     * 2 - crit
     * 3 - heal
     */
    public Move[] moveset;
    public String[] names;
    public String[] descriptions;

    public Moveset(ResourceManager rm) {
        this.rm = rm;

        moveset = new Move[4];
        names = new String[4];
        descriptions = new String[4];
    }

    /**
     * Resets a Moveset with a set of 4 new random Moves
     */
    public void reset(int min, int max, int hp) {
        moveset = getRandomMoves();
        int dmg;

        for (int i = 0; i < 4; i++) {
            // reset damage seed for a new value between player's dmg range each iteration
            dmg = MathUtils.random(min, max);
            if (moveset[i].type == 3) moveset[i].setHeal(hp);
            else moveset[i].setDamage(dmg);

            names[i] = moveset[i].name;
            // Concatenates move info into a full description
            if (moveset[i].type < 2) {
                descriptions[i] = "dmg: " + Math.round(moveset[i].minDamage)
                        + "-" + Math.round(moveset[i].maxDamage);
            } else if (moveset[i].type == 2) {
                descriptions[i] = "dmg: " + Math.round(moveset[i].minDamage) + " + "
                        + moveset[i].crit + "% to crit";
            } else {
                descriptions[i] = "HP: " + Math.round(moveset[i].minHeal)
                        + "-" + Math.round(moveset[i].maxHeal) + ", -" + moveset[i].dmgReduction
                        + "% DMG";
            }
        }
    }

    /**
     * Resets moveset for bosses
     */
    public void reset(int min, int max, int hp, int bossId) {
        moveset = getBossMoves(bossId);
        int dmg;
        for (int i = 0; i < 4; i++) {
            dmg = MathUtils.random(min, max);
            if (moveset[i].type == 3) moveset[i].setHeal(hp);
            else moveset[i].setDamage(dmg);
        }
    }

    /**
     * Returns the first damage move from a moveset
     * If there are no damage moves then it returns a random heal move
     *
     * @return
     */
    public Move getDamagePriority() {
        for (int i = 0; i < moveset.length; i++) {
            if (moveset[i].type != 3) {
                return moveset[i];
            }
        }
        return moveset[MathUtils.random(3)];
    }

    /**
     * Returns the first heal move from a moveset
     * If there are no heal moves, then it returns a random move
     *
     * @return
     */
    public Move getHealPriority() {
        for (int i = 0; i < moveset.length; i++) {
            if (moveset[i].type == 3) {
                return moveset[i];
            }
        }
        return moveset[MathUtils.random(3)];
    }

    /**
     * Returns a Move array with 4 unique moves chosen from all possible Moves
     *
     * @return
     */
    private Move[] getRandomMoves() {
        Array<Move> all = new Array<Move>();
        all.addAll(rm.accurateMoves);
        all.addAll(rm.wideMoves);
        all.addAll(rm.critMoves);
        all.addAll(rm.healMoves);

        Move[] ret = new Move[4];

        int index;
        for (int i = 0; i < ret.length; i++) {
            index = MathUtils.random(all.size - 1);
            Move randMove = all.get(index);
            Move temp = null;

            if (randMove.type < 2)
                temp = new Move(randMove.type, randMove.name, randMove.minDamage, randMove.maxDamage);
            else if (randMove.type == 2)
                temp = new Move(randMove.name, randMove.minDamage, randMove.crit);
            else if (randMove.type == 3)
                temp = new Move(randMove.name, randMove.minHeal, randMove.maxHeal, randMove.dmgReduction);

            ret[i] = temp;
            all.removeIndex(index);
        }

        return ret;
    }

    /**
     * Returns a Move array with 4 unique moves from a boss's movepool
     *
     * @param bossId
     * @return
     */
    private Move[] getBossMoves(int bossId) {
        Array<Move> pool = rm.bossMoves.get(bossId);
        Move[] ret = new Move[4];
        int index;
        for (int i = 0; i < ret.length; i++) {
            index = MathUtils.random(pool.size - 1);
            Move randMove = pool.get(index);
            Move temp = null;

            if (randMove.type < 2)
                temp = new Move(randMove.type, randMove.name, randMove.minDamage, randMove.maxDamage);
            else if (randMove.type == 2)
                temp = new Move(randMove.name, randMove.minDamage, randMove.crit);
            else if (randMove.type == 3)
                temp = new Move(randMove.name, randMove.minHeal, randMove.maxHeal, randMove.dmgReduction);

            ret[i] = temp;
            //pool.removeIndex(index);
        }

        return ret;
    }

}
