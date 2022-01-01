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

package com.anyicomplex.unlucky.save;

import com.anyicomplex.unlucky.battle.SpecialMoveset;
import com.anyicomplex.unlucky.entity.Player;
import com.anyicomplex.unlucky.inventory.Equipment;
import com.anyicomplex.unlucky.inventory.Inventory;
import com.anyicomplex.unlucky.inventory.Item;
import com.anyicomplex.unlucky.inventory.ShopItem;
import com.anyicomplex.unlucky.resource.Statistics;

import java.util.Arrays;

/**
 * Provides a serializable object that represents the data of the player
 * that is considered important for save files.
 *
 * @author Ming Li
 */
public class PlayerAccessor  {

    // status fields
    public int hp;
    public int maxHp;
    public int level;
    public int exp;
    public int maxExp;
    public int gold;
    public int minDamage;
    public int maxDamage;
    public int accuracy;
    public int smoveCd;

    // level save
    public int maxWorld;
    public int maxLevel;

    // inventory and equips consist of ItemAccessors to reduce unnecessary fields
    public ItemAccessor[] inventory;
    public ItemAccessor[] equips;

    // smoveset is simply an array of integers representing smove ids
    // -1 if no smove in slot
    public int[] smoveset;

    // statistics
    public Statistics stats;

    // settings
    public Settings settings;

    public PlayerAccessor() {
        inventory = new ItemAccessor[Inventory.NUM_SLOTS];
        equips = new ItemAccessor[Equipment.NUM_SLOTS];
        smoveset = new int[SpecialMoveset.MAX_MOVES];
        Arrays.fill(smoveset, -1);
    }

    /**
     * Updates the fields of this accessor with data from the player
     * @param player
     */
    public void load(Player player) {
        // load atomic fields
        this.hp = player.getHp();
        this.maxHp = player.getMaxHp();
        this.level = player.getLevel();
        this.exp = player.getExp();
        this.maxExp = player.getMaxExp();
        this.gold = player.getGold();
        this.minDamage = player.getMinDamage();
        this.maxDamage = player.getMaxDamage();
        this.accuracy = player.getAccuracy();
        this.smoveCd = player.smoveCd;
        this.maxWorld = player.maxWorld;
        this.maxLevel = player.maxLevel;

        // load inventory and equips
        for (int i = 0; i < Inventory.NUM_SLOTS; i++) {
            if (!player.inventory.isFreeSlot(i)) {
                Item item = player.inventory.getItem(i);
                if (item instanceof ShopItem) {
                    ShopItemAccessor sia = new ShopItemAccessor();
                    sia.load((ShopItem) item);
                    inventory[i] = sia;
                }
                else {
                    ItemAccessor ia = new ItemAccessor();
                    ia.load(item);
                    inventory[i] = ia;
                }
            }
            else {
                inventory[i] = null;
            }
        }
        for (int i = 0; i < Equipment.NUM_SLOTS; i++) {
            if (player.equips.getEquipAt(i) != null) {
                Item equip = player.equips.getEquipAt(i);
                if (equip instanceof ShopItem) {
                    ShopItemAccessor sia = new ShopItemAccessor();
                    sia.load((ShopItem) equip);
                    equips[i] = sia;
                }
                else {
                    ItemAccessor ia = new ItemAccessor();
                    ia.load(equip);
                    equips[i] = ia;
                }
            }
            else {
                equips[i] = null;
            }
        }

        Arrays.fill(smoveset, -1);
        // load smoveset
        for (int i = 0; i < player.smoveset.smoveset.size; i++) {
            smoveset[i] = player.smoveset.getMoveAt(i).id;
        }

        // statistics
        this.stats = player.stats;

        // settings
        this.settings = player.settings;
    }

}
