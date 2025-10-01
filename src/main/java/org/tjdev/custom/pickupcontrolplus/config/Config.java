package org.tjdev.custom.pickupcontrolplus.config;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.tjdev.custom.pickupcontrolplus.PickupControlPlus;
import org.tjdev.util.tjpluginutil.config.mapper.InlineComment;
import org.tjdev.util.tjpluginutil.config.mapper.MappedConfig;
import org.tjdev.util.tjpluginutil.database.SimpleDatabase;
import org.tjdev.util.tjpluginutil.object.NewThis;
import org.tjdev.util.tjpluginutil.object.annotation.Priority;
import org.tjdev.util.tjpluginutil.object.config.SimpleDatabaseImpl;
import org.tjdev.util.tjpluginutil.spigot.locale.LangDownload;
import org.tjdev.util.tjpluginutil.spigot.object.config.IDisplay;
import org.tjdev.util.tjpluginutil.spigot.object.config.TJAction;

import java.util.Arrays;
import java.util.List;

import static org.tjdev.util.tjpluginutil.database.Syntax.sql;
import static org.tjdev.util.tjpluginutil.spigot.object.action.ActionFactory.messageSound;

@Priority(priority = 0)
public class Config extends MappedConfig implements NewThis {
    public static Config CONFIG;
    public static SimpleDatabase db;

    public Config() {
        load();

        CONFIG = this;
        db = database.create();
        db.validate(s -> s.executeUpdate(sql("+TBL ?? !E? PickupControlPlus_players(" +
                                             "uuid CHAR(36) PRIMARY KEY," +
                                             "disabled_items LONGTEXT" +
                                             ")")));

        LangDownload.init(lang);

        PickupControlPlus.MATERIAL_ITEMS = Arrays.stream(Material.values())
                                                 .filter(Material::isItem)
                                                 .filter(r -> r != Material.AIR)
                                                 .filter(r -> !disabledItems.contains(r))
                                                 .toList();
    }

    public String lang = "en_us";
    public SimpleDatabaseImpl database = SimpleDatabaseImpl.DEFAULT_SQLITE;
    public TJAction set = messageSound(
            "&aSet pickup status of &e%item% &ato {%disabled% ? '&cCan\\\\'t pickup' : '&aPickupable'}&a.",
            Sound.BLOCK_NOTE_BLOCK_PLING);
    public TJAction enterSearch = messageSound("&6Please enter the item name, &c'cancel' to cancel&6:",
                                               Sound.BLOCK_NOTE_BLOCK_PLING);
    public TJAction enterCancelled = messageSound("&cCancelled.", Sound.ENTITY_VILLAGER_NO);
    public IDisplay item = new IDisplay(
            "&f%name%",
            "{%disabled% ? '&cCan\\\\'t pickup' : '&aPickupable'}",
            "",
            "&7Click to toggle!"
    );
    @InlineComment("When enabled, player can pickup disabled item with right click the item.")
    public boolean forcePickupMode = false;

    @InlineComment("Remove item from gui / command.")
    public List<Material> disabledItems = List.of(Material.COMMAND_BLOCK);
}
