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

package com.anyicomplex.unlucky.ui;

import com.anyicomplex.unlucky.Unlucky;
import com.anyicomplex.unlucky.effects.Moving;
import com.anyicomplex.unlucky.entity.Player;
import com.anyicomplex.unlucky.event.EventState;
import com.anyicomplex.unlucky.inventory.Inventory;
import com.anyicomplex.unlucky.inventory.Item;
import com.anyicomplex.unlucky.map.TileMap;
import com.anyicomplex.unlucky.map.WeatherType;
import com.anyicomplex.unlucky.resource.ResourceManager;
import com.anyicomplex.unlucky.resource.Util;
import com.anyicomplex.unlucky.screen.GameScreen;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

/**
 * Handles button input and everything not in the game camera
 *
 * @author Ming Li
 */
public class Hud extends UI {

    // directional pad: index i 0 - down, 1 - up, 2 - right, 3 - left
    private ImageButton[] dirPad;
    // if dir pad is held down
    public boolean touchDown = false;
    public int dirIndex = -1;
    // for changing the player's facing direction with a short tap like in pokemon
    public float dirTime = 0;

    // option buttons: inventoryUI and settings
    private ImageButton[] optionButtons;

    // window that slides on the screen to show the world and level
    private Window levelDescriptor;
    private Label levelDesc;
    private Moving levelMoving;
    private boolean ld = false;
    private float showTime = 0;

    // death screen that is a prompt with the map background dimmed out
    public Group deathGroup;
    private Image dark;
    private Image frame;
    private Label youDied;
    private Label loss;

    public Image shade;
    public Dialog settingsDialog;

