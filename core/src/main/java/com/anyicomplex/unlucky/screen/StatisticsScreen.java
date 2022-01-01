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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

/**
 * A screen with a scroll pane displaying all game statistics
 *
 * @author Ming Li
 */
public class StatisticsScreen extends MenuExtensionScreen {

    // screen banner
    protected Label bannerLabel;
    protected Image banner;

    // lines of statistics
    private String[] statsDescs;
    private String[] statsNums;

    private Label.LabelStyle[] headerStyles;
    private Label.LabelStyle statStyle;
    private Image scrollBackground;

    // scroll pane
    private Table scrollTable;
    private Table selectionContainer;
    private ScrollPane scrollPane;

    public StatisticsScreen(final Unlucky game, final ResourceManager rm) {
        super(game, rm);

        // create title label
        banner = new Image(rm.skin, "default-slider");
        banner.setPosition(8, 102);
        banner.setSize(164, 12);
        stage.addActor(banner);

        bannerLabel = new Label("STATISTICS", rm.skin);
        bannerLabel.setStyle(new Label.LabelStyle(rm.pixel10, new Color(1, 212 / 255.f, 0, 1)));
        bannerLabel.setSize(50, 12);
        bannerLabel.setTouchable(Touchable.disabled);
        bannerLabel.setPosition(14, 102);
        bannerLabel.setAlignment(Align.left);
        stage.addActor(bannerLabel);

        ImageButton.ImageButtonStyle s = new ImageButton.ImageButtonStyle();
        s.imageUp = new TextureRegionDrawable(rm.playButton[0][0]);
        s.imageDown = new TextureRegionDrawable(rm.playButton[1][0]);

        // handle exit button
        stage.addActor(exitButton);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.menuScreen.transitionIn = 2;
                if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                setSlideScreen(game.menuScreen, false);
            }
        });

        scrollBackground = new Image(rm.skin, "default-slider");
        scrollBackground.setPosition(8, 8);
        scrollBackground.setSize(184, 88);
        stage.addActor(scrollBackground);

        headerStyles = new Label.LabelStyle[] {
            new Label.LabelStyle(rm.pixel10, new Color(150 / 255.f, 1, 1, 1)),
            new Label.LabelStyle(rm.pixel10, new Color(0, 195 / 255.f, 0, 1)),
            new Label.LabelStyle(rm.pixel10, new Color(230 / 255.f, 30 / 255.f, 0, 1))
        };
        statStyle = new Label.LabelStyle(rm.pixel10, Color.WHITE);

        scrollTable = new Table();
    }

    @Override
    public void show() {
        game.fps.setPosition(2, 2);
        stage.addActor(game.fps);

        super.showSlide(true);
        // update statistics every screen show
        statsDescs = game.player.stats.getDescList();
        statsNums = game.player.stats.getStatsList();
        scrollTable.remove();
        createScrollPane();
        stage.setScrollFocus(scrollPane);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (!clickable) return super.keyDown(keycode);
                if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
                    game.menuScreen.transitionIn = 2;
                    if (!game.player.settings.muteSfx) rm.buttonclick0.play(game.player.settings.sfxVolume);
                    setSlideScreen(game.menuScreen, false);
                    Gdx.input.setInputProcessor(stage);
                    return true;
                }
                return super.keyDown(keycode);
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void createScrollPane() {
        scrollTable = new Table();
        scrollTable.setFillParent(true);
        stage.addActor(scrollTable);
        selectionContainer = new Table();

        for (int i = 0; i < statsDescs.length; i++) {
            Label statDesc;
            if (statsDescs[i].startsWith("player")) {
                statDesc = new Label(statsDescs[i], headerStyles[0]);
                statDesc.setFontScale(0.8f);
            } else if (statsDescs[i].startsWith("map")) {
                statDesc = new Label(statsDescs[i], headerStyles[1]);
                statDesc.setFontScale(0.8f);
            } else if (statsDescs[i].startsWith("battle")) {
                statDesc = new Label(statsDescs[i], headerStyles[2]);
                statDesc.setFontScale(0.8f);
            } else {
                statDesc = new Label(statsDescs[i], statStyle);
                statDesc.setFontScale(0.5f);
            }
            Label statNum = new Label(statsNums[i], statStyle);
            statNum.setFontScale(0.5f);

            if (statsDescs[i].startsWith("player") || statsDescs[i].startsWith("map") || statsDescs[i].startsWith("battle")) {
                selectionContainer.add(statDesc).padBottom(6).align(Align.left).row();
            }
            else {
                selectionContainer.add(statDesc).padBottom(6).align(Align.left);
                selectionContainer.add(statNum).padBottom(6).padRight(-60).align(Align.right);
                selectionContainer.row();
            }
        }

        selectionContainer.pack();
        selectionContainer.setTransform(true);
        selectionContainer.setOrigin(selectionContainer.getWidth() / 2,
            selectionContainer.getHeight() / 2);

        scrollPane = new ScrollPane(selectionContainer, rm.skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.layout();
        scrollTable.add(scrollPane).size(254, 80).fill();
        scrollTable.setPosition(-34, -8);
    }

}
