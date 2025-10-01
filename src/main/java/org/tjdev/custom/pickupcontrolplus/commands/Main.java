package org.tjdev.custom.pickupcontrolplus.commands;

import org.tjdev.util.tjpluginutil.object.NewThis;
import org.tjdev.util.tjpluginutil.spigot.command.CustomCommand;
import org.tjdev.util.tjpluginutil.spigot.command.SpigotCommand;

import static org.tjdev.util.tjpluginutil.spigot.object.TJPlugin.plugin;

public class Main implements NewThis {
    public Main() {
        new CustomCommand(plugin.getName().toLowerCase()) {{
            addDefault("reload");
            addReload();

            init(new SpigotCommand(getName(), "Main command.").command);
        }};
    }
}
