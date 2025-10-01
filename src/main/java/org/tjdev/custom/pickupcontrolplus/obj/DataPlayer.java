package org.tjdev.custom.pickupcontrolplus.obj;

import org.bukkit.Material;
import org.tjdev.util.tjpluginutil.text.StringUtil;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.tjdev.custom.pickupcontrolplus.config.Config.db;
import static org.tjdev.util.tjpluginutil.database.Syntax.WRAP;

public class DataPlayer {
    public final UUID uuid;
    public final Set<Material> disabled = new HashSet<>();

    public DataPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public void save() {
        try (var st = db.connection().createStatement()) {
            st.executeUpdate(db.UPSERT(
                    "PickupControlPlus_players",
                    List.of("uuid", "disabled_items"),
                    List.of(WRAP(uuid), WRAP(StringUtil.join(";", disabled)))
            ));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
