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

import com.anyicomplex.unlucky.Unlucky;
import com.anyicomplex.unlucky.battle.SpecialMoveset;
import com.anyicomplex.unlucky.entity.Player;
import com.anyicomplex.unlucky.inventory.Equipment;
import com.anyicomplex.unlucky.inventory.Inventory;
import com.anyicomplex.unlucky.inventory.Item;
import com.anyicomplex.unlucky.inventory.ShopItem;
import com.anyicomplex.unlucky.resource.ResourceManager;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

/**
 * Handles the reading and writing of save data to json files.
 *
 * @author Ming Li
 */
public class Save {

    // for saving and loading
    private Player player;
    public PlayerAccessor psave;
    private Json json;
    private FileHandle file;
    private String fileName;

    public Save(Player player, String fileName) {
        this.player = player;
        psave = new PlayerAccessor();
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);
        // file = Gdx.files.local(path);
        this.fileName = fileName;
        if (Gdx.app.getType() == Application.ApplicationType.WebGL) return;
        file = Gdx.files.absolute(Unlucky.STORAGE_ABSOLUTE_PATH + fileName);
    }

    /**
     * Loads the player data into the PlayerAccessor then
     * writes the player save data to the json file
     */
    public void save() {
        // load player data
        psave.load(player);
        // write data to save json
        if (Gdx.app.getType() == Application.ApplicationType.WebGL || Gdx.app.getType() == Application.ApplicationType.Android) {
            Preferences wrapper = Gdx.app.getPreferences(fileName);
            wrapper.putString(fileName, Base64Coder.encodeString(json.prettyPrint(psave)));
            wrapper.flush();
            return;
        }
        file.writeString(Base64Coder.encodeString(json.prettyPrint(psave)), false);
    }

    /**
     * Reads the player data from the save json file and then
     * loads the data into the game through the player
     */
    public void load(ResourceManager rm) {
        if (Gdx.app.getType() == Application.ApplicationType.WebGL || Gdx.app.getType() == Application.ApplicationType.Android) {
            Preferences wrapper = Gdx.app.getPreferences(fileName);
            String jsonString = wrapper.getString(fileName, "NODATA");
            if (jsonString.equals("NODATA")) save();
            try {
                psave = json.fromJson(PlayerAccessor.class, Base64Coder.decodeString(jsonString));
            }
            catch (Exception ignored) {
                return;
            }
        }
        else {
            if (!file.exists()) save();
            try {
                psave = json.fromJson(PlayerAccessor.class, Base64Coder.decodeString(file.readString()));
            }
            catch (Exception ignored) {
                return;
            }
        }

        // load atomic fields
        player.setHp(psave.hp);
        player.setMaxHp(psave.maxHp);
        player.setLevel(psave.level);
        player.setExp(psave.exp);
        player.setMaxExp(psave.maxExp);
        player.setGold(psave.gold);
        player.setMinDamage(psave.minDamage);
        player.setMaxDamage(psave.maxDamage);
        player.setAccuracy(psave.accuracy);
        player.smoveCd = psave.smoveCd;
        player.maxWorld = psave.maxWorld;
        player.maxLevel = psave.maxLevel;

        // load inventory and equips
        loadInventory(rm);
        loadEquips(rm);

        // load smoveset
        for (int i = 0; i < SpecialMoveset.MAX_MOVES; i++) {
            if (psave.smoveset[i] != -1) {
                player.smoveset.addSMove(psave.smoveset[i]);
            }
        }

        // load statistics
        player.stats = psave.stats;

        // load and apply settings
        player.settings = psave.settings;
        if (player.settings.muteMusic) rm.setMusicVolume(0f);
        else rm.setMusicVolume(player.settings.musicVolume);
    }

    /**
     * Helper method for loading and converting ItemAccessors to Items in the inventory
     */
    private void loadInventory(ResourceManager rm) {
        for (int i = 0; i < Inventory.NUM_SLOTS; i++) {
            ItemAccessor ia = psave.inventory[i];
            if (ia != null) {
                // shop items
                if (ia instanceof ShopItemAccessor) {
                    ShopItem sitem = null;
                    if (ia.type == 0)
                        sitem = new ShopItem(rm, ia.name, ia.desc, ia.rarity, ia.imgIndex, 0,
                            ia.hp, ia.exp, ia.sell, ((ShopItemAccessor) ia).price);
                    else if (ia.type >= 2 && ia.type <= 9) {
                        sitem = new ShopItem(rm, ia.name, ia.desc, ia.type, ia.rarity, ia.imgIndex, 0,
                            ia.mhp, ia.dmg, ia.acc, ia.sell, ((ShopItemAccessor) ia).price);
                        sitem.enchantCost = ia.enchantCost;
                    }
                    else if (ia.type == 10)
                        sitem = new ShopItem(rm, ia.name, ia.desc, ia.rarity, ia.imgIndex, 0,
                            ia.eChance, ia.sell, ((ShopItemAccessor) ia).price);
                    player.inventory.addItemAtIndex(sitem, ia.index);
                }
                else {
                    Item item = null;
                    if (ia.type == 0)
                        item = new Item(rm, ia.name, ia.desc, ia.rarity, ia.imgIndex, 0, 0,
                            ia.hp, ia.exp, ia.sell);
                    else if (ia.type == 1)
                        item = new Item(rm, ia.name, ia.desc, ia.rarity, ia.imgIndex, 0, 0, ia.sell);
                    else if (ia.type >= 2 && ia.type <= 9) {
                        item = new Item(rm, ia.name, ia.desc, ia.type, ia.rarity, ia.imgIndex, 0, 0,
                            ia.mhp, ia.dmg, ia.acc, ia.sell);
                        item.enchantCost = ia.enchantCost;
                    }
                    else if (ia.type == 10)
                        item = new Item(rm, ia.name, ia.desc, ia.rarity, ia.imgIndex, 0, 0,
                            ia.eChance, ia.sell);
                    player.inventory.addItemAtIndex(item, ia.index);
                }
            }
        }
    }

    /**
     * Helper method for loading and converting ItemAccessors to Items in the inventory
     */
    private void loadEquips(ResourceManager rm) {
        for (int i = 0; i < Equipment.NUM_SLOTS; i++) {
            ItemAccessor ia = psave.equips[i];
            if (ia != null) {
                // shop items
                if (ia instanceof ShopItemAccessor) {
                    ShopItem sitem = null;
                    if (ia.type == 0)
                        sitem = new ShopItem(rm, ia.name, ia.desc, ia.rarity, ia.imgIndex, 0,
                            ia.hp, ia.exp, ia.sell, ((ShopItemAccessor) ia).price);
                    else if (ia.type >= 2 && ia.type <= 9)
                        sitem = new ShopItem(rm, ia.name, ia.desc, ia.type, ia.rarity, ia.imgIndex, 0,
                            ia.mhp, ia.dmg, ia.acc, ia.sell, ((ShopItemAccessor) ia).price);
                    else if (ia.type == 10)
                        sitem = new ShopItem(rm, ia.name, ia.desc, ia.rarity, ia.imgIndex, 0,
                            ia.eChance, ia.sell, ((ShopItemAccessor) ia).price);
                    player.equips.addEquip(sitem);
                }
                else {
                    Item item = null;
                    if (ia.type == 0)
                        item = new Item(rm, ia.name, ia.desc, ia.rarity, ia.imgIndex, 0, 0,
                            ia.hp, ia.exp, ia.sell);
                    else if (ia.type == 1)
                        item = new Item(rm, ia.name, ia.desc, ia.rarity, ia.imgIndex, 0, 0, ia.sell);
                    else if (ia.type >= 2 && ia.type <= 9)
                        item = new Item(rm, ia.name, ia.desc, ia.type, ia.rarity, ia.imgIndex, 0, 0,
                            ia.mhp, ia.dmg, ia.acc, ia.sell);
                    else if (ia.type == 10)
                        item = new Item(rm, ia.name, ia.desc, ia.rarity, ia.imgIndex, 0, 0,
                            ia.eChance, ia.sell);
                    player.equips.addEquip(item);
                }
            }
        }
    }

}
