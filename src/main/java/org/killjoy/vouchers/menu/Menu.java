package org.killjoy.vouchers.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Menu implements Listener {
    protected final Plugin plugin;

    protected final Map<Integer, Consumer<InventoryClickEvent>> clickListeners = new HashMap<>();
    protected Inventory inventory;

    protected Menu(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(final @NonNull InventoryClickEvent event) {
        if (this.inventory == null) {
            return;
        }

        InventoryView view = event.getView();
        Inventory top = view.getTopInventory();
        if (!top.equals(this.inventory)) {
            return;
        }

        boolean isTop = event.getClickedInventory() == top;
        event.setCancelled(event.isShiftClick() || isTop);

        if (isTop) {
            Consumer<InventoryClickEvent> listener = this.clickListeners.get(event.getSlot());
            if (listener != null) {
                listener.accept(event);
            }
        }
    }

    @EventHandler
    public void onClose(final @NonNull InventoryCloseEvent event) {
        InventoryClickEvent.getHandlerList().unregister(this);
        event.getHandlers().unregister(this);
    }

    public void showTo(final @NonNull Player player) {
        this.inventory = Bukkit.createInventory(null,
                this.getSize(),
                this.getTitle());
        this.draw(player);

        Bukkit.getPluginManager().registerEvents(this, this.plugin);
        player.openInventory(inventory);
    }

    public void draw(final @NonNull Player player) {
        this.inventory.clear();
        this.clickListeners.clear();

        this.doDraw(player);
    }

    protected abstract String getTitle();

    protected abstract int getSize();

    protected abstract void doDraw(final @NonNull Player player);
}
