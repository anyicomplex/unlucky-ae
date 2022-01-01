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

package com.anyicomplex.unlucky.ui.inventory;

import com.anyicomplex.unlucky.inventory.Item;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;

/**
 * Window UI that acts as a tooltip for an item displaying its stats
 * The window should adjust its size according to how much information an item has
 *
 * @author Ming Li
 */
public class ItemTooltip extends Window {

    private Label desc;

    // label styles for different color names based on item rarity
    private Label.LabelStyle common;
    private Label.LabelStyle rare;
    private Label.LabelStyle epic;
    private Label.LabelStyle legendary;

    public ItemTooltip(Skin skin) {
        super("", skin);
        desc = new Label("", skin);
        desc.setFontScale(0.5f);
        this.getTitleLabel().setFontScale(0.5f);

        common = new Label.LabelStyle(skin.getFont("default-font"), new Color(1, 1, 1, 1));
        rare = new Label.LabelStyle(skin.getFont("default-font"), new Color(0, 200 / 255.f, 0, 1));
        epic = new Label.LabelStyle(skin.getFont("default-font"), new Color(0, 180 / 255.f, 1, 1));
        legendary = new Label.LabelStyle(skin.getFont("default-font"), new Color(164 / 255.f, 80 / 255.f, 1, 1));

        left();
        // fix padding because of scaling
        this.padTop(12);
        this.padLeft(2);
        this.padBottom(4);
        add(desc);
        pack();
        this.setTouchable(Touchable.disabled);
        this.setVisible(false);
        this.setMovable(false);
        this.setOrigin(Align.bottomLeft);
    }

    /**
     * Sets the title color to the item's rarity and sets the descriptions
     * common - white
     * rare - green
     * epic - blue
     * legendary - purple
     *
     * @param item
     * @param x
     * @param y
     */
    public void show(Item item, float x, float y) {
        this.setPosition(x, y);
        this.setVisible(true);
        updateText(item);
    }

    public void updateText(Item item) {
        switch (item.rarity) {
            case 0:
                this.getTitleLabel().setStyle(common);
                break;
            case 1:
                this.getTitleLabel().setStyle(rare);
                break;
            case 2:
                this.getTitleLabel().setStyle(epic);
                break;
            case 3:
                this.getTitleLabel().setStyle(legendary);
                break;
        }
        this.getTitleLabel().setText(item.labelName);
        desc.setText(item.getFullDesc());

        pack();
    }

    public void hide() {
        this.setVisible(false);
    }

}