    public Hud(final GameScreen gameScreen, TileMap tileMap, final Player player, final ResourceManager rm) {
        super(gameScreen, tileMap, player, rm);

        createDirPad();
        createOptionButtons();
        createLevelDescriptor();
        createDeathPrompt();

        shade = new Image(rm.shade);
        shade.setVisible(false);
        shade.setTouchable(Touchable.disabled);
        stage.addActor(shade);

        settingsDialog = new Dialog("Paused", rm.dialogSkin) {
            {
                getButtonTable().defaults().width(50);
                getButtonTable().defaults().height(15);
                TextButton b = new TextButton("Back", rm.dialogSkin);
                b.getLabel().setFontScale(0.75f);
                button(b, "back");
                getButtonTable().padTop(-6).row();
                TextButton s = new TextButton("Settings", rm.dialogSkin);
                s.getLabel().setFontScale(0.75f);
                button(s, "settings");
                getButtonTable().row();
                TextButton q = new TextButton("Quit", rm.dialogSkin);
                q.getLabel().setFontScale(0.75f);
                button(q, "quit");
                key(Input.Keys.B, "back");
                key(Input.Keys.S, "settings");
                key(Input.Keys.Q, "quit");
            }
            @Override
            protected void result(Object object) {
                if (!game.player.settings.muteSfx) rm.buttonclick2.play(game.player.settings.sfxVolume);
                if (object.equals("back")) {
                    shade.setVisible(false);
                    toggle(true);

                    // play music and sfx
                    if (!player.settings.muteMusic) gameScreen.gameMap.mapTheme.play();
                    if (!player.settings.muteSfx) {
                        if (gameScreen.gameMap.weather == WeatherType.RAIN) {
                            gameScreen.gameMap.soundId = rm.lightrain.play(player.settings.sfxVolume);
                            rm.lightrain.setLooping(gameScreen.gameMap.soundId, true);
                        }
                        else if (gameScreen.gameMap.weather == WeatherType.HEAVY_RAIN || gameScreen.gameMap.weather == WeatherType.THUNDERSTORM) {
                            gameScreen.gameMap.soundId = rm.heavyrain.play(player.settings.sfxVolume);
                            rm.heavyrain.setLooping(gameScreen.gameMap.soundId, true);
                        }
                    }

                    gameScreen.setCurrentEvent(EventState.MOVING);
                }
                else if (object.equals("settings")) {
                    game.settingsScreen.inGame = true;
                    game.settingsScreen.worldIndex = gameScreen.gameMap.worldIndex;
                    if (gameScreen.isClickable()) {
                        gameScreen.setClickable(false);
                        gameScreen.setBatchFade(false);
                        // fade out animation
                        stage.addAction(Actions.sequence(Actions.fadeOut(0.3f),
                            Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    gameScreen.setClickable(true);
                                    game.setScreen(game.settingsScreen);
                                }
                            })));
                    }
                }
                else if (object.equals("quit")) {
                    quit();
                }
            }
        };
        settingsDialog.getTitleLabel().setAlignment(Align.center);
        settingsDialog.getBackground().setMinWidth(70);
    }

    public void update(float dt) {
        if (touchDown) {
            dirTime += dt;
            // quick tap to change direction
            if (dirTime > 0 && dirTime <= 0.15f) player.getAm().setAnimation(dirIndex);
            // move the player
            else movePlayer(dirIndex);
        }
        else {
            player.getAm().stopAnimation();
        }

        if (ld) {
            levelMoving.update(dt);
            levelDescriptor.setPosition(levelMoving.position.x, levelMoving.position.y);
            if (!levelMoving.shouldStart) showTime += dt;
            // after 3 seconds of showing moves back to the starting position
            if (showTime >= 3) {
                showTime = 0;
                float y = 116 - levelDescriptor.getPrefHeight();
                levelMoving.origin.set(4, y);
                levelMoving.target.set(-levelDescriptor.getPrefWidth(), y);
                levelMoving.start();
            }
            if (levelMoving.target.x == -levelDescriptor.getPrefWidth() &&
                levelMoving.position.x == levelMoving.target.x) {
                ld = false;
                showTime = 0;
                levelDescriptor.setVisible(false);
            }
        }
    }

    public void render(float dt) {
        stage.act(dt);
        stage.draw();
    }

    /**
     * Starts the slide in animation of the level descriptor when
     * the player first enters the level.
     */
    public void startLevelDescriptor() {
        int worldIndex = gameScreen.gameMap.worldIndex;
        int levelIndex = gameScreen.gameMap.levelIndex;
        String worldName = rm.worlds.get(worldIndex).name;
        String levelName = rm.worlds.get(worldIndex).levels[levelIndex].name;

        levelDescriptor.getTitleLabel().setText("WORLD " + (worldIndex + 1) + " : " + "LEVEL " + (levelIndex + 1));
        levelDesc.setText(worldName + "\n" + levelName + "\n" + "AVG LVL: " + rm.worlds.get(worldIndex).levels[levelIndex].avgLevel);
        levelDescriptor.setVisible(true);
        levelDescriptor.pack();

        float y = 116 - levelDescriptor.getPrefHeight();
        levelMoving.origin.set(-levelDescriptor.getPrefWidth(), y);
        levelMoving.target.set(4, y);

        ld = true;
        levelMoving.start();
    }

    /**
     * Turns the HUD on and off when another event occurs
     *
     * @param toggle
     */
    public void toggle(boolean toggle) {
        if (toggle) {
            gameScreen.getGame().fps.setPosition(5, 5);
            stage.addActor(gameScreen.getGame().fps);
        }
        for (int i = 0; i < 4; i++) dirPad[i].setVisible(toggle);
        for (int i = 0; i < 2; i++) optionButtons[i].setVisible(toggle);
        levelDescriptor.setVisible(toggle);
    }

    /**
     * Draws the directional pad and applies Drawable effects
     * Unfortunately have to do each one separately
     */
    private void createDirPad() {
        dirPad = new ImageButton[4];

        // when each button is pressed it changes for a more visible effect
        ImageButton.ImageButtonStyle[] styles = rm.loadImageButtonStyles(4, rm.dirpad20x20);

        // down
        dirPad[0] = new ImageButton(styles[0]);
        dirPad[0].setPosition(Util.DIR_PAD_SIZE + Util.DIR_PAD_OFFSET, Util.DIR_PAD_OFFSET);
        // up
        dirPad[1] = new ImageButton(styles[1]);
        dirPad[1].setPosition(Util.DIR_PAD_SIZE + Util.DIR_PAD_OFFSET, (Util.DIR_PAD_SIZE * 2) + Util.DIR_PAD_OFFSET);
        // right
        dirPad[2] = new ImageButton(styles[2]);
        dirPad[2].setPosition((Util.DIR_PAD_SIZE * 2) + Util.DIR_PAD_OFFSET, Util.DIR_PAD_SIZE + Util.DIR_PAD_OFFSET);
        // left
        dirPad[3] = new ImageButton(styles[3]);
        dirPad[3].setPosition(Util.DIR_PAD_OFFSET, Util.DIR_PAD_SIZE + Util.DIR_PAD_OFFSET);

        handleDirPadEvents();

        if (Unlucky.DISABLE_PAD) return;
        for (ImageButton imageButton : dirPad) {
            stage.addActor(imageButton);
        }

    }

    /**
     * Creates the two option buttons: inventoryUI and settings
     */
    private void createOptionButtons() {
        optionButtons = new ImageButton[2];

        ImageButton.ImageButtonStyle[] styles = rm.loadImageButtonStyles(2, rm.optionbutton32x32);
        for (int i = 0; i < 2; i++) {
            optionButtons[i] = new ImageButton(styles[i]);
            optionButtons[i].setPosition(155 + (i * 25), 100);
            stage.addActor(optionButtons[i]);
        }
        handleOptionEvents();
    }

    /**
     * Creates the level descriptor window
     */
    private void createLevelDescriptor() {
        levelDescriptor = new Window("", rm.skin);
        levelDescriptor.getTitleLabel().setFontScale(0.5f);
        levelDescriptor.getTitleLabel().setAlignment(Align.center);
        levelDescriptor.setMovable(false);
        levelDescriptor.setTouchable(Touchable.disabled);
        levelDescriptor.setKeepWithinStage(false);
        levelDescriptor.setVisible(false);
        levelDesc = new Label("", new Label.LabelStyle(rm.pixel10, Color.WHITE));
        levelDesc.setFontScale(0.5f);
        levelDesc.setAlignment(Align.center);
        levelDescriptor.left();
        levelDescriptor.padTop(12);
        levelDescriptor.padLeft(2);
        levelDescriptor.padBottom(4);
        levelDescriptor.add(levelDesc).width(70);
        stage.addActor(levelDescriptor);
        levelMoving = new Moving(new Vector2(), new Vector2(), 150.f);
    }

    /**
     * Creates the death screen message
     */
    private void createDeathPrompt() {
        deathGroup = new Group();
        deathGroup.setTransform(false);
        deathGroup.setVisible(false);
        deathGroup.setSize(Unlucky.V_WIDTH, Unlucky.V_HEIGHT);
        deathGroup.setTouchable(Touchable.enabled);

        dark = new Image(rm.shade);
        deathGroup.addActor(dark);

        frame = new Image(rm.skin, "textfield");
        frame.setSize(100, 60);
        frame.setPosition(Unlucky.V_WIDTH / 2.0f - 50, Unlucky.V_HEIGHT / 2.0f - 30);
        deathGroup.addActor(frame);

        youDied = new Label("YOU DIED!", new Label.LabelStyle(rm.pixel10, Color.RED));
        youDied.setSize(100, 10);
        youDied.setPosition(50, 75);
        youDied.setAlignment(Align.center);
        youDied.setTouchable(Touchable.disabled);
        deathGroup.addActor(youDied);

        loss = new Label("", new Label.LabelStyle(rm.pixel10, Color.WHITE));
        loss.setFontScale(0.5f);
        loss.setWrap(true);
        loss.setSize(100, 40);
        loss.setAlignment(Align.top);
        loss.setPosition(Unlucky.V_WIDTH / 2.0f - 50, Unlucky.V_HEIGHT / 2.0f - 30);
        loss.setTouchable(Touchable.disabled);
        deathGroup.addActor(loss);

        // click to continue
        deathGroup.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                backToMenu();
            }
        });

        stage.addActor(deathGroup);
    }

    /**
     * Death text displays the items the player would've got, the gold and exp lost
     * @param text
     */
    public void setDeathText(String text) {
        loss.setText(text);
    }

    public void backToMenu() {
        game.menuScreen.transitionIn = 0;
        if (gameScreen.gameMap.weather != WeatherType.NORMAL) {
            rm.lightrain.stop(gameScreen.gameMap.soundId);
            rm.heavyrain.stop(gameScreen.gameMap.soundId);
        }
        if (gameScreen.isClickable()) {
            gameScreen.setClickable(false);
            gameScreen.setBatchFade(false);
            // fade out animation
            stage.addAction(Actions.sequence(Actions.fadeOut(0.3f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        gameScreen.setClickable(true);
                        gameScreen.gameMap.mapTheme.stop();
                        game.setScreen(game.menuScreen);
                    }
                })));
        }
    }

    public void loseObtained() {
        player.addGold(-gameScreen.gameMap.goldObtained);
        player.addExp(-gameScreen.gameMap.expObtained);
        if (gameScreen.gameMap.itemsObtained.size != 0) {
            for (Item item : gameScreen.gameMap.itemsObtained) {
                for (int i = 0; i < Inventory.NUM_SLOTS; i++) {
                    if (player.inventory.getItem(i) == item)
                        player.inventory.removeItem(i);
                }
            }
        }
    }

    /**
     * Player quits the level and returns to the main menu screen
     * The player will lose all gold, exp, and items obtained during the level
     */
    private void quit() {
        final String text = "If you quit, you will lose all \ngold, exp, and items obtained in this level.\n" +
            "Are you sure you want to quit?";
            new Dialog("Warning", rm.dialogSkin) {
            {
                Label l = new Label(text, rm.dialogSkin);
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
                if (!game.player.settings.muteSfx) rm.buttonclick2.play(game.player.settings.sfxVolume);
                if (object.equals("yes")) {
                    loseObtained();
                    player.setHp(player.getMaxHp());
                    player.inMap = false;
                    if (gameScreen.gameMap.weather != WeatherType.NORMAL) {
                        rm.lightrain.stop(gameScreen.gameMap.soundId);
                        rm.heavyrain.stop(gameScreen.gameMap.soundId);
                    }
                    backToMenu();
                }
                else settingsDialog.show(stage);
            }
        }.show(stage).getTitleLabel().setAlignment(Align.center);
    }

    /**
     * Handles player movement commands
     */
    private void handleDirPadEvents() {
        for (int i = 0; i < 4; i++) {
            final int index = i;
            dirPad[i].addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    dirTime = 0;
                    touchDown = true;
                    dirIndex = index;
                    return true;
                }
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    touchDown = false;
                }
            });
        }
    }

    private void movePlayer(int dir) {
        if (player.canMove()) player.getAm().setAnimation(dir);
        if (player.canMove() && !player.nextTileBlocked(dir)) {
            player.move(dir);
        }
    }

    /**
     * Handles two option button commands
     */
    private void handleOptionEvents() {
        // inventoryUI
        optionButtons[0].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                toggle(false);
                gameScreen.setCurrentEvent(EventState.INVENTORY);
                gameScreen.getGame().inventoryUI.init(false, null);
                gameScreen.getGame().inventoryUI.start();
            }
        });

        // settings
        optionButtons[1].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                shade.setVisible(true);
                toggle(false);

                // pause music and sfx
                gameScreen.gameMap.mapTheme.pause();
                if (gameScreen.gameMap.weather != WeatherType.NORMAL) {
                    rm.lightrain.stop(gameScreen.gameMap.soundId);
                    rm.heavyrain.stop(gameScreen.gameMap.soundId);
                }

                gameScreen.setCurrentEvent(EventState.PAUSE);
                settingsDialog.show(stage);
            }
        });
    }

}