package com.anyicomplex.unlucky.lwjgl3;

import com.anyicomplex.unlucky.Unlucky;
import com.anyicomplex.unlucky.util.PlatformSupport;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.SharedLibraryLoader;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Lwjgl3PlatformSupport extends PlatformSupport {

    private final Unlucky game;

    public Lwjgl3PlatformSupport(Unlucky game) {
        this.game = game;
    }

    @Override
    public void openURIFromString(String uri) {
        if (uri == null) throw new NullPointerException("Uri cannot be null.");
        try {
            new URI(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid uri.");
        }
        String baseCommand;
        if (SharedLibraryLoader.isWindows) baseCommand = "start";
        else if (SharedLibraryLoader.isLinux) baseCommand = "xdg-open";
        else if (SharedLibraryLoader.isMac) baseCommand = "open";
        else {
            Gdx.net.openURI(uri);
            return;
        }
        try {
            Runtime.getRuntime().exec(baseCommand + " " + uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
