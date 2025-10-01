package org.tjdev.custom.pickupcontrolplus;

import org.bukkit.Material;
import org.tjdev.util.tjpluginutil.database.SimpleDatabase;
import org.tjdev.util.tjpluginutil.object.config.SimpleDatabaseImpl;
import org.tjdev.util.tjpluginutil.spigot.PluginUtil;
import org.tjdev.util.tjpluginutil.spigot.inventory.custom.GUILoader;
import org.tjdev.util.tjpluginutil.spigot.object.TJPlugin;
import org.tjdev.util.tjpluginutil.spigot.object.config.IDisplay;
import org.tjdev.util.tjpluginutil.spigot.object.config.TJAction;

import java.util.List;

import static org.tjdev.util.tjpluginutil.object.TJPluginExtension.requester;

public final class PickupControlPlus extends TJPlugin {
    public static List<Material> MATERIAL_ITEMS;

    @Override
    public void enable() {
        requester = "Ikorvath";
        requestType = PluginUtil.PLUGIN_TYPE.FREE;

        IDisplay.init();
        TJAction.init();
        SimpleDatabase.init();
        SimpleDatabaseImpl.init();
        GUILoader.load();
    }
}
