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
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

/**
 * Allows the player to select the world to battle in
 * Displays options for worlds with a scroll pane and renders an background
 * according to the world
 * For each world, displays the description and level range of the world
 *
 * @author Ming Li
 */
public class WorldSelectScreen extends SelectScreen {

    public WorldSelectScreen(final Unlucky game, final ResourceManager rm) {
        super(game, rm);

        handleExitButton();
        handleEnterButton();
        createScrollPane();
    }

    @Override
    public void show() {
        super.show();

        bannerLabel.setText("SELECT A WORLD");
        bannerLabel.getStyle().fontColor = new Color(1, 212 / 255.f, 0, 1);

        this.worldIndex = game.player.maxWorld;

        // automatically scroll to the position of the currently selected world button
        float r = (float) worldIndex / (rm.worlds.size - 1);
        scrollPane.setScrollPercentY(r);
        stage.setScrollFocus(scrollPane);

        selectAt(worldIndex);
        fullDescLabel.setText(rm.worlds.get(worldIndex).longDesc);

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
                    setFadeScreen(game.menuScreen);
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
        if (clickable) {
            clickable = false;
            batchFade = false;
            // fade out animation
            stage.addAction(Actions.sequence(Actions.fadeOut(0.3f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            clickable = true;
                            game.levelSelectScreen.setWorld(worldIndex);
                            game.setScreen(game.levelSelectScreen);
                        }
                    })));
        }
    }

    protected void handleExitButton() {
        super.handleExitButton(game.menuScreen);
    }

    protected void handleEnterButton() {
        enterButtonGroup.setPosition(114, 4);
        stage.addActor(enterButtonGroup);
        enterLabel.setText("SELECT");
        enterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                performEnter();
            }
        });
    }

    protected void createScrollPane() {
        scrollButtons = new Array<>();

        descStyle = new Label.LabelStyle(rm.pixel10, Color.WHITE);
        buttonSelected = new TextButton.TextButtonStyle();
        buttonSelected.up = new TextureRegionDrawable(rm.skin.getRegion("default-round-down"));

        scrollTable = new Table();
        scrollTable.setFillParent(true);
        stage.addActor(scrollTable);

        selectionContainer = new Table();
        for (int i = 0; i < rm.worlds.size; i++) {
            final int index = i;

            // button and label group
            Group g = new Group();
            g.setSize(90, 30);
            g.setTransform(false);

            Label name = new Label(rm.worlds.get(i).name, nameStyles[i]);
            name.setPosition(5, 20);
            name.setFontScale(1.7f / 2);
            name.setTouchable(Touchable.disabled);
            Label desc = new Label(rm.worlds.get(i).shortDesc, descStyle);
            desc.setPosition(5, 6);
            desc.setFontScale(0.5f);
            desc.setTouchable(Touchable.disabled);

            final TextButton b = new TextButton("", rm.skin);
            b.getStyle().checked = b.getStyle().down;
            b.getStyle().over = null;
            if (i == 0) b.setChecked(true);

            b.setTouchable(Touchable.enabled);
            scrollButtons.add(b);

            name.setText(rm.worlds.get(i).name);
            desc.setText(rm.worlds.get(i).shortDesc);

            // select world
            b.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!game.player.settings.muteSfx) rm.invselectclick.play(game.player.settings.sfxVolume);
                    worldIndex = index;
                    selectAt(worldIndex);
                    fullDescLabel.setText(rm.worlds.get(worldIndex).longDesc);
                }
            });
            b.setFillParent(true);

            g.addActor(b);
            g.addActor(name);
            g.addActor(desc);

            selectionContainer.add(g).padBottom(4).size(90, 30).row();
        }
        selectionContainer.pack();
        selectionContainer.setTransform(false);
        selectionContainer.setOrigin(selectionContainer.getWidth() / 2,
            selectionContainer.getHeight() / 2);

        scrollPane = new ScrollPane(selectionContainer, rm.skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.layout();
        scrollTable.add(scrollPane).size(112, 101).fill();
        scrollTable.setPosition(-38, -10);
    }

    public void render(float dt) {
        super.render(dt, worldIndex);
    }

}
