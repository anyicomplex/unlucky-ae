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
import com.anyicomplex.unlucky.util.Disposer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Screen template for game states.
 *
 * @author Ming Li
 */
public abstract class AbstractScreen implements Screen, Disposable {

    protected final Unlucky game;
    protected final ResourceManager rm;

    // camera that focuses on the player
    protected OrthographicCamera cam;
    // viewport that keeps aspect ratios of the game when resizing
    protected Viewport viewport;
    // main stage of each screen
    protected Stage stage;

    // to delay the batch rendering until after transition finishes
    protected volatile boolean renderBatch = false;
    // to toggle color fading for batch draw calls
    protected volatile boolean batchFade = true;
    // to remove previous clicks buffered before switching the screen
    protected volatile boolean clickable = true;

    public AbstractScreen(final Unlucky game, final ResourceManager rm) {
        this.game = game;
        this.rm = rm;

        cam = new OrthographicCamera(Unlucky.V_WIDTH, Unlucky.V_HEIGHT);
        cam.setToOrtho(false);
        // the game will retain it's scaled dimensions regardless of resizing
        viewport = new FitViewport(Unlucky.V_WIDTH, Unlucky.V_HEIGHT, cam);

        stage = new Stage(viewport, game.batch);
    }

    @Override
    public void render(float dt) {
        stage.act(dt);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        Disposer.dispose(stage);
    }

    public Stage getStage() {
        return stage;
    }

    public OrthographicCamera getCamera() {
        return cam;
    }

    public SpriteBatch getBatch() {
        return game.batch;
    }

    public Unlucky getGame() { return game; }

    /**
     * Switches to a new screen while handling fading buffer
     * Fade transition
     *
     * @param screen
     */
    public void setFadeScreen(final Screen screen) {
        if (clickable) {
            clickable = false;
            batchFade = false;
            // fade out animation
            stage.addAction(Actions.sequence(Actions.fadeOut(0.3f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        clickable = true;
                        game.setScreen(screen);
                    }
                })));
        }
    }

    /**
     * Switches to a new screen while handling fading buffer
     * Slide transition either to the left or right
     *
     * @param screen
     * @param right
     */
    public void setSlideScreen(final Screen screen, boolean right) {
        if (clickable) {
            clickable = false;
            batchFade = true;
            // slide animation
            stage.addAction(Actions.sequence(
                Actions.moveBy(right ? -Unlucky.V_WIDTH : Unlucky.V_WIDTH, 0, 0.15f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        clickable = true;
                        game.setScreen(screen);
                    }
                })));
        }
    }

    public boolean isRenderBatch() {
        return renderBatch;
    }

    public void setRenderBatch(boolean renderBatch) {
        this.renderBatch = renderBatch;
    }

    public boolean isBatchFade() {
        return batchFade;
    }

    public void setBatchFade(boolean batchFade) {
        this.batchFade = batchFade;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

}
