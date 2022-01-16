package com.anyicomplex.unlucky.lwjgl3;

import com.anyicomplex.desktop.util.OpenLinkInBrowser;
import com.anyicomplex.unlucky.Unlucky;
import com.anyicomplex.unlucky.util.PlatformSupport;
import com.badlogic.gdx.Gdx;

public class Lwjgl3PlatformSupport extends PlatformSupport {

    private final Unlucky game;

    public Lwjgl3PlatformSupport(Unlucky game) {
        this.game = game;
    }

    @Override
    public void openURIFromString(String uri) {
        OpenLinkInBrowser.fromString(uri);
    }

    @Override
    public void fullscreenMode() {
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        game.setCustomCursor();
    }

    @Override
    public void windowedMode() {
        Gdx.graphics.setWindowedMode(game.player.settings.width, game.player.settings.height);
        game.setSystemCursor();
    }

    @Override
    public boolean isFullscreenMode() {
        return Gdx.graphics.isFullscreen();
    }

}
