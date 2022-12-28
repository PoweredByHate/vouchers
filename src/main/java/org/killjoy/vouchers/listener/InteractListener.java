package org.killjoy.vouchers.listener;

import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.killjoy.vouchers.event.VoucherRedeemEvent;
import org.killjoy.vouchers.voucher.Voucher;
import org.killjoy.vouchers.voucher.VoucherRegistry;

public class InteractListener implements Listener {

    @Inject
    private VoucherRegistry registry;

    @EventHandler(ignoreCancelled = true)
    public void onInteract(final @NonNull PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        Action action = event.getAction();
        if (!action.name().contains("RIGHT")) {
            return;
        }

        if (registry.get(item).isPresent()) {
            Player player = event.getPlayer();
            Voucher voucher = registry.get(item).get();

            VoucherRedeemEvent e = new VoucherRedeemEvent(player, voucher);
            Bukkit.getPluginManager().callEvent(e);

            if (e.isCancelled()) {
                return;
            }

            event.setCancelled(true);

            EquipmentSlot hand = event.getHand();
            int newAmount = item.getAmount() - 1;
            if (newAmount == 0) {
                setHand(player, hand, null);
            } else {
                item.setAmount(newAmount);
                setHand(player, hand, item);
            }

            voucher.claim(player);
        }
    }

    private void setHand(Player player, EquipmentSlot slot, ItemStack item) {
        PlayerInventory inv = player.getInventory();
        if (slot == EquipmentSlot.HAND) {
            inv.setItemInMainHand(item);
        } else if (slot == EquipmentSlot.OFF_HAND) {
            inv.setItemInOffHand(item);
        } else {
            throw new IllegalArgumentException(String.format("'%s' is not a valid hand slot", slot));
        }
    }
}
