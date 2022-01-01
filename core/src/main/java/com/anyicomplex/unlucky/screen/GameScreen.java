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
import com.anyicomplex.unlucky.event.Battle;
import com.anyicomplex.unlucky.event.EventState;
import com.anyicomplex.unlucky.map.GameMap;
import com.anyicomplex.unlucky.map.WeatherType;
import com.anyicomplex.unlucky.parallax.Background;
import com.anyicomplex.unlucky.resource.ResourceManager;
import com.anyicomplex.unlucky.screen.game.DialogScreen;
import com.anyicomplex.unlucky.screen.game.LevelUpScreen;
import com.anyicomplex.unlucky.screen.game.TransitionScreen;
import com.anyicomplex.unlucky.ui.Hud;
import com.anyicomplex.unlucky.ui.battleui.BattleUIHandler;
import com.anyicomplex.unlucky.util.Disposer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

/**
 * Handles all gameplay.
 *
 * @author Ming Li
 */
public class GameScreen extends AbstractScreen {

    public EventState currentEvent;

    public GameMap gameMap;
    public Hud hud;
    public BattleUIHandler battleUIHandler;
    public Battle battle;
    public TransitionScreen transition;
    public LevelUpScreen levelUp;
    public DialogScreen dialog;

    // input
    public InputMultiplexer multiplexer;

    // battle background
    private Background[] bg;

    // key
    private int worldIndex;
    private int levelIndex;

    // whether or not to reset the game map on show
    // used for transitioning between screen during a pause
    public boolean resetGame = true;

    public GameScreen(final Unlucky game, final ResourceManager rm) {
        super(game, rm);

        currentEvent = EventState.MOVING;

        gameMap = new GameMap(this, game.player, rm);
        battle = new Battle(this, gameMap.tileMap, gameMap.player);
        hud = new Hud(this, gameMap.tileMap, gameMap.player, rm);
        battleUIHandler = new BattleUIHandler(this, gameMap.tileMap, gameMap.player, battle, rm);
        transition = new TransitionScreen(this, battle, battleUIHandler, hud, gameMap.player, rm);
        levelUp = new LevelUpScreen(this, gameMap.tileMap, gameMap.player, rm);
        dialog = new DialogScreen(this, gameMap.tileMap, gameMap.player, rm);

        // create bg
        bg = new Background[2];
        // sky
        bg[0] = new Background((OrthographicCamera) battleUIHandler.getStage().getCamera(), new Vector2(0.3f, 0));
        // field
        bg[1] = new Background((OrthographicCamera) battleUIHandler.getStage().getCamera(), new Vector2(0, 0));


        // input multiplexer
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(hud.getStage());
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (!clickable) return super.keyDown(keycode);
                if (game.inventoryUI.ui.moving.shouldStart) return super.keyDown(keycode);
                switch (currentEvent) {
                    case TILE_EVENT:
                        switch (keycode) {
                            case Input.Keys.ENTER:
                            case Input.Keys.NUMPAD_ENTER:
                                dialog.performClick();
                                break;
                        }
                        return true;
                    case BATTLING:
                        switch (keycode) {
                            case Input.Keys.ENTER:
                            case Input.Keys.NUMPAD_ENTER:
                                battleUIHandler.battleEventHandler.performClick();
                                break;
                            case Input.Keys.NUM_1:
                            case Input.Keys.NUMPAD_1:
                                battleUIHandler.moveUI.performMove(0);
                                break;
                            case Input.Keys.NUM_2:
                            case Input.Keys.NUMPAD_2:
                                battleUIHandler.moveUI.performMove(1);
                                break;
                            case Input.Keys.NUM_3:
                            case Input.Keys.NUMPAD_3:
                                battleUIHandler.moveUI.performMove(2);
                                break;
                            case Input.Keys.NUM_4:
                            case Input.Keys.NUMPAD_4:
                                battleUIHandler.moveUI.performMove(3);
                                break;
                            case Input.Keys.M:
                                battleUIHandler.moveUI.performSpecialMove();
                                break;
                            case Input.Keys.BACK:
                            case Input.Keys.ESCAPE:
                                battleUIHandler.moveUI.performEscape();
                                break;
                        }
                        return true;
                    case DEATH:
                        if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                        hud.backToMenu();
                        return true;
                    case MOVING:
                        switch (keycode) {
                            case Input.Keys.BACK:
                            case Input.Keys.ESCAPE:
                                if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                                hud.shade.setVisible(true);
                                hud.toggle(false);

                                // pause music and sfx
                                gameMap.mapTheme.pause();
                                if (gameMap.weather != WeatherType.NORMAL) {
                                    rm.lightrain.stop(gameMap.soundId);
                                    rm.heavyrain.stop(gameMap.soundId);
                                }

                                setCurrentEvent(EventState.PAUSE);
                                hud.settingsDialog.show(hud.getStage());
                                return true;
                            case Input.Keys.E:
                                if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                                hud.toggle(false);
                                setCurrentEvent(EventState.INVENTORY);
                                getGame().inventoryUI.init(false, null);
                                getGame().inventoryUI.start();
                                return true;
                            case Input.Keys.W:
                            case Input.Keys.UP:
                                hud.dirIndex = 1;
                                hud.dirTime = 0;
                                hud.touchDown = true;
                                break;
                            case Input.Keys.A:
                            case Input.Keys.LEFT:
                                hud.dirIndex = 3;
                                hud.dirTime = 0;
                                hud.touchDown = true;
                                break;
                            case Input.Keys.S:
                            case Input.Keys.DOWN:
                                hud.dirIndex = 0;
                                hud.dirTime = 0;
                                hud.touchDown = true;
                                break;
                            case Input.Keys.D:
                            case Input.Keys.RIGHT:
                                hud.dirIndex = 2;
                                hud.dirTime = 0;
                                hud.touchDown = true;
                                break;
                            default:
                                return super.keyDown(keycode);
                        }
                        return true;
                    case PAUSE:
                    case LEVEL_UP:
                    case TRANSITION:
                    case INVENTORY:
                    case NONE:
                        return super.keyDown(keycode);
                }
                return super.keyDown(keycode);
            }

