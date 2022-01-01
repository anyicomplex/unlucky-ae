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
import com.anyicomplex.unlucky.effects.Moving;
import com.anyicomplex.unlucky.resource.ResourceManager;
import com.anyicomplex.unlucky.util.PlatformSupport;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
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
 * The main menu screen of the game that holds all access points for playing,
 * managing the player's inventory, bank, shop, etc, and the settings
 *
 * @author Ming Li
 */
public class MenuScreen extends MenuExtensionScreen {

    // whether to fade or slide in after a transition from another screen
    // 0 - fade in
    // 1 - slide in right
    // 2 - slide in left
    public int transitionIn = 0;

    // title animation (each letter moves down at descending speeds)
    private Moving[] titleMoves;
    private Image[] letters;

    // label style
    private Label.LabelStyle menuStyle;
    private Label battleLabel;

    // play button
    private ImageButton playButton;
    // other buttons
    private ImageButton[] optionButtons;

    private static final int NUM_BUTTONS = 6;

    // Credits Screen box
    private Image dark;
    private Group credits;
    private Image frame;
    private Label copyright;
    private Label github;
    private Label youtube;
    private Label copyright2;
    private Label github2;
    private Image[] creditsIcons;
    private ImageButton exitButton;

    public MenuScreen(final Unlucky game, final ResourceManager rm) {
        super(game, rm);

        menuStyle = new Label.LabelStyle(rm.pixel10, new Color(79 / 255.f, 79 / 255.f, 117 / 255.f, 1));

        // one for each letter
        titleMoves = new Moving[7];
        letters = new Image[7];
        for (int i = 0; i < 7; i++) {
            titleMoves[i] = new Moving(new Vector2(), new Vector2(), 0);
            letters[i] = new Image(rm.title[i]);
            stage.addActor(letters[i]);
        }

        handlePlayButton();
        handleOptionButtons();

        battleLabel = new Label("Start", menuStyle);
        battleLabel.setSize(80, 40);
        battleLabel.setFontScale(1.5f);
        battleLabel.setTouchable(Touchable.disabled);
        battleLabel.setAlignment(Align.center);
        battleLabel.setPosition(60, 35);

        stage.addActor(battleLabel);

        createCreditsScreen();

        // menu music
        rm.menuTheme.setLooping(true);
        rm.menuTheme.play();
    }

    @Override
    public void show() {
        game.fps.setPosition(5, 115);
        stage.addActor(game.fps);

        if (!rm.menuTheme.isPlaying()) rm.menuTheme.play();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (!clickable) return super.keyDown(keycode);
                switch (keycode) {
                    case Input.Keys.E:
                        if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                        setSlideScreen(game.inventoryScreen, false);
                        return true;
                    case Input.Keys.S:
                        if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                        game.settingsScreen.inGame = false;
                        setSlideScreen(game.settingsScreen, true);
                        return true;
                    case Input.Keys.H:
                        if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                        setSlideScreen(game.shopScreen, false);
                        return true;
                    case Input.Keys.T:
                        if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                        setSlideScreen(game.statisticsScreen, true);
                        return true;
                    case Input.Keys.M:
                        if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                        setSlideScreen(game.smoveScreen, false);
                        return true;
                    case Input.Keys.I:
                        if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                        credits.setVisible(true);
                        return true;
                    case Input.Keys.ESCAPE:
                    if (credits.isVisible()) {
                        if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                        credits.setVisible(false);
                    }
                    else Gdx.app.exit();
                    return true;
                    case Input.Keys.ENTER:
                    case Input.Keys.NUMPAD_ENTER:
                        if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                        setFadeScreen(game.worldSelectScreen);
                        return true;
                }
                return super.keyDown(keycode);
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
        renderBatch = false;
        batchFade = true;
        resetTitleAnimation();

        if (transitionIn == 0) {
            // fade in animation
            stage.addAction(Actions.sequence(Actions.alpha(0), Actions.run(new Runnable() {
                @Override
                public void run() {
                    renderBatch = true;
                }
            }), Actions.fadeIn(0.5f)));
        } else {
            renderBatch = true;
            // slide in animation
            stage.addAction(Actions.sequence(Actions.moveTo(
                transitionIn == 1 ? Unlucky.V_WIDTH : -Unlucky.V_WIDTH, 0), Actions.moveTo(0, 0, 0.3f)));
        }
    }

