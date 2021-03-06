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

package com.anyicomplex.unlucky.screen;

import com.anyicomplex.unlucky.Unlucky;
import com.anyicomplex.unlucky.map.Level;
import com.anyicomplex.unlucky.resource.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

/**
 * Allows the player to select a level from a world
 * Displays the levels using a scroll pane
 *
 * WorldSelectScreen will always come before this screen and pass world data
 *
 * @author Ming Li
 */
public class LevelSelectScreen extends SelectScreen {

    // the world these levels are in
    private int numLevels;

    // current level selection
    private int currentLevelIndex;
    private int numLevelsToShow;

    // player stats to be displayed
    private String playerStats;

    public LevelSelectScreen(final Unlucky game, final ResourceManager rm) {
        super(game, rm);

        handleExitButton();
        handleEnterButton();
        createScrollPane();
    }

    @Override
    public void show() {
        super.show();
        game.player.inMap = false;

        bannerLabel.setText(rm.worlds.get(worldIndex).name);
        bannerLabel.setStyle(nameStyles[worldIndex]);

        playerStats = "Player\n-----------------------------------\n" +
            "LEVEL: " + game.player.getLevel() +
            "\nHP: " + game.player.getHp() + "/" + game.player.getMaxHp() +
            "\nDAMAGE: " + game.player.getMinDamage() + "-" + game.player.getMaxDamage() +
            "\nSPECIAL MOVESET: \n" + game.player.smoveset.toString();

        // the level the player is currently on and not completed
        if (this.worldIndex == game.player.maxWorld) {
            this.currentLevelIndex = game.player.maxLevel;
            this.numLevelsToShow = game.player.maxLevel;
        }
        // levels the player have completed so show all the levels
        else if (this.worldIndex < game.player.maxWorld) {
            this.currentLevelIndex = 0;
            this.numLevelsToShow = rm.worlds.get(worldIndex).numLevels - 1;
        }
        // in a world the player has not gotten to yet
        else {
            this.currentLevelIndex = 0;
            this.numLevelsToShow = -1;
        }

        // the side description will show player stats and level name
        String levelName = rm.worlds.get(worldIndex).levels[currentLevelIndex].name;
        fullDescLabel.setText(levelName + "\n\n" + playerStats);

        if (this.worldIndex > game.player.maxWorld) fullDescLabel.setText("???????????????" + "\n\n" + playerStats);

        scrollTable.remove();
        createScrollPane();
        stage.setScrollFocus(scrollPane);

        // automatically scroll to the position of the currently selected world button
        scrollPane.setScrollY(currentLevelIndex * 24);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (!clickable) return super.keyDown(keycode);
                switch (keycode) {
                    case Input.Keys.BACK:
                    case Input.Keys.ESCAPE:
                    if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                    game.menuScreen.transitionIn = 0;
                    setFadeScreen(game.worldSelectScreen);
                    Gdx.input.setInputProcessor(stage);
                    return true;
                    case Input.Keys.ENTER:
                    case Input.Keys.NUMPAD_ENTER:
                        performEnter();
                        return true;
                }
                return super.keyDown(keycode);
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void performEnter() {
        if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
        if (worldIndex <= game.player.maxWorld) {
            // if the player's inventory is full give a warning
            if (game.player.inventory.isFull()) {
                new Dialog("Warning", rm.dialogSkin) {
                    {
                        Label l = new Label("Your inventory is full.\nAre you sure you want to proceed?", rm.dialogSkin);
                        l.setFontScale(0.5f);
                        l.setAlignment(Align.center);
                        text(l);
                        getButtonTable().defaults().width(40);
                        getButtonTable().defaults().height(15);
                        button("Yes", "yes");
                        button("No", "no");
                        key(Input.Keys.Y, "yes");
                        key(Input.Keys.N, "no");
                    }

                    @Override
                    protected void result(Object object) {
                        if (object.equals("yes")) enterGame();
                    }

                }.show(stage).getTitleLabel().setAlignment(Align.center);
            } else {
                enterGame();
            }
        }
    }

    /**
     * To know know what world this screen is in
     *
     * @param worldIndex
     */
    public void setWorld(int worldIndex) {
        this.worldIndex = worldIndex;
        this.numLevels = rm.worlds.get(worldIndex).numLevels;
    }

    protected void handleExitButton() {
        super.handleExitButton(game.worldSelectScreen);
    }

    protected void handleEnterButton() {
        enterButtonGroup.setPosition(114, 4);
        stage.addActor(enterButtonGroup);
        enterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                performEnter();
            }
        });
    }

    /**
     * Enters the map with the corresponding world, level key
     */
    private void enterGame() {
        game.gameScreen.init(worldIndex, currentLevelIndex);
        game.gameScreen.resetGame = true;
        rm.menuTheme.pause();
        game.player.inMap = true;
        setFadeScreen(game.gameScreen);
    }

    protected void createScrollPane() {
        scrollButtons = new Array<TextButton>();

        nameStyle = new Label.LabelStyle(rm.pixel10, Color.WHITE);
        descStyle = new Label.LabelStyle(rm.pixel10, Color.WHITE);
        buttonSelected = new TextButton.TextButtonStyle();
        buttonSelected.up = new TextureRegionDrawable(rm.skin.getRegion("default-round-down"));

        scrollTable = new Table();
        scrollTable.setFillParent(true);
        stage.addActor(scrollTable);

        selectionContainer = new Table();
        for (int i = 0; i < numLevels; i++) {
            final int index = i;

            // button and label group
            Group g = new Group();
            g.setSize(90, 20);
            g.setTransform(false);

            Level l = rm.worlds.get(worldIndex).levels[index];

            Label name;
            // on last level (boss level) the name is red
            if (i == numLevels - 1)
                name = new Label(l.name, new Label.LabelStyle(rm.pixel10, new Color(225 / 255.f, 0, 0, 1)));
            else
                name = new Label(l.name, nameStyle);
            name.setPosition(5, 10);
            name.setFontScale(0.66f);
            name.setTouchable(Touchable.disabled);
            Label desc = new Label("Average level: " + l.avgLevel, descStyle);
            desc.setPosition(5, 4);
            desc.setFontScale(0.5f);
            desc.setTouchable(Touchable.disabled);

            final TextButton b = new TextButton("", rm.skin);
            b.getStyle().checked = b.getStyle().down;
            b.getStyle().over = null;
            if (i == currentLevelIndex) b.setChecked(true);

            // only enable the levels the player has defeated
            if (index > numLevelsToShow) {
                b.setTouchable(Touchable.disabled);
                name.setText("???????????????");
                desc.setText("Average level:  ???");
            }
            else {
                b.setTouchable(Touchable.enabled);
                scrollButtons.add(b);
                name.setText(l.name);
                desc.setText("Average level: " + l.avgLevel);
            }

            // select level
            b.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!game.player.settings.muteSfx) rm.invselectclick.play(game.player.settings.sfxVolume);
                    currentLevelIndex = index;
                    selectAt(currentLevelIndex);
                    String levelName = rm.worlds.get(worldIndex).levels[currentLevelIndex].name;
                    fullDescLabel.setText(levelName + "\n\n" + playerStats);
                }
            });
            b.setFillParent(true);

            g.addActor(b);
            g.addActor(name);
            g.addActor(desc);

            selectionContainer.add(g).padBottom(4).size(90, 20).row();
        }
        selectionContainer.pack();
        selectionContainer.setTransform(false);
        selectionContainer.setOrigin(selectionContainer.getWidth() / 2,
            selectionContainer.getHeight() / 2);

        scrollPane = new ScrollPane(selectionContainer, rm.skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.layout();
        scrollTable.add(scrollPane).size(112, 93).fill();
        scrollTable.setPosition(-38, -10);
    }

    public void render(float dt) {
        super.render(dt, worldIndex);
    }

}
