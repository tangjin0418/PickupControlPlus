package org.tjdev.custom.pickupcontrolplus.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tjdev.util.tjpluginutil.command.tab.SubCommand;
import org.tjdev.util.tjpluginutil.object.NewThis;
import org.tjdev.util.tjpluginutil.spigot.command.CustomCommand;
import org.tjdev.util.tjpluginutil.spigot.command.SpigotCommand;
import org.tjdev.util.tjpluginutil.spigot.inventory.custom.GUILoader;
import org.tjdev.util.tjpluginutil.spigot.inventory.inventorygui.DynamicGuiElement;
import org.tjdev.util.tjpluginutil.spigot.inventory.inventorygui.GuiElementGroup;
import org.tjdev.util.tjpluginutil.spigot.inventory.inventorygui.StaticGuiElement;
import org.tjdev.util.tjpluginutil.spigot.locale.LangDownload;
import org.tjdev.util.tjpluginutil.spigot.object.handler.InputHandler;

import java.util.List;

import static org.tjdev.custom.pickupcontrolplus.PickupControlPlus.MATERIAL_ITEMS;
import static org.tjdev.custom.pickupcontrolplus.config.Config.CONFIG;
import static org.tjdev.custom.pickupcontrolplus.handler.DataHandler.DATA_PLAYER;
import static org.tjdev.util.tjpluginutil.ICommandUtil.onlyPlayer;

public class Pickup implements NewThis {
    public Pickup() {

        new CustomCommand("pickup") {{
            addDefault((commandSender, strings) -> {
                if (onlyPlayer(commandSender)) return;
                var p = (Player) commandSender;
                var dp = DATA_PLAYER.get(p);
                if (dp == null) return;

                final boolean[] showDisabled = {false};
                final String[] search = {null};
                var gui = GUILoader.GUIS.get("default").create(p);
                gui.addElement(new DynamicGuiElement('.', () -> {
                    var group = new GuiElementGroup('.');

                    for (Material m : showDisabled[0] ? dp.disabled : MATERIAL_ITEMS) {
                        var name = LangDownload.getDisplayName(new ItemStack(m));
                        if (search[0] != null &&
                            !name.toLowerCase().contains(search[0].toLowerCase()) &&
                            !m.name().toLowerCase().contains(search[0].toLowerCase())) continue;

                        var disabled = dp.disabled.contains(m);
                        group.addElement(new StaticGuiElement('.', CONFIG.item
                                .replace("name", name)
                                .replace("disabled", String.valueOf(disabled))
                                .apply(m), s -> {
                            p.performCommand("pickupcontrolplus:pickup " +
                                             m.name().toLowerCase() +
                                             " " + disabled);
                            gui.draw();
                            return true;
                        }));
                    }

                    return group;
                }));
                gui.function("toggle", r -> {
                    showDisabled[0] = !showDisabled[0];
                    gui.draw();
                    return true;
                });
                gui.function("search", r -> {
                    gui.close();

                    CONFIG.enterSearch.execute(p);
                    new InputHandler(p) {
                        @Override
                        public boolean whenInput(String s) {
                            search[0] = s;
                            gui.show(p);
                            return true;
                        }
                    }.cancelInput("cancel").whenCancel(() -> {
                        CONFIG.enterCancelled.execute(p);
                        gui.show(p);
                        return true;
                    });
                    return true;
                });
                gui.replacer(r -> r.replace("show-disabled", String.valueOf(showDisabled[0])));
                gui.open();
            }, new SubCommand<>(MATERIAL_ITEMS.stream().map(r -> r.name().toLowerCase()).toList()));

            addTab(List.of("(ANY)"), "true", "false");
            addArgCommand((commandSender, strings) -> {
                var enabled = strings[1].equals("true");

                Material m;
                try {
                    m = Material.valueOf(strings[0].toUpperCase());
                } catch (IllegalArgumentException ignored) {
                    return;
                }
                if (!MATERIAL_ITEMS.contains(m)) return;

                if (onlyPlayer(commandSender)) return;
                var p = (Player) commandSender;

                var dp = DATA_PLAYER.get(p);
                if (dp == null) return;

                if (enabled) dp.disabled.remove(m);
                else dp.disabled.add(m);

                CONFIG.set.replace("disabled", String.valueOf(!enabled))
                          .replace("item", LangDownload.getDisplayName(new ItemStack(m)))
                          .execute(p);
            }, "(ANY)", "(BOOL)");

            init(new SpigotCommand(getName(), "Pickup manage.").command);
        }};
    }
}
