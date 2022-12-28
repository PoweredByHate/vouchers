package org.killjoy.vouchers.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class ItemParser {
    private ItemParser() {
    }

    public static @NonNull ItemStack parse(@NonNull ConfigurationSection section) {
        String typeString = requireNonNull(section.getString("type", "Type is required"));
        Material type = requireNonNull(Material.getMaterial(typeString), "Type is not valid Material");
        ItemStack item = new ItemStack(type);

        ItemMeta meta = requireNonNull(item.getItemMeta());
        String name = section.getString("name");
        if (name != null) {
            meta.displayName(MessageParser.parse(name));
        }

        var lore = section.getStringList("lore");
        if (!lore.isEmpty()) {
            List<Component> parsed = new ArrayList<>(lore.size());
            for (var line : lore) {
                parsed.add(MessageParser.parse(line));
            }
            meta.lore(parsed);
        }

        item.setItemMeta(meta);
        return item;
    }
}