            @Override
            public boolean keyUp(int keycode) {
                switch (keycode) {
                    case Input.Keys.W:
                    case Input.Keys.A:
                    case Input.Keys.S:
                    case Input.Keys.D:
                    case Input.Keys.UP:
                    case Input.Keys.LEFT:
                    case Input.Keys.RIGHT:
                    case Input.Keys.DOWN:
                        hud.touchDown = false;
                        return true;
                }
                return false;
            }
        });
        multiplexer.addProcessor(battleUIHandler.getStage());
        multiplexer.addProcessor(levelUp.getStage());
        multiplexer.addProcessor(dialog.getStage());
    }

    @Override
    public void resize(int width, int height) {
        hud.getStage().getViewport().update(width, height);
        battleUIHandler.getStage().getViewport().update(width, height);
        levelUp.getStage().getViewport().update(width, height);
        dialog.getStage().getViewport().update(width, height);
        game.inventoryUI.getStage().getViewport().update(width, height);
        super.resize(width, height);
    }

    public void init(int worldIndex, int levelIndex) {
        this.worldIndex = worldIndex;
        this.levelIndex = levelIndex;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiplexer);
        batchFade = renderBatch = true;

        // fade in animation
        hud.getStage().addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.5f)));

        if (resetGame) {
            // init tile map
            setCurrentEvent(EventState.MOVING);
            hud.deathGroup.setVisible(false);
            gameMap.init(worldIndex, levelIndex);
            gameMap.player.moving = -1;
            battle.tileMap = gameMap.tileMap;
            hud.setTileMap(gameMap.tileMap);
            battleUIHandler.setTileMap(gameMap.tileMap);
            levelUp.setTileMap(gameMap.tileMap);
            dialog.setTileMap(gameMap.tileMap);

            // update bg
            createBackground(gameMap.worldIndex);

            hud.toggle(true);
            hud.touchDown = false;
            hud.shade.setVisible(false);
            hud.startLevelDescriptor();
        }
    }

    /**
     * Creates the dynamic background
     * @param bgIndex is the theme of bg
     */
    private void createBackground(int bgIndex) {
        // background image array is ordered by depth
        TextureRegion[] images = rm.battleBackgrounds400x240[bgIndex];
        for (int i = 0; i < 2; i++) bg[i].setImage(images[i]);
        // set background movement for the specific worlds
        if (bgIndex == 0) bg[0].setVector(40, 0);
        else if (bgIndex == 1) bg[0].setVector(0, 0);
        else if (bgIndex == 2) bg[0].setVector(40, 0);
        bg[1].setVector(0, 0);
    }

    /**
     * When the player dies it shows a "click to continue" message along with what they lost
     */
    public void die() {
        // reset player's hp after dying
        gameMap.player.setHp(gameMap.player.getMaxHp());
        setCurrentEvent(EventState.DEATH);
        hud.toggle(false);
        hud.deathGroup.setVisible(true);
    }

    /**
     * Updates the camera position to follow the player unless he's on the edges of the map
     */
    public void updateCamera() {
        // camera directs on the player
        if (gameMap.player.getPosition().x <= gameMap.tileMap.mapWidth * 16 - 7 * 16 &&
            gameMap.player.getPosition().x >= 6 * 16)
            cam.position.x = gameMap.player.getPosition().x + 8;
        if (gameMap.player.getPosition().y <= gameMap.tileMap.mapHeight * 16 - 4 * 16 &&
            gameMap.player.getPosition().y >= 4 * 16 - 8)
            cam.position.y = gameMap.player.getPosition().y + 4;
        cam.update();

        if (gameMap.player.getPosition().x < 6 * 16) cam.position.x = 104;
        if (gameMap.player.getPosition().y < 4 * 16 - 8) cam.position.y = 60.5f;
        if (gameMap.player.getPosition().x > gameMap.tileMap.mapWidth * 16 - 7 * 16)
            cam.position.x = (gameMap.tileMap.mapWidth * 16 - 7 * 16) + 8;
        if (gameMap.player.getPosition().y > gameMap.tileMap.mapHeight * 16 - 4 * 16)
            cam.position.y = (gameMap.tileMap.mapHeight * 16 - 4 * 16) + 4;
    }

    public void update(float dt) {
        if (currentEvent != EventState.PAUSE) {
            // update game time
            gameMap.time += dt;
        }

        if (currentEvent == EventState.MOVING) {
            updateCamera();

            gameMap.update(dt);
            hud.update(dt);
        }

        if (currentEvent == EventState.BATTLING) {
            // update bg
            for (Background background : bg) {
                background.update(dt);
            }
            battleUIHandler.update(dt);
        }

        if (currentEvent == EventState.TRANSITION) transition.update(dt);
        if (currentEvent == EventState.LEVEL_UP) levelUp.update(dt);
        if (currentEvent == EventState.TILE_EVENT) dialog.update(dt);
        if (currentEvent == EventState.INVENTORY) game.inventoryUI.update(dt);
    }

    public void render(float dt) {
        update(dt);

        // clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (renderBatch) {
            game.batch.begin();

            // fix fading
            if (batchFade) game.batch.setColor(Color.WHITE);

            if (currentEvent == EventState.BATTLING || transition.renderBattle) {
                // bg camera
                game.batch.setProjectionMatrix(battleUIHandler.getStage().getCamera().combined);
                for (Background background : bg) {
                    background.render(game.batch);
                }
            }

            if (currentEvent == EventState.MOVING || currentEvent == EventState.INVENTORY ||
                transition.renderMap || currentEvent == EventState.TILE_EVENT ||
                currentEvent == EventState.DEATH || currentEvent == EventState.PAUSE) {
                // map camera
                game.batch.setProjectionMatrix(cam.combined);
                // render map and player
                gameMap.render(dt, game.batch, cam);
            }

            game.batch.end();
        }

        if (currentEvent == EventState.MOVING || currentEvent == EventState.DEATH || currentEvent == EventState.PAUSE)
            hud.render(dt);
        if (currentEvent == EventState.BATTLING || transition.renderBattle)
            battleUIHandler.render(dt);
        if (currentEvent == EventState.LEVEL_UP || transition.renderLevelUp)
            levelUp.render(dt);
        if (currentEvent == EventState.TILE_EVENT) dialog.render(dt);
        if (currentEvent == EventState.INVENTORY) game.inventoryUI.render(dt);
        if (currentEvent == EventState.TRANSITION) transition.render(dt);

        //game.profile("GameScreen");
    }

    public void dispose() {
        super.dispose();
        Disposer.dispose(hud, battleUIHandler, dialog, levelUp);
    }

    /**
     * @TODO: Add some sort of transitioning between events
     * @param event
     */
    public void setCurrentEvent(EventState event) {
        this.currentEvent = event;
    }

}