/*
 * Copyright (C) 2017 MCME
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mcmiddleearth.mywarp;

import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.platform.Platform;
import io.github.mywarp.mywarp.warp.Warp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eriol_Eandur
 */
public class MCMEWarpUtil {
    
    private static List<String> randomWarpWorlds = new ArrayList<>();
    
    private static Platform platform;
    
    static boolean init = false;
    
    public static void init(Platform platf) {
        platform = platf;
        File dataFolder = platform.getDataFolder();
        if(dataFolder.exists()) {
            File config = new File(dataFolder, "randomWarpConfig.txt");
            if(!config.exists()) {
                try {
                    config.createNewFile();
                    if(config.exists() && config.canWrite()) {
                        try(PrintWriter fw = new PrintWriter(new FileWriter(config))) {
                            fw.println("world");
                            randomWarpWorlds.add("world");
                            fw.println("moria");
                            randomWarpWorlds.add("moria");
                            fw.close();
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MCMEWarpUtil.class.getName()).log(Level.WARNING, null, ex);
                }
                
            } else if(config.canRead()) {
                try(Scanner scanner = new Scanner(config)) {
                    while(scanner.hasNext()) {
                        randomWarpWorlds.add(scanner.nextLine());
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MCMEWarpUtil.class.getName()).log(Level.WARNING, null, ex);
                }
            }
        }
        init = true;
        Logger.getGlobal().info("MCMEWarpUtil initialized!");
    }
    
    public static Warp getRandomWarp(Collection<Warp> warps) {
        if(!init) {
            return null;
        }
        List<Warp> valid = new ArrayList<>();
        for(Warp warp: warps) {
            if(Character.isUpperCase(warp.getName().charAt(0))
                    && isRandomWarpWorld(warp)) {
                valid.add(warp);
            }
        }
        if(valid.size()>0) {
            Collections.shuffle(valid);
            return valid.get(0);
        } else {
            return null;
        }
    }
    
    private static boolean isRandomWarpWorld(Warp warp) {
        for(String worldName: randomWarpWorlds) {
            Optional<LocalWorld> localeWorld = platform.getGame().getWorld(worldName);
            if(localeWorld.isPresent() && warp.getWorldIdentifier().equals(localeWorld.get().getUniqueId())) {
                return true;
            }
        }
        return false;
    }
}
