package org.killjoy.vouchers.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.killjoy.vouchers.Vouchers;
import org.killjoy.vouchers.language.Lang;
import org.killjoy.vouchers.util.MessageParser;
import org.killjoy.vouchers.voucher.Voucher;
import org.killjoy.vouchers.voucher.VoucherHandler;

import java.util.HashMap;
import java.util.Map;

import static org.killjoy.vouchers.util.MessageParser.from;

@Singleton
public final class ChatListener implements Listener {

    private final Vouchers plugin;
    private final VoucherHandler handler;
    private final Lang lang;

    private final Map<Player, Voucher> map = new HashMap<>();

    @Inject
    public ChatListener(final @NonNull Vouchers plugin,
                        final @NonNull VoucherHandler handler,
                        final @NonNull Lang lang) {
        this.plugin = plugin;
        this.handler = handler;
        this.lang = lang;
    }

    @EventHandler
    public void onChat(final @NonNull AsyncChatEvent event) {
        Player player = event.getPlayer();
        Voucher voucher = this.map.remove(player);
        if (voucher == null) {
            return;
        }

        event.setCancelled(true);
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            if (voucher.state() != null) {
                if (voucher.state() == Voucher.State.RENAME) {
                    Component name = event.message();
                    voucher.name(MessageParser.from(name));
                    player.sendMessage(lang.c("rename-success", Map.of("name", from(name))));
                } else if (voucher.state() == Voucher.State.ITEM_SELECTOR) {
                    String message = from(event.message());
                    if (!message.equalsIgnoreCase("confirm")) {
                        return;
                    }

                    ItemStack item = player.getInventory().getItemInMainHand();
                    Material type = item.getType();
                    if (type == Material.AIR) {
                        player.sendMessage(lang.c("must-be-holding-item"));
                        return;
                    }
                    voucher.material(type.toString());
                    player.sendMessage(lang.c("item-success", Map.of("material", type.toString())));
                }
                voucher.state(null);
                handler.save(voucher);
            }
        });
    }

    public Map<Player, Voucher> getMap() {
        return map;
    }
}
