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
import com.anyicomplex.unlucky.resource.ResourceManager;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

/**
 *
 * Screen that allows the player to modify settings of the game such as
 * music and sfx volume, toggle animations, show fps, etc.
 * Can be accessed either in game while paused or through the main menu.
 *
 * @author Ming Li
 */
public class SettingsScreen extends MenuExtensionScreen {

    // to be set when the player accesses the screen from in game
    public int worldIndex;

    // whether or not the player is accessing settings from in game
    public boolean inGame = false;

    // ui
    private Image banner;
    private Label bannerLabel;
    private Image bg;
    private Label.LabelStyle white;
    private Label description;

    private Label[] settingLabels;
    private Slider musicSlider;
    private Slider sfxSlider;
    private CheckBox muteMusic;
    private CheckBox muteSfx;
    private CheckBox showEnemyLevels;
    private CheckBox showWeatherAnims;
    private CheckBox showFps;
    private CheckBox fullscreen;

    private volatile boolean fullscreenClicked = false;
    private volatile boolean shouldCheckFullscreen = true;

    public SettingsScreen(final Unlucky game, final ResourceManager rm) {
        super(game, rm);

        // exit button
        stage.addActor(exitButton);
        exitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                if (inGame) {
                    game.gameScreen.resetGame = false;
                    setFadeScreen(game.gameScreen);
                    game.gameScreen.hud.settingsDialog.show(game.gameScreen.hud.getStage());
                }
                else {
                    game.menuScreen.transitionIn = 2;
                    setSlideScreen(game.menuScreen, false);
                }
            }
        });

        // create title label
        banner = new Image(rm.skin, "default-slider");
        banner.setPosition(8, 102);
        banner.setSize(164, 12);
        stage.addActor(banner);

        bannerLabel = new Label("SETTINGS", rm.skin);
        bannerLabel.setStyle(new Label.LabelStyle(rm.pixel10, new Color(1, 212 / 255.f, 0, 1)));
        bannerLabel.setSize(50, 12);
        bannerLabel.setTouchable(Touchable.disabled);
        bannerLabel.setPosition(14, 102);
        bannerLabel.setAlignment(Align.left);
        stage.addActor(bannerLabel);

        bg = new Image(rm.skin, "default-slider");
        bg.setPosition(8, 8);
        bg.setSize(184, 88);
        stage.addActor(bg);

        white = new Label.LabelStyle(rm.pixel10, Color.WHITE);
        description = new Label("SOUND                                 MISC",
            new Label.LabelStyle(rm.pixel10, new Color(1, 212 / 255.f, 0, 1)));
        description.setFontScale(0.75f);
        description.setTouchable(Touchable.disabled);
        description.setPosition(14, 85);
        stage.addActor(description);

        // create settings labels
        settingLabels = new Label[8];
        String[] settingStrs = new String[] {
            "MUSIC VOLUME", "SFX VOLUME", "MUTE MUSIC:", "MUTE SFX:",
            "SHOW ENEMY LEVELS:", "WEATHER ANIMATIONS:", "SHOW FPS:", "FULLSCREEN:"
        };
        for (int i = 0; i < 8; i ++) {
            settingLabels[i] = new Label(settingStrs[i], white);
            settingLabels[i].setTouchable(Touchable.disabled);
            settingLabels[i].setFontScale(0.5f);
            stage.addActor(settingLabels[i]);
        }
        for (int i = 0; i < 2; i ++) settingLabels[i].setPosition(14, 76 - i * 24);
        for (int i = 2; i < 4; i ++) settingLabels[i].setPosition(14, 26 - (i - 2) * 14);
        for (int i = 4; i < 8; i ++) settingLabels[i].setPosition(111, 72 - (i - 4) * 16);

        createSliders();
        createCheckboxes();
    }

    /**
     * Creates the volume sliders for the music and sound effects
     */
    private void createSliders() {
        musicSlider = new Slider(0.f, 1.f, 0.02f, false, rm.skin);
        musicSlider.setPosition(14, 64);
        musicSlider.setSize(75, 10);
        stage.addActor(musicSlider);

        sfxSlider = new Slider(0.f, 1.f, 0.02f, false, rm.skin);
        sfxSlider.setPosition(14, 40);
        sfxSlider.setSize(75, 10);
        stage.addActor(sfxSlider);

        musicSlider.setValue(game.player.settings.musicVolume);
        sfxSlider.setValue(game.player.settings.sfxVolume);

        // slider events
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // save to player's settings
                game.player.settings.musicVolume = musicSlider.getValue();
                if (!game.player.settings.muteMusic) rm.setMusicVolume(musicSlider.getValue());
                if (!inGame) if (!musicSlider.isDragging()) game.save.save();
            }
        });
        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.player.settings.sfxVolume = sfxSlider.getValue();
                if (!inGame) if (!sfxSlider.isDragging()) game.save.save();
            }
        });
    }

    /**
     * Creates the checkboxes for the toggle settings
     */
    private void createCheckboxes() {
        muteMusic = new CheckBox("", rm.skin);
        muteMusic.setPosition(50, 25);
        stage.addActor(muteMusic);

        muteSfx = new CheckBox("", rm.skin);
        muteSfx.setPosition(50, 10);
        stage.addActor(muteSfx);

        showEnemyLevels = new CheckBox("", rm.skin);
        showEnemyLevels.setPosition(170, 71);
        stage.addActor(showEnemyLevels);
        showWeatherAnims = new CheckBox("", rm.skin);
        showWeatherAnims.setPosition(170, 55);
        stage.addActor(showWeatherAnims);
        showFps = new CheckBox("", rm.skin);
        showFps.setPosition(170, 39);
        stage.addActor(showFps);
        fullscreen = new CheckBox("", rm.skin);
        fullscreen.setVisible(!Unlucky.DISABLE_FULLSCREEN);
        settingLabels[7].setVisible(!Unlucky.DISABLE_FULLSCREEN);

        fullscreen.setPosition(170, 23);
        stage.addActor(fullscreen);

        muteMusic.setChecked(game.player.settings.muteMusic);
        muteSfx.setChecked(game.player.settings.muteSfx);
        showEnemyLevels.setChecked(game.player.settings.showEnemyLevels);
        showWeatherAnims.setChecked(game.player.settings.showWeatherAnimations);
        showFps.setChecked(game.player.settings.showFps);
        fullscreen.setChecked(Gdx.graphics.isFullscreen());

        // checkbox events
        muteMusic.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!game.player.settings.muteSfx) rm.buttonclick2.play(game.player.settings.sfxVolume);
                game.player.settings.muteMusic = muteMusic.isChecked();
                if (muteMusic.isChecked()) rm.setMusicVolume(0f);
                else rm.setMusicVolume(game.player.settings.musicVolume);
                if (!inGame) game.save.save();
            }
        });
        muteSfx.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!game.player.settings.muteSfx) rm.buttonclick2.play(game.player.settings.sfxVolume);
                game.player.settings.muteSfx = muteSfx.isChecked();
                if (!inGame) game.save.save();
            }
        });
        showEnemyLevels.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!game.player.settings.muteSfx) rm.buttonclick2.play(game.player.settings.sfxVolume);
                game.player.settings.showEnemyLevels = showEnemyLevels.isChecked();
                if (!inGame) game.save.save();
            }
        });
        showWeatherAnims.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!game.player.settings.muteSfx) rm.buttonclick2.play(game.player.settings.sfxVolume);
                game.player.settings.showWeatherAnimations = showWeatherAnims.isChecked();
                if (inGame) {
                    if (showWeatherAnims.isChecked()) game.gameScreen.gameMap.setWeather(game.gameScreen.gameMap.tileMap.weather);
                    else game.gameScreen.gameMap.setWeather(0);
                }
                if (!inGame) game.save.save();
            }
        });
        showFps.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!game.player.settings.muteSfx) rm.buttonclick2.play(game.player.settings.sfxVolume);
                game.player.settings.showFps = showFps.isChecked();
                game.fps.setVisible(showFps.isChecked());
                if (!inGame) game.save.save();
            }
        });
        fullscreen.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Unlucky.DISABLE_FULLSCREEN) return;
                if (!shouldCheckFullscreen) return;
                if (!game.player.settings.muteSfx) rm.buttonclick2.play(game.player.settings.sfxVolume);
                fullscreenClicked = true;
                game.player.settings.fullscreen = fullscreen.isChecked();
                if (game.player.settings.fullscreen) game.fullscreen();
                else game.windowedMode();
                if (!inGame) game.save.save();
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (Unlucky.DISABLE_FULLSCREEN) return;
        if (fullscreenClicked) {
            fullscreenClicked = false;
            shouldCheckFullscreen = true;
        }
        else {
            if (!game.player.settings.fullscreen && fullscreen.isChecked()) {
                shouldCheckFullscreen = false;
                fullscreen.setChecked(false);
                game.setSystemCursor();
                shouldCheckFullscreen = true;
            }
            if (game.player.settings.fullscreen && !fullscreen.isChecked()) {
                shouldCheckFullscreen = false;
                fullscreen.setChecked(true);
                game.setCustomCursor();
                shouldCheckFullscreen = true;
            }
        }
    }

    public void show() {
        game.fps.setPosition(2, 2);
        stage.addActor(game.fps);

        // fade in transition if in game
        if (inGame) {
            // Gdx.input.setInputProcessor(stage);
            renderBatch = false;
            batchFade = true;

            stage.addAction(Actions.sequence(Actions.moveTo(0, 0), Actions.alpha(0),
                Actions.run(new Runnable() {
                @Override
                public void run() {
                    renderBatch = true;
                }
            }), Actions.fadeIn(0.5f)));
        }
        // slide in transition if in menu
        else {
            super.showSlide(true);
            stage.addAction(Actions.alpha(1));
        }

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (!clickable) return super.keyDown(keycode);
                if (keycode == Input.Keys.ESCAPE) {
                    if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                    if (inGame) {
                        game.gameScreen.resetGame = false;
                        setFadeScreen(game.gameScreen);
                        game.gameScreen.hud.settingsDialog.show(game.gameScreen.hud.getStage());
                    }
                    else {
                        game.menuScreen.transitionIn = 2;
                        setSlideScreen(game.menuScreen, false);
                    }
                    return true;
                }
                return super.keyDown(keycode);
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float dt) {
        update(dt);

        if (!inGame) {
            for (int i = 0; i < game.menuBackground.length; i++) {
                game.menuBackground[i].update(dt);
            }
        }

        // clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (renderBatch) {
            stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
            stage.getBatch().begin();
            // fix fading
            if (batchFade) stage.getBatch().setColor(Color.WHITE);

            // if in game the background is the background of the current world
            if (inGame) {
                stage.getBatch().draw(rm.worldSelectBackgrounds[worldIndex], 0, 0);
            }
            else {
                for (int i = 0; i < game.menuBackground.length; i++) {
                    game.menuBackground[i].render((SpriteBatch) stage.getBatch());
                }
            }
            stage.getBatch().end();
        }

        stage.act(dt);
        stage.draw();
    }

}
