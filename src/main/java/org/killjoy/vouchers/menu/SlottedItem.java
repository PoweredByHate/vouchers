package org.killjoy.vouchers.menu;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.killjoy.vouchers.util.ItemParser;

public class SlottedItem {
    private final int slot;
    private final ItemStack item;

    public SlottedItem(ConfigurationSection section) {
        this.slot = section.getInt("slot");
        this.item = ItemParser.parse(section);
    }

    public SlottedItem(int slot, ItemStack item) {
        this.slot = slot;
        this.item = item;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        return item;
    }

    public int addTo(Inventory inventory) {
        inventory.setItem(this.slot, this.item);
        return this.slot;
    }
}
