package com.anyicomplex.unlucky.lwjgl3;

import com.anyicomplex.unlucky.Unlucky;
import com.anyicomplex.unlucky.save.PlayerAccessor;
import com.anyicomplex.unlucky.save.Settings;
import com.anyicomplex.unlucky.util.PlatformSupport;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import java.io.File;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
	public static void main(String[] args) {
		createApplication();
	}

	private static Lwjgl3Application createApplication() {

		SingleInstanceLock.exitIfOtherInstancesRunning(Lwjgl3Launcher.class.getCanonicalName());

		Unlucky.APP_NAME = Lwjgl3Launcher.class.getPackage().getSpecificationTitle();
		Unlucky.VERSION = Lwjgl3Launcher.class.getPackage().getSpecificationVersion();
		Unlucky.VERSION_CODE_STRING = Lwjgl3Launcher.class.getPackage().getImplementationVersion();
		Unlucky.VERSION_CODE = Integer.parseInt(Unlucky.VERSION_CODE_STRING);
		if (Unlucky.VERSION_CODE < 10) Unlucky.VERSION_CODE_STRING = "00" + Unlucky.VERSION_CODE;
		else if (Unlucky.VERSION_CODE < 100) Unlucky.VERSION_CODE_STRING = "0" + Unlucky.VERSION_CODE;
		Unlucky.TITLE = Unlucky.APP_NAME + " v" + Unlucky.VERSION + " [Build " + Unlucky.VERSION_CODE_STRING + "]";

		String storageBasePath;
		String storageRelativePath = "";
		if (SharedLibraryLoader.isWindows) {
			File appDataDir = new File(System.getenv("APPDATA"));
			storageBasePath = appDataDir.getParent();
			storageRelativePath = appDataDir.getName();
		}
		else {
			storageBasePath = System.getProperty("user.home");
		}
		if (!storageBasePath.endsWith("/")) storageBasePath += "/";
		Unlucky.STORAGE_BASE_PATH = storageBasePath;
		storageRelativePath += "/Anyicomplex/Games";
		if (SharedLibraryLoader.isLinux) {
			storageRelativePath = ".local/share/anyicomplex/games";
		}
		if (SharedLibraryLoader.isMac) {
			storageRelativePath = "Library/Application Support/Anyicomplex/Games";
		}
		storageRelativePath += "/" + Unlucky.APP_NAME + "/";
		Unlucky.STORAGE_RELATIVE_PATH = storageRelativePath;
		Unlucky.STORAGE_ABSOLUTE_PATH = storageBasePath + storageRelativePath;

		Unlucky.DISABLE_PAD = true;
		Unlucky.DISABLE_FULLSCREEN = false;
		Unlucky.DISABLE_CURSOR = false;

		Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();

		configuration.setWindowSizeLimits(Unlucky.V_WIDTH * Unlucky.V_MIN_SCALE,
				Unlucky.V_HEIGHT * Unlucky.V_MIN_SCALE, -1, -1);
		configuration.setTitle(Unlucky.TITLE);
		configuration.useVsync(true);
		//// Limits FPS to the refresh rate of the currently active monitor.
		configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
		//// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
		//// useful for testing performance, but can also be very stressful to some hardware.
		//// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
		configuration.setWindowIcon(Files.FileType.Internal, "icons/icon128.png", "icons/icon32.png",
				"icons/icon64.png", "icons/icon16.png");

		Unlucky unlucky = new Unlucky();
		PlatformSupport.setHandler(new Lwjgl3PlatformSupport(unlucky));

		Settings settings;
		try {
			PlayerAccessor psave = new Json().fromJson(PlayerAccessor.class, Base64Coder.decodeString(
					new Lwjgl3FileHandle(Unlucky.STORAGE_ABSOLUTE_PATH + "save", Files.FileType.Absolute).readString()));
			settings = psave.settings;
		}
		catch (Exception ignored) {
			settings = new Settings();
		}

		unlucky.preLoadSettings = settings;

		configuration.setMaximized(settings.maximized);
		configuration.setWindowedMode(settings.width, settings.height);
		settings.lastWidth = settings.width;
		settings.lastHeight = settings.height;

		Settings finalSettings = settings;
		configuration.setWindowListener(new Lwjgl3WindowListener() {
			@Override
			public void created(Lwjgl3Window window) {
				long windowHandle = window.getWindowHandle();
				if (finalSettings.posX != Integer.MIN_VALUE && finalSettings.posY != Integer.MIN_VALUE) {
					GLFW.glfwSetWindowPos(windowHandle, finalSettings.posX, finalSettings.posY);
					finalSettings.lastPosX = finalSettings.posX;
					finalSettings.lastPosY = finalSettings.posY;
				}
				GLFW.glfwSetWindowSizeCallback(windowHandle, new GLFWWindowSizeCallback() {
					@Override
					public void invoke(long window, int width, int height) {
						if (!finalSettings.fullscreen) {
							finalSettings.lastWidth = finalSettings.width;
							finalSettings.lastHeight = finalSettings.height;
							if (!finalSettings.maximized) {
								finalSettings.width = width;
								finalSettings.height = height;
							}
						}
					}
				});
				GLFW.glfwSetWindowPosCallback(windowHandle, new GLFWWindowPosCallback() {
					@Override
					public void invoke(long window, int xpos, int ypos) {
						if (!finalSettings.fullscreen) {
							finalSettings.lastPosX = finalSettings.posX;
							finalSettings.lastPosY = finalSettings.posY;
							if (!finalSettings.maximized) {
								finalSettings.posX = xpos;
								finalSettings.posY = ypos;
							}
						}
					}
				});
			}
			@Override
			public void iconified(boolean isIconified) {}
			@Override
			public void maximized(boolean isMaximized) {
				finalSettings.maximized = isMaximized;
				if (isMaximized) {
					finalSettings.posX = finalSettings.lastPosX;
					finalSettings.posY = finalSettings.lastPosY;
					finalSettings.width = finalSettings.lastWidth;
					finalSettings.height = finalSettings.lastHeight;
				}
			}
			@Override
			public void focusLost() {}
			@Override
			public void focusGained() {}
			@Override
			public boolean closeRequested() {
				return true;
			}
			@Override
			public void filesDropped(String[] files) {}
			@Override
			public void refreshRequested() {}
		});
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (unlucky.player.inMap) {
					unlucky.gameScreen.hud.loseObtained();
					unlucky.player.setHp(unlucky.player.getMaxHp());
				}
				unlucky.save.save();
			}
		});
		return new Lwjgl3Application(unlucky, configuration);
	}

}