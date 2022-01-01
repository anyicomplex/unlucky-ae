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

import com.anyicomplex.unlucky.resource.Util;
import com.badlogic.gdx.utils.Array;

/**
 * A set of special moves
 * The player can have a set amount of special moves
 * A special moveset cannot have more than 2 of same moves
 *
 * @author Ming Li
 */
public class SpecialMoveset {

    // maximum numbers of smoves in a set
    public static final int MAX_MOVES = 5;
    public Array<SpecialMove> smoveset;

    public SpecialMoveset() {
        smoveset = new Array<SpecialMove>();
    }

    /**
     * Adds a special move based on id to the set
     * if constraints are met
     *
     * @param id
     */
    public void addSMove(int id) {
        if (canAdd(id)) {
            smoveset.add(getMove(id));
        }
    }

    public SpecialMove getMoveAt(int i) {
        return smoveset.get(i);
    }

    /**
     * Clears the smoveset
     */
    public void clear() {
        smoveset.clear();
    }

    public boolean isFull() {
        return smoveset.size == MAX_MOVES;
    }

    /**
     * Removes an smove from a given index
     * @param index
     */
    public void remove(int index) {
        smoveset.removeIndex(index);
    }

    public String toString() {
        String ret = "[ ";
        for (int i = 0; i < smoveset.size; i++) {
            ret += smoveset.get(i).name + (i == smoveset.size - 1 ? "" : ", ");
        }
        ret += " ]";
        return ret;
    }

    /**
     * Returns whether it's possible to add a certain smove to the set
     * Constraint 1: must be enough space
     * Constraint 2: move must not already appear twice
     *
     * @param id
     * @return
     */
    public boolean canAdd(int id) {
        if (smoveset.size == MAX_MOVES) return false;
        int count = 0;
        for (int i = 0; i < smoveset.size; i++) {
            if (smoveset.get(i).id == id) count++;
        }
        return count < 2;
    }

    /**
     * Returns the smove associated with a id
     *
     * @param id
     * @return
     */
    private SpecialMove getMove(int id) {
        switch (id) {
            case Util.DISTRACT: return Util.S_DISTRACT;
            case Util.FOCUS: return Util.S_FOCUS;
            case Util.INTIMIDATE: return Util.S_INTIMIDATE;
            case Util.REFLECT: return Util.S_REFLECT;
            case Util.STUN: return Util.S_STUN;
            case Util.INVERT: return Util.S_INVERT;
            case Util.SACRIFICE: return Util.S_SACRIFICE;
            case Util.SHIELD: return Util.S_SHIELD;
        }
        return null;
    }

}
