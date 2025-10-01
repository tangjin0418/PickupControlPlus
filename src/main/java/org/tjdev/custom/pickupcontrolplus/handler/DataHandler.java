package org.tjdev.custom.pickupcontrolplus.handler;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.tjdev.custom.pickupcontrolplus.obj.DataPlayer;
import org.tjdev.util.tjpluginutil.object.EnableDisable;
import org.tjdev.util.tjpluginutil.spigot.object.IncludedListener;
import org.tjdev.util.tjpluginutil.spigot.object.task.LaterAsync;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.tjdev.custom.pickupcontrolplus.config.Config.db;
import static org.tjdev.util.tjpluginutil.database.Syntax.WRAP;
import static org.tjdev.util.tjpluginutil.database.Syntax.sql;
import static org.tjdev.util.tjpluginutil.spigot.object.task.Scope.Async;

public class DataHandler implements EnableDisable, IncludedListener {
    public static final Map<Player, DataPlayer> DATA_PLAYER = new ConcurrentHashMap<>();

    private static void load(Player p) {
        try (var st = db.connection().createStatement();
             var rs =
                     st.executeQuery(sql("? disabled_items@PickupControlPlus_players W? uuid = " + WRAP(p.getUniqueId())))) {
            var dp = new DataPlayer(p.getUniqueId());
            DATA_PLAYER.put(p, dp);
            if (!rs.isClosed() && rs.next()) {
                var str = rs.getString(1);
                if (!str.isEmpty())
                    dp.disabled.addAll(Arrays.stream(str.split(";")).map(Material::valueOf).toList());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        new LaterAsync(20, () -> {
            if (e.getPlayer().isOnline()) load(e.getPlayer());
        });
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        var dp = DATA_PLAYER.remove(e.getPlayer());
        if (dp != null) Async(dp::save);
    }

    @Override
    public void enable() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) load(onlinePlayer);
    }

    @Override
    public void disable() {
        for (DataPlayer value : DATA_PLAYER.values()) value.save();
    }
}
