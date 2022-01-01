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

package com.anyicomplex.unlucky.inventory;

import com.anyicomplex.unlucky.resource.ResourceManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * A special type of item that is only sold in the shop
 * and has a certain cost in gold
 *
 * @author Ming Li
 */
public class ShopItem extends Item {

    // price of the item in the shop
    public int price;

    /**
     * For potions
     * Only can be consumed for hp or sold for gold
     *
     * @param name
     * @param desc
     * @param rarity
     * @param imgIndex for textureregion in spritesheet
     * @param hp
     * @param sell
     */
    public ShopItem(ResourceManager rm, String name, String desc, int rarity,
                    int imgIndex, int level, int hp, int exp, int sell, int price) {
        super(rm, name, desc, rarity, imgIndex, level, level, hp, exp, sell);
        this.price = price;
        actor = new Image(rm.shopitems[0][imgIndex]);
    }

    /**
     * For all types of equips
     * Gives increased stats and can be sold for gold
     *
     * @param name
     * @param desc
     * @param type
     * @param rarity
     * @param imgIndex
     * @param mhp
     * @param dmg
     * @param acc
     * @param sell
     */
    public ShopItem(ResourceManager rm, String name, String desc, int type, int rarity,
                    int imgIndex, int level, int mhp, int dmg, int acc, int sell, int price) {
        super(rm, name, desc, type, rarity, imgIndex, level, level, mhp, dmg, acc, sell);
        this.price = price;
        actor = new Image(rm.shopitems[type - 1][imgIndex]);
        int enchantSeed = MathUtils.random(75, 225);
        for (int i = 0; i < level; i++) enchantCost += enchantSeed;
    }

    /**
     * For enchant scrolls
     *
     * @param rm
     * @param name
     * @param desc
     * @param rarity
     * @param imgIndex
     * @param eChance
     * @param sell
     * @param price
     */
    public ShopItem(ResourceManager rm, String name, String desc, int rarity, int imgIndex, int level,
                    int eChance, int sell, int price) {
        super(rm, name, desc, rarity, imgIndex, level, level, eChance, sell);
        this.price = price;
        actor = new Image(rm.shopitems[9][imgIndex]);
    }

}
