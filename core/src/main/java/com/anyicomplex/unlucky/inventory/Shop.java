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
import com.badlogic.gdx.utils.Array;

/**
 * A shop selling potions and unique equips
 * The shop contains everything from beginner equips to endgame equips
 *
 * @author Ming Li
 */
public class Shop {

    // items sold separated by type
    // 0 - misc, 1 - equips, 2 - accessories (rings and necklaces)
    public Array<Array<ShopItem>> items;

    public Shop(ResourceManager rm) {
        items = new Array<Array<ShopItem>>();
        for (int i = 0; i < 3; i++) items.add(new Array<ShopItem>());

        // fill shop items with items
        for (int rarity = 0; rarity < rm.shopItems.size; rarity++) {
            for (int i = 0; i < rm.shopItems.get(rarity).size; i++) {
                ShopItem item = rm.shopItems.get(rarity).get(i);
                ShopItem shopItem;
                // potions
                if (item.type == 0) {
                    shopItem = new ShopItem(rm, item.name, item.desc, item.rarity,
                        item.imgIndex, item.minLevel, item.hp, item.exp, item.sell, item.price);
                    items.get(0).add(shopItem);
                }
                // equip
                else if (item.type >= 2 && item.type <= 6) {
                    shopItem = new ShopItem(rm, item.name, item.desc, item.type, item.rarity, item.imgIndex,
                        item.minLevel, item.mhp, item.dmg, item.acc, item.sell, item.price);
                    items.get(1).add(shopItem);
                }
                // accs
                else if (item.type >= 7 && item.type <= 9) {
                    shopItem = new ShopItem(rm, item.name, item.desc, item.type, item.rarity, item.imgIndex,
                        item.minLevel, item.mhp, item.dmg, item.acc, item.sell, item.price);
                    items.get(2).add(shopItem);
                }
                // enchant scrolls
                else if (item.type == 10) {
                    shopItem = new ShopItem(rm, item.name, item.desc, item.rarity, item.imgIndex, item.minLevel,
                        item.eChance, item.sell, item.price);
                    items.get(0).add(shopItem);
                }
            }
        }
    }

}
