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

package com.anyicomplex.unlucky;

import com.anyicomplex.unlucky.entity.Player;
import com.anyicomplex.unlucky.parallax.Background;
import com.anyicomplex.unlucky.resource.ResourceManager;
import com.anyicomplex.unlucky.save.Save;
import com.anyicomplex.unlucky.save.Settings;
import com.anyicomplex.unlucky.screen.*;
import com.anyicomplex.unlucky.screen.game.VictoryScreen;
import com.anyicomplex.unlucky.ui.inventory.InventoryUI;
import com.anyicomplex.unlucky.util.Disposer;
import com.anyicomplex.unlucky.util.PlatformSupport;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * "Unlucky" is a RPG/Dungeon Crawler based on RNG
 * The player will go through various levels with numerous enemies
 * and attempt to complete each level by reaching the end tile.
 *
 * @author Ming Li
 */
public class Unlucky extends Game {

    public volatile static String APP_NAME;
    public volatile static String VERSION;
    public volatile static int VERSION_CODE;
    public volatile static String VERSION_CODE_STRING;
    public volatile static String TITLE;

    public volatile static String STORAGE_BASE_PATH;
    public volatile static String STORAGE_RELATIVE_PATH;
    public volatile static String STORAGE_ABSOLUTE_PATH;

    // Links
    public static final String GITHUB = "https://github.com/mingli1/Unlucky";
    public static final String GITHUB2 = "https://github.com/anyicomplex/unlucky-ae";
    public static final String YOUTUBE = "https://www.youtube.com/channel/UC-oA-vkeYrgEy23Sq2PLC8w/videos?shelf_id=0&sort=dd&view=0";

    // Desktop screen dimensions
    public static final int V_WIDTH = 200;
    public static final int V_HEIGHT = 120;
    public static final int V_MIN_SCALE = 3;
    public static final int V_SCALE = 6;

    public volatile static boolean DISABLE_PAD = false;
    public volatile static boolean DISABLE_FULLSCREEN = true;
    public volatile static boolean DISABLE_CURSOR = true;

    // Rendering utilities
    public SpriteBatch batch;

    // Resources
    public ResourceManager rm;

    // Universal player
    public Player player;

    // Game save
    public Save save;
    public Settings preLoadSettings;

    // Screens
    public MenuScreen menuScreen;
    public GameScreen gameScreen;
    public WorldSelectScreen worldSelectScreen;
    public LevelSelectScreen levelSelectScreen;
    public InventoryScreen inventoryScreen;
    public ShopScreen shopScreen;
    public SpecialMoveScreen smoveScreen;
    public StatisticsScreen statisticsScreen;
    public InventoryUI inventoryUI;
    public VictoryScreen victoryScreen;
    public SettingsScreen settingsScreen;

    // main bg
    public Background[] menuBackground;

    // debugging
    public Label fps;

	public void create() {

        batch = new SpriteBatch();
        rm = new ResourceManager();
        player = new Player("player", rm);

        save = new Save(player, "save");
        save.load(rm);
        if (preLoadSettings != null) player.settings = preLoadSettings;

        if (Gdx.app.getType() == Application.ApplicationType.WebGL) player.settings.fullscreen = false;
        if (player.settings.fullscreen) {
            fullscreen();
        }

        // debugging
        fps = new Label("", new Label.LabelStyle(rm.pixel10, Color.RED));
        fps.setFontScale(0.5f);
        fps.setVisible(player.settings.showFps);

        inventoryUI = new InventoryUI(this, player, rm);
        menuScreen = new MenuScreen(this, rm);
        gameScreen = new GameScreen(this, rm);
        worldSelectScreen = new WorldSelectScreen(this, rm);
        levelSelectScreen = new LevelSelectScreen(this, rm);
        inventoryScreen = new InventoryScreen(this, rm);
        shopScreen = new ShopScreen(this, rm);
        smoveScreen = new SpecialMoveScreen(this, rm);
        statisticsScreen = new StatisticsScreen(this, rm);
        victoryScreen = new VictoryScreen(this, rm);
        settingsScreen = new SettingsScreen(this, rm);

        // create parallax background
        menuBackground = new Background[3];

        // ordered by depth
        // sky
        menuBackground[0] = new Background(rm.titleScreenBackground[0],
            (OrthographicCamera) menuScreen.getStage().getCamera(), new Vector2(0, 0));
        menuBackground[0].setVector(0, 0);
        // back clouds
        menuBackground[1] = new Background(rm.titleScreenBackground[2],
            (OrthographicCamera) menuScreen.getStage().getCamera(), new Vector2(0.3f, 0));
        menuBackground[1].setVector(20, 0);
        // front clouds
        menuBackground[2] = new Background(rm.titleScreenBackground[1],
            (OrthographicCamera) menuScreen.getStage().getCamera(), new Vector2(0.3f, 0));
        menuBackground[2].setVector(60, 0);

        if (Gdx.app.getType() == Application.ApplicationType.WebGL) setScreen(new GwtScreen(this, rm));
        else setScreen(menuScreen);
	}

    public void setCustomCursor() {
        if (!DISABLE_CURSOR) {
            Pixmap cursorImage = new Pixmap(Gdx.files.internal("ui/pointer.png"));
            Gdx.graphics.setCursor(Gdx.graphics.newCursor(cursorImage,
                    cursorImage.getWidth() / 2, cursorImage.getHeight() / 2));
            Disposer.dispose(cursorImage);
        }
    }

    public void setSystemCursor() {
        if (!DISABLE_CURSOR) {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
    }

    public void fullscreen() {
        PlatformSupport.fullscreen();
    }

    public void windowed() {
        PlatformSupport.windowed();
    }

	public void render() {
        fps.setText(Gdx.graphics.getFramesPerSecond() + " fps");
        super.render();
    }

	public void dispose() {
        Disposer.dispose(batch);
        super.dispose();

        Disposer.dispose(menuScreen, gameScreen, worldSelectScreen,
                levelSelectScreen, inventoryScreen, shopScreen, statisticsScreen,
                inventoryUI, victoryScreen, settingsScreen, rm);

	}

    public boolean isFullscreen() {
        return PlatformSupport.isFullscreen();
    }

    @Override
    public void resize(int width, int height) {
        if (!Unlucky.DISABLE_FULLSCREEN) player.settings.fullscreen = isFullscreen();
        super.resize(width, height);
        if (getScreen() != settingsScreen) settingsScreen.resize(width, height);
    }

}
