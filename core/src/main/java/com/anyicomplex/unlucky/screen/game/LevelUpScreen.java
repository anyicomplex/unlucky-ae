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

package com.anyicomplex.unlucky.screen.game;

import com.anyicomplex.unlucky.animation.AnimationManager;
import com.anyicomplex.unlucky.entity.Player;
import com.anyicomplex.unlucky.event.EventState;
import com.anyicomplex.unlucky.map.TileMap;
import com.anyicomplex.unlucky.resource.ResourceManager;
import com.anyicomplex.unlucky.screen.GameScreen;
import com.anyicomplex.unlucky.ui.UI;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.Arrays;

/**
 * Displays a level up screen showing the player's old stats and an animation
 * increasing the stats to their new values
 *
 * @author Ming Li
 */
public class LevelUpScreen extends UI {

    // Scene2D
    private ImageButton ui;
    private Label title;
    private Label levelDesc;
    private Label[] statsDescs;
    private String[] statNames = { "MAX EXP:", "ACCURACY:", "MAX DMG:", "MIN DMG:", "MAX HP:" };
    private Label[] stats;
    private Label[] increases;
    private Label clickToContinue;

    // animation
    private AnimationManager levelUpAnim;
    private float stateTime = 0;
    private boolean showClick = true;

    // event
    // stats number animation
    private boolean sAnimFinished = false;
    private boolean startAnim = false;
    private int[] statsNum = new int[5];
    private int[] increasedStats = new int[5];
    private float statsTime = 0;

    public LevelUpScreen(GameScreen gameScreen, TileMap tileMap, Player player, ResourceManager rm) {
        super(gameScreen, tileMap, player, rm);

        // create bg
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(rm.levelupscreen400x240);
        ui = new ImageButton(style);
        ui.setSize(200, 120);
        ui.setPosition(0, 0);
        ui.setTouchable(Touchable.disabled);
        stage.addActor(ui);

        handleClick();

        // create animation
        levelUpAnim = new AnimationManager(rm.levelUp96x96, 4, 0, 1 / 4f);

        // create labels
        BitmapFont font = rm.pixel10;
        Label.LabelStyle titleFont = new Label.LabelStyle(font, new Color(0, 205 / 255.f, 20 / 255.f, 1));
        Label.LabelStyle stdWhite = new Label.LabelStyle(font, new Color(1, 1, 1, 1));
        Label.LabelStyle yellow = new Label.LabelStyle(font, new Color(1, 212 / 255.f, 0, 1));
        Label.LabelStyle blue = new Label.LabelStyle(font, new Color(0, 190 / 255.f, 1, 1));

        title = new Label("LEVEL UP!", titleFont);
        title.setSize(200, 20);
        title.setPosition(0, 95);
        title.setFontScale(2.5f);
        title.setAlignment(Align.center);
        title.setTouchable(Touchable.disabled);
        stage.addActor(title);

        levelDesc = new Label("You reached level 1", stdWhite);
        levelDesc.setSize(200, 20);
        levelDesc.setPosition(0, 80);
        levelDesc.setAlignment(Align.center);
        levelDesc.setTouchable(Touchable.disabled);
        stage.addActor(levelDesc);

        statsDescs = new Label[statNames.length];
        stats = new Label[statNames.length];
        increases = new Label[statNames.length];

        for (int i = 0; i < statNames.length; i++) {
            statsDescs[i] = new Label(statNames[i], stdWhite);
            statsDescs[i].setSize(10, 10);
            statsDescs[i].setFontScale(1.3f / 2);
            statsDescs[i].setPosition(100, 17 + (i * 12));
            statsDescs[i].setAlignment(Align.left);
            statsDescs[i].setTouchable(Touchable.disabled);
            stage.addActor(statsDescs[i]);

            stats[i] = new Label("1330", blue);
            stats[i].setSize(10, 10);
            stats[i].setFontScale(1.3f / 2);
            stats[i].setPosition(140, 17 + (i * 12));
            stats[i].setAlignment(Align.left);
            stats[i].setTouchable(Touchable.disabled);
            stage.addActor(stats[i]);

            increases[i] = new Label("+20", yellow);
            increases[i].setSize(10, 10);
            increases[i].setFontScale(1.3f / 2);
            increases[i].setPosition(170, 17 + (i * 12));
            increases[i].setAlignment(Align.left);
            increases[i].setTouchable(Touchable.disabled);
            stage.addActor(increases[i]);
        }

        clickToContinue = new Label("Click to continue", stdWhite);
        clickToContinue.setSize(200, 10);
        clickToContinue.setFontScale(0.5f);
        clickToContinue.setPosition(0, 2);
        clickToContinue.setAlignment(Align.center);
        clickToContinue.setTouchable(Touchable.disabled);
        stage.addActor(clickToContinue);
    }

