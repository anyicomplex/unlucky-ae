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

/**
 * An Inventory is a collection of Items arranged in a grid
 * This acts mainly as a collection class and functions to implement inventory management
 *
 * @author Ming Li
 */
public class Inventory {

    // inventory dimensions
    public static final int NUM_SLOTS = 24;

    public Item[] items;

    public Inventory() {
        items = new Item[NUM_SLOTS];
    }

    /**
     * Returns the index of the first empty slot in the inventory
     * Returns -1 if there are no free slots
     *
     * @return
     */
    public int getFirstFreeSlotIndex() {
        for (int i = 0; i < NUM_SLOTS; i++) {
            if (items[i] == null) return i;
        }
        return -1;
    }

    /**
     * Returns the Item in the inventory at a given index
     * but does not remove the item from the inventory
     *
     * @param index
     * @return
     */
    public Item getItem(int index) {
        return items[index];
    }

    /**
     * Returns whether or not a slot at an index is empty
     *
     * @param index
     * @return
     */
    public boolean isFreeSlot(int index) {
        return items[index] == null;
    }

    /**
     * Adds an Item to the inventory that is placed in the first available slot
     * Returns false if item cannot be added
     *
     * @param item
     * @return
     */
    public boolean addItem(Item item) {
        int i = getFirstFreeSlotIndex();
        if (i != -1) {
            items[i] = item;
            item.index = i;
            return true;
        }
        return false;
    }

    /**
     * Adds an Item at a specific index
     * Returns false if item cannot be added
     *
     * @param item
     * @param index
     * @return
     */
    public boolean addItemAtIndex(Item item, int index) {
        if (isFreeSlot(index)) {
            items[index] = item;
            item.index = index;
            return true;
        }
        return false;
    }

    /**
     * Removes an Item from the inventory at a specific index
     *
     * @param index
     */
    public void removeItem(int index) {
        if (items[index] != null) items[index] = null;
    }

    /**
     * Removes an Item from the inventory and returns the Item
     *
     * @param index
     * @return
     */
    public Item takeItem(int index) {
        Item ret = null;
        if (items[index] != null) {
            ret = items[index];
            items[index] = null;
            return ret;
        }
        return null;
    }

    /**
     * Returns whether or not the inventory is full
     *
     * @return
     */
    public boolean isFull() {
        for (int i = 0; i < NUM_SLOTS; i++) {
            if (items[i] == null) return false;
        }
        return true;
    }

    /**
     * Clears every item in the inventory
     */
    public void clear() {
        for (int i = 0; i < NUM_SLOTS; i++) {
            removeItem(i);
        }
    }

}
