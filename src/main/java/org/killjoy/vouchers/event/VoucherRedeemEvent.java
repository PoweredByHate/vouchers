package org.killjoy.vouchers.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.killjoy.vouchers.voucher.Voucher;

public class VoucherRedeemEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Voucher voucher;

    private boolean cancelled;

    public VoucherRedeemEvent(@NonNull Player player, @NonNull Voucher voucher) {
        this.player = player;
        this.voucher = voucher;
        this.cancelled = false;
    }

    public @NonNull Player getPlayer() {
        return this.player;
    }

    public @NonNull Voucher getVoucher() {
        return this.voucher;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NonNull HandlerList getHandlerList() {
        return handlers;
    }
}
