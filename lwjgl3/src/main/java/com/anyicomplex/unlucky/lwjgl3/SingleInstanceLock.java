/*
 * Copyright (c) 2012-2015 Oleg Dolya
 *
 * Copyright (c) 2014 Edu Garcia
 *
 * Copyright (c) 2021 Yi An
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.anyicomplex.unlucky.lwjgl3;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

/**
 * Simple utility class that prevents user from running more than one instances of the same application. File-based.
 */
public class SingleInstanceLock {

    /**
     * Exit if other current application's instances running, depends on lock file with appId.
     *
     * @param appId application id, can be any words, cannot be null.
     * @param exitCode exit code, will be call by {@link System#exit(int)}
     */
    public static synchronized void exitIfOtherInstancesRunning (String appId, int exitCode) {
        if (appId == null) throw new NullPointerException("AppId cannot be null.");
        String tmpDir = System.getProperty("java.io.tmpdir");
        String userName = System.getProperty("user.name");
        final File file = new File(tmpDir, appId + "." + userName + ".lock");
        try {
            final RandomAccessFile lockFile = new RandomAccessFile(file, "rw");
            final FileLock fileLock = lockFile.getChannel().tryLock();
            if (fileLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            fileLock.release();
                            lockFile.close();
                            boolean success = file.delete();
                            if (!success) System.err.println("Unable to remove lock: \n" + "Failed to delete lock.");
                        }
                        catch (IOException e) {
                            System.err.println("Unable to remove lock: \n" + e.getMessage());
                        }
                    }
                });
                return;
            }
        }
        catch (IOException e) {
            System.err.println("Unable to create lock: \n" + e.getMessage());
        }
        System.exit(exitCode);
    }

    /**
     * Exit with code 0 if other current application's instances running, depends on lock file with appId.
     * Also see {@link SingleInstanceLock#exitIfOtherInstancesRunning(String, int)}
     *
     * @param appId application id, can be any words, cannot be null.
     */
    public static void exitIfOtherInstancesRunning (String appId) {
        exitIfOtherInstancesRunning(appId, 0);
    }

}
