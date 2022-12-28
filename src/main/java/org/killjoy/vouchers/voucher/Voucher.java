package org.killjoy.vouchers.voucher;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.killjoy.vouchers.Constants;
import org.killjoy.vouchers.util.MessageParser;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class Voucher {

    private final String key;

    private String name;
    private List<String> lore = new ArrayList<>();
    private State state;

    private String material;

    private List<String> commands = new ArrayList<>();

    public Voucher(final @NonNull String key) {
        this.key = key;
    }

    public @NonNull ItemStack toItemStack() {
        Material type = requireNonNull(Material.getMaterial(material), "Type is not valid Material");
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();

        if (name != null) {
            meta.displayName(MessageParser.parse(name));
        }

        if (lore != null) {
            meta.lore(MessageParser.parse(lore));
        }

        item.setItemMeta(meta);
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString(Constants.VOUCHER_ITEM_KEY, this.key);
        return nbtItem.getItem();
    }

    public void claim(final @NonNull Player player) {
        for (String command : commands) {
            command = command.replace("%player", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public @NonNull String key() {
        return this.key;
    }

    public @NonNull String name() {
        return this.name;
    }

    public void name(final @NonNull String name) {
        this.name = name;
    }

    public @Nullable State state() {
        return this.state;
    }

    public void state(final @Nullable State state) {
        this.state = state;
    }

    public @Nullable String material() {
        return this.material;
    }

    public void material(final @Nullable String material) {
        this.material = material;
    }

    public void lore(final @NonNull String c) {
        this.lore.add(c);
    }

    public void lore(final @NonNull List<String> c) {
        this.lore = c;
    }

    public void command(final @NonNull String command) {
        this.commands.add(command);
    }

    public void commands(final @NonNull List<String> commands) {
        this.commands = commands;
    }

    public @Nullable List<String> commands() {
        return commands;
    }

    public enum State {
        RENAME,
        ITEM_SELECTOR
    }
}
