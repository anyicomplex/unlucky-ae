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

import com.anyicomplex.unlucky.inventory.Item;

/**
 * Provides a serializable object that represents the data of items
 * that are considered important for save files.
 *
 * @author Ming Li
 */
public class ItemAccessor {

    // descriptors
    public String name;
    public String labelName;
    public String desc;

    // position of the item in the inventory
    public int index;

    // the composite key for the image actor
    public int type;
    public int imgIndex;

    // stats
    public int rarity;
    public int hp;
    public int mhp;
    public int dmg;
    public int acc;
    public int sell;
    public int exp;
    public int enchants;
    public int enchantCost;
    public int bonusEnchantChance;
    public int eChance;

    /**
     * Updates the fields of this accessor with data from the item
     * @param item
     */
    public void load(Item item) {
        this.name = item.name;
        this.desc = item.desc;
        this.labelName = item.labelName;

        this.index = item.index;

        this.type = item.type;
        this.imgIndex = item.imgIndex;

        this.rarity = item.rarity;
        this.hp = item.hp;
        this.mhp = item.mhp;
        this.dmg = item.dmg;
        this.acc = item.acc;
        this.sell = item.sell;
        this.exp = item.exp;
        this.enchants = item.enchants;
        this.enchantCost = item.enchantCost;
        this.bonusEnchantChance = item.bonusEnchantChance;
        this.eChance = item.eChance;
    }

}
