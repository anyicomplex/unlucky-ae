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

import com.anyicomplex.unlucky.Unlucky;
import com.anyicomplex.unlucky.inventory.Item;
import com.anyicomplex.unlucky.map.GameMap;
import com.anyicomplex.unlucky.resource.ResourceManager;
import com.anyicomplex.unlucky.screen.AbstractScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

/**
 * The screen that appears after the player successfully completes a level
 * Shows time of completion, gold obtained, items obtained, exp obtained, etc
 *
 * @author Ming Li
 */
public class VictoryScreen extends AbstractScreen {

    // banner
    private Image bannerBg;
    private Label bannerText;

    // exit button
    private ImageButton exitButton;
    private Label nextLabel;

    // next button
    private ImageButton nextButton;

    // information
    private Image infoBg;
    private Label info;
    private static final int NUM_COLS = 5;

    private GameMap gameMap;

    public VictoryScreen(final Unlucky game, final ResourceManager rm) {
        super(game, rm);

        bannerBg = new Image(rm.skin, "default-slider");
        bannerBg.setSize(120, 18);
        bannerBg.setPosition(Unlucky.V_WIDTH / 2.0f - 70, 96);
        stage.addActor(bannerBg);

        bannerText = new Label("VICTORY", new Label.LabelStyle(rm.pixel10, new Color(0, 215 / 255.f, 0, 1)));
        bannerText.setFontScale(1.5f);
        bannerText.setSize(120, 18);
        bannerText.setPosition(Unlucky.V_WIDTH / 2.0f - 70, 96);
        bannerText.setAlignment(Align.center);
        stage.addActor(bannerText);

        // init exit button
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(rm.menuExitButton[0][0]);
        style.imageDown = new TextureRegionDrawable(rm.menuExitButton[1][0]);
        exitButton = new ImageButton(style);
        exitButton.setSize(18, 18);
        exitButton.setPosition(177, 96);
        stage.addActor(exitButton);

        exitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                for (Item item : gameMap.itemsObtained) item.actor.remove();
                game.menuScreen.transitionIn = 0;
                setFadeScreen(game.menuScreen);
            }
        });

        infoBg = new Image(rm.skin, "default-slider");
        infoBg.setSize(120, 88);
        infoBg.setPosition(Unlucky.V_WIDTH / 2.0f - 70, 4);
        stage.addActor(infoBg);

        info = new Label("", new Label.LabelStyle(rm.pixel10, Color.WHITE));
        info.setFontScale(0.5f);
        info.setWrap(true);
        info.setAlignment(Align.topLeft);
        info.setSize(112, 50);
        info.setPosition(Unlucky.V_WIDTH / 2.0f - 70 + 4, 38);
        stage.addActor(info);

        ImageButton.ImageButtonStyle nextStyle = new ImageButton.ImageButtonStyle();
        nextStyle.imageUp = new TextureRegionDrawable(rm.smoveButtons[0][0]);
        nextStyle.imageDown = new TextureRegionDrawable(rm.smoveButtons[1][0]);

        nextButton = new ImageButton(nextStyle);
        nextButton.setPosition(157, 8);
        stage.addActor(nextButton);

        nextLabel = new Label("NEXT", new Label.LabelStyle(rm.pixel10, Color.WHITE));
        nextLabel.setFontScale(0.5f);
        nextLabel.setTouchable(Touchable.disabled);
        nextLabel.setSize(38, 18);
        nextLabel.setAlignment(Align.center);
        nextLabel.setPosition(154, 8);
        stage.addActor(nextLabel);

        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameMap.levelIndex != rm.worlds.get(gameMap.worldIndex).numLevels - 1) {
                    // switch back to level select screen
                    for (Item item : gameMap.itemsObtained) item.actor.remove();
                    game.levelSelectScreen.setWorld(gameMap.worldIndex);
                    rm.menuTheme.play();
                    setFadeScreen(game.levelSelectScreen);
                }
            }
        });
    }

    public void init(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    @Override
    public void show() {

        nextButton.setVisible(gameMap.levelIndex != rm.worlds.get(gameMap.worldIndex).numLevels - 1);
        nextLabel.setVisible(gameMap.levelIndex != rm.worlds.get(gameMap.worldIndex).numLevels - 1);

        game.fps.setPosition(5, 115);
        stage.addActor(game.fps);

        Gdx.input.setInputProcessor(stage);

        batchFade = true;

        renderBatch = false;
        // fade in animation
        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.run(new Runnable() {
            @Override
            public void run() {
                renderBatch = true;
            }
        }), Actions.fadeIn(0.5f)));

        String infoText = rm.worlds.get(gameMap.worldIndex).name + ": " +
            rm.worlds.get(gameMap.worldIndex).levels[gameMap.levelIndex].name + " completed!\n\n" +
            "Time: " + Math.round(gameMap.time) + " seconds\n\n" +
            "Total gold obtained: " + gameMap.goldObtained + "\n" +
            "Total experience obtained: " + gameMap.expObtained + "\n\n" +
            "Items obtained: ";
        info.setText(infoText);

        // show items obtained's image actors in a grid
        for (int i = 0; i < gameMap.itemsObtained.size; i++) {
            int x = i % NUM_COLS;
            int y = i / NUM_COLS;
            Item item = gameMap.itemsObtained.get(i);
            item.actor.remove();
            item.actor.setPosition(Unlucky.V_WIDTH / 2.0f - 70 + 8 + (x * 24), 34 - (y * 16));
            stage.addActor(item.actor);
        }

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (!clickable) return super.keyDown(keycode);
                switch (keycode) {
                    case Input.Keys.ENTER:
                    case Input.Keys.NUMPAD_ENTER:
                        if (gameMap.levelIndex != rm.worlds.get(gameMap.worldIndex).numLevels - 1) {
                            // switch back to level select screen
                            for (Item item : gameMap.itemsObtained) item.actor.remove();
                            game.levelSelectScreen.setWorld(gameMap.worldIndex);
                            rm.menuTheme.play();
                            Gdx.input.setInputProcessor(stage);
                            setFadeScreen(game.levelSelectScreen);
                        }
                        return true;
                    case Input.Keys.ESCAPE:
                        for (Item item : gameMap.itemsObtained) item.actor.remove();
                        game.menuScreen.transitionIn = 0;
                        Gdx.input.setInputProcessor(stage);
                        setFadeScreen(game.menuScreen);
                        return true;
                }
                return super.keyDown(keycode);
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void update(float dt) {}

    public void render(float dt) {
        update(dt);

        if (renderBatch) {
            stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
            stage.getBatch().begin();

            // fix fading
            if (batchFade) stage.getBatch().setColor(Color.WHITE);

            // render world background corresponding to the selected world
            //stage.getBatch().draw(rm.worldSelectBackgrounds[gameMap.worldIndex], 0, 0);
            stage.getBatch().draw(rm.worldSelectBackgrounds[gameMap.worldIndex], 0, 0);

            stage.getBatch().end();
        }

        super.render(dt);
    }

}
