package org.killjoy.vouchers.menu.impl;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.killjoy.vouchers.Vouchers;
import org.killjoy.vouchers.language.Lang;
import org.killjoy.vouchers.listener.ChatListener;
import org.killjoy.vouchers.menu.Menu;
import org.killjoy.vouchers.menu.SlottedItem;
import org.killjoy.vouchers.voucher.Voucher;

import static java.util.Objects.requireNonNull;

public class EditMenu extends Menu {

    private final Vouchers plugin;
    private final Voucher voucher;
    private final Lang lang;
    private final ChatListener listener;

    private final String title;
    private final int size;

    private final SlottedItem rename;
    private final SlottedItem item;
    private final SlottedItem commands;

    @Inject
    protected EditMenu(final @NonNull Vouchers plugin,
                       final @NonNull @Assisted Voucher voucher,
                       final @NonNull Lang lang,
                       final @NonNull ChatListener listener) {
        super(plugin);
        this.plugin = plugin;
        this.voucher = voucher;
        this.lang = lang;
        this.listener = listener;

        FileConfiguration cfg = plugin.getConfig();
        ConfigurationSection menu = requireNonNull(cfg.getConfigurationSection("edit-menu"), "edit menu is null");

        this.title = menu.getString("title");
        this.size = menu.getInt("size");

        this.rename = new SlottedItem(requireNonNull(menu.getConfigurationSection("rename"), "rename is null"));
        this.item = new SlottedItem(requireNonNull(menu.getConfigurationSection("item-selector"), "item selector is null"));
        this.commands = new SlottedItem(requireNonNull(menu.getConfigurationSection("command-editor"), "command editor is null"));
    }

    @Override
    protected String getTitle() {
        return this.title;
    }

    @Override
    protected int getSize() {
        return this.size;
    }

    @Override
    protected void doDraw(@NonNull Player player) {
        int renameSlot = this.rename.addTo(super.inventory);
        this.clickListeners.put(renameSlot, ev -> {
            voucher.state(Voucher.State.RENAME);
            listener.getMap().put(player, voucher);
            player.sendMessage(lang.c("rename-enter-new-name"));
            this.delayTask(player::closeInventory);
        });

        int itemSelectorSlot = this.item.addTo(super.inventory);
        this.clickListeners.put(itemSelectorSlot, ev -> {
            voucher.state(Voucher.State.ITEM_SELECTOR);
            listener.getMap().put(player, voucher);
            player.sendMessage(lang.c("item-hold-new-item"));
            this.delayTask(player::closeInventory);
        });

        int commandEditorSlot = this.commands.addTo(super.inventory);
        this.clickListeners.put(commandEditorSlot, ev -> {

        });
    }

    private void delayTask(Runnable task) {
        Bukkit.getScheduler().runTaskLater(this.plugin, task, 1);
    }
}
