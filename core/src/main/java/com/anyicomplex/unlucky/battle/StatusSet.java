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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

/**
 * Container for status effect icons and their rendering
 * Renders status icons in a horizontal line
 * If a status is removed then the line shrinks
 * There are no duplicate effects in a set
 *
 * @author Ming Li
 */
public class StatusSet {

    // status icons
    public Array<StatusEffect> effects;

    // whether or not it's the player's status icons
    private boolean player;
    private ResourceManager rm;

    public StatusSet(boolean player, ResourceManager rm) {
        this.player = player;
        this.rm = rm;
        effects = new Array<StatusEffect>();
    }

    /**
     * Adds an effect to the list if the effect is not already in it
     *
     * @param effect
     */
    public void addEffect(int effect) {
        if (findEffect(effect) == -1) {
            effects.add(new StatusEffect(effect, rm));
        }
    }

    public void clear() {
        effects.clear();
    }

    /**
     * Removes all multi turn status effects
     */
    public void clearAllButSingleTurnEffects() {
        for (int i = 0; i < effects.size; i++) {
            if (effects.get(i).type == StatusEffect.DMG_RED) {
                effects.removeIndex(i);
            }
        }
    }

    /**
     * Removes all single turn status effects
     */
    public void clearAllButMultiTurnEffects() {
        for (int i = 0; i < effects.size; i++) {
            if (effects.get(i).type != StatusEffect.DMG_RED) {
                effects.removeIndex(i);
            }
        }
    }

    /**
     * Returns if the set contains a certain effect
     *
     * @param effect
     * @return
     */
    public boolean contains(int effect) {
        return findEffect(effect) != -1;
    }

    /**
     * Returns the index of an effect
     * Returns -1 if not in set
     *
     * @param effect
     * @return
     */
    public int findEffect(int effect) {
        for (int i = 0; i < effects.size; i++) {
            if (effects.get(i).type == effect)
                return effects.get(i).type;
        }
        return -1;
    }

    /**
     * Player's status bar renders from left to right
     * Enemy's renders from right to left
     *
     * @param batch
     */
    public void render(SpriteBatch batch) {
        for (int i = 0; i < effects.size; i++) {
            StatusEffect s = effects.get(i);
            if (player) {
                if (s != null) batch.draw(s.icon, 1 + (i * 11), 90);
            } else {
                if (s != null) batch.draw(s.icon, 189 - (i * 11), 90);
            }
        }
    }

}