    public void start() {
        reset();
        statsNum[0] = player.getMaxExp();
        statsNum[1] = player.getAccuracy();
        statsNum[2] = player.getMaxDamage();
        statsNum[3] = player.getMinDamage();
        statsNum[4] = player.getMaxHp();
        increasedStats[0] = statsNum[0] + player.getMaxExpIncrease();
        increasedStats[1] = statsNum[1] + player.getAccuracyIncrease();
        increasedStats[2] = statsNum[2] + player.getMaxDmgIncrease();
        increasedStats[3] = statsNum[3] + player.getMinDmgIncrease();
        increasedStats[4] = statsNum[4] + player.getHpIncrease();

        increases[0].setText("+" + player.getMaxExpIncrease());
        increases[1].setText("+" + player.getAccuracyIncrease());
        increases[2].setText("+" + player.getMaxDmgIncrease());
        increases[3].setText("+" + player.getMinDmgIncrease());
        increases[4].setText("+" + player.getHpIncrease());

        for (int i = 0; i < 5; i++) {
            stats[i].setText(String.valueOf(statsNum[i]));
        }
        for (int i = 0; i < statNames.length; i++) {
            increases[i].setVisible(true);
        }
        ui.setTouchable(Touchable.enabled);
        // update information
        levelDesc.setText("You reached level " + player.getLevel());

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                switch (keycode) {
                    case Input.Keys.ENTER:
                    case Input.Keys.NUMPAD_ENTER:
                        performClick();
                        return true;
                }
                return super.keyUp(keycode);
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void handleClick() {
        ui.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                performClick();
            }
        });
    }

    public void performClick() {
        if (!ui.isTouchable()) return;
        if (sAnimFinished) {
            // switch to transition screen
            gameScreen.setCurrentEvent(EventState.TRANSITION);
            gameScreen.transition.start(EventState.LEVEL_UP, EventState.MOVING);
            Gdx.input.setInputProcessor(gameScreen.multiplexer);
            reset();
        }
        // start
        else if (!sAnimFinished && !startAnim) {
            // start stats animation
            startAnim = true;
            for (int i = 0; i < statNames.length; i++) {
                increases[i].setVisible(false);
            }
            player.applyLevelUp();
        }
        // finish animation early
        else if (!sAnimFinished && startAnim) {
            for (int i = 0; i < 5; i++) {
                statsNum[i] = increasedStats[i];
                stats[i].setText(String.valueOf(statsNum[i]));
            }
            sAnimFinished = true;
        }
    }

    /**
     * Mainly just setting labels back to visible
     */
    public void reset() {
        startAnim = false;
        sAnimFinished = false;
        statsTime = stateTime = 0;
        ui.setTouchable(Touchable.disabled);
    }

    public void update(float dt) {
        // update animation
        levelUpAnim.update(dt);
        stateTime += dt;
        if (stateTime > 0.5f) {
            showClick = !showClick;
            stateTime = 0;
        }

        // animation
        if (startAnim) {
            if (!sAnimFinished) {
                statsTime += dt;
                if (statsTime > 0.05f) {
                    for (int i = 0; i < 5; i++) {
                        if (statsNum[i] < increasedStats[i])
                            statsNum[i]++;
                        stats[i].setText(String.valueOf(statsNum[i]));
                    }
                    statsTime = 0;
                }
                // animation finished
                if (Arrays.equals(statsNum, increasedStats)) sAnimFinished = true;
            }
        }

        if (showClick) clickToContinue.setVisible(true);
        else clickToContinue.setVisible(false);

    }

    public void render(float dt) {
        stage.act(dt);
        stage.draw();

        gameScreen.getBatch().setProjectionMatrix(stage.getCamera().combined);
        gameScreen.getBatch().begin();
        gameScreen.getBatch().draw(levelUpAnim.getKeyFrame(true), 23, 27);
        gameScreen.getBatch().end();
    }

}
