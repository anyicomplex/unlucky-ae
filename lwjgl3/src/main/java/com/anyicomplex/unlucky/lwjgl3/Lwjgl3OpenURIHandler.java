package com.anyicomplex.unlucky.lwjgl3;

import com.anyicomplex.unlucky.util.OpenURIHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.SharedLibraryLoader;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Lwjgl3OpenURIHandler implements OpenURIHandler {

    @Override
    public void from(String uri) {
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

}