    private void handlePlayButton() {
        ImageButton.ImageButtonStyle s = new ImageButton.ImageButtonStyle();
        s.imageUp = new TextureRegionDrawable(rm.playButton[0][0]);
        s.imageDown = new TextureRegionDrawable(rm.playButton[1][0]);
        playButton = new ImageButton(s);
        playButton.setPosition(60, 35);
        stage.addActor(playButton);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                setFadeScreen(game.worldSelectScreen);
            }
        });
    }

    private void handleOptionButtons() {
        ImageButton.ImageButtonStyle[] styles = rm.loadImageButtonStyles(NUM_BUTTONS, rm.menuButtons);
        optionButtons = new ImageButton[NUM_BUTTONS];
        for (int i = 0; i < NUM_BUTTONS; i++) {
            optionButtons[i] = new ImageButton(styles[i]);
            optionButtons[i].setSize(20, 20);
            optionButtons[i].getImage().setFillParent(true);
            stage.addActor(optionButtons[i]);
        }
        // inventory button
        optionButtons[0].setPosition(6, 85);
        // settings button
        optionButtons[1].setPosition(171, 85);
        // shop button
        optionButtons[2].setPosition(6, 50);
        // smove button
        optionButtons[3].setPosition(6, 15);
        // statistics button
        optionButtons[4].setPosition(170, 50);
        // credits button
        optionButtons[5].setPosition(170, 15);

        // inventory screen
        optionButtons[0].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                setSlideScreen(game.inventoryScreen, false);
            }
        });
        // settings screen
        optionButtons[1].addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                game.settingsScreen.inGame = false;
                setSlideScreen(game.settingsScreen, true);
            }
        });
        // shop screen
        optionButtons[2].addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                setSlideScreen(game.shopScreen, false);
            }
        });
        // smove screen
        optionButtons[3].addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                setSlideScreen(game.smoveScreen, false);
            }
        });
        // statistics screen
        optionButtons[4].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                setSlideScreen(game.statisticsScreen, true);
            }
        });
        // credits screen
        optionButtons[5].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                credits.setVisible(true);
            }
        });
    }

    private void createCreditsScreen() {
        credits = new Group();
        credits.setTransform(false);
        credits.setVisible(false);
        credits.setSize(Unlucky.V_WIDTH, Unlucky.V_HEIGHT);

        // darken the menu screen to focus on the credits
        dark = new Image(rm.shade);
        credits.addActor(dark);

        frame = new Image(rm.skin, "textfield");
        frame.setSize(100, 100);
        frame.setPosition(Unlucky.V_WIDTH / 2.0f - 50, Unlucky.V_HEIGHT / 2.0f - 50);
        credits.addActor(frame);

        ImageButton.ImageButtonStyle exitStyle = new ImageButton.ImageButtonStyle();
        exitStyle.imageUp = new TextureRegionDrawable(rm.exitbutton18x18[0][0]);
        exitStyle.imageDown = new TextureRegionDrawable(rm.exitbutton18x18[1][0]);
        exitButton = new ImageButton(exitStyle);
        exitButton.setSize(14, 14);
        exitButton.setPosition(50 + 92, 50 + 52);
        credits.addActor(exitButton);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                credits.setVisible(false);
            }
        });

        copyright = new Label("Unlucky V1.0" + "\nCopyright (c) 2018 Ming Li",
            new Label.LabelStyle(rm.pixel10, Color.WHITE));
        copyright.setFontScale(0.75f);
        copyright.setPosition(53, 86);
        copyright.setTouchable(Touchable.disabled);
        credits.addActor(copyright);

        github = new Label("GITHUB", new Label.LabelStyle(rm.pixel10, new Color(140 / 255.f, 60 / 255.f, 1, 1)));
        github.setPosition(80, 86 - 14);
        credits.addActor(github);
        github.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlatformSupport.openURI(Unlucky.GITHUB);
            }
        });

        youtube = new Label("YOUTUBE", new Label.LabelStyle(rm.pixel10, Color.RED));
        youtube.setPosition(80, 86 - 14 - 18);
        credits.addActor(youtube);
        youtube.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlatformSupport.openURI(Unlucky.YOUTUBE);
            }
        });

        copyright2 = new Label(Unlucky.APP_NAME + " V" + Unlucky.VERSION + "\nCopyright (c) 2021 Yi An",
                new Label.LabelStyle(rm.pixel10, Color.WHITE));
        copyright2.setFontScale(0.75f);
        copyright2.setPosition(53, 86 - 14 - 18 - 22);
        copyright2.setTouchable(Touchable.disabled);
        credits.addActor(copyright2);

        github2 = new Label("GITHUB", new Label.LabelStyle(rm.pixel10, new Color(140 / 255.f, 60 / 255.f, 1, 1)));
        github2.setPosition(80, 86 - 14 - 18 - 22 - 14);
        credits.addActor(github2);
        github2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlatformSupport.openURI(Unlucky.GITHUB2);
            }
        });

        creditsIcons = new Image[3];
        for (int i = 0; i < 3; i ++) {
            final int index = i;
            if (i == 2) {
                creditsIcons[i] = new Image(rm.creditsicons[1]);
                creditsIcons[i].setPosition(56, 14);
            }
            else {
                creditsIcons[i] = new Image(rm.creditsicons[i]);
                creditsIcons[i].setPosition(56, 16 + 34 + i * 18);
            }
            creditsIcons[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (index == 1) PlatformSupport.openURI(Unlucky.GITHUB);
                    if (index == 2) PlatformSupport.openURI(Unlucky.GITHUB2);
                    else PlatformSupport.openURI(Unlucky.YOUTUBE);
                }
            });
            credits.addActor(creditsIcons[i]);
        }

        stage.addActor(credits);
    }

    public void update(float dt) {
        for (int i = 0; i < titleMoves.length; i ++) {
            titleMoves[i].update(dt);
            letters[i].setPosition(titleMoves[i].position.x, titleMoves[i].position.y);
        }
    }
    /**
     * Resets and starts the title animation on every transition to this screen
     */
    private void resetTitleAnimation() {
        // entire title text starts at x = 74
        for (int i = 0; i < titleMoves.length; i++) {
            titleMoves[i].origin.set(new Vector2(37 + i * 18, 120 + 24));
            titleMoves[i].target.set(new Vector2(37 + i * 18, 120 - 35));
            titleMoves[i].speed = (275 - i * 24) / 2.0f;
            titleMoves[i].horizontal = false;
            titleMoves[i].start();
        }
    }

}
