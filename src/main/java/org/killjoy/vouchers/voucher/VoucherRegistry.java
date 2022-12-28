package org.killjoy.vouchers.voucher;

import com.google.common.collect.Maps;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.killjoy.vouchers.Constants;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public final class VoucherRegistry {

    private final Map<String, Voucher> vouchers = Maps.newHashMap();

    public synchronized void register(final @NonNull Voucher voucher) {
        this.vouchers.put(voucher.key(), voucher);
    }

    public synchronized void clean(final @NonNull Voucher voucher) {
        this.vouchers.remove(voucher.key());
    }

    public Optional<Voucher> get(final @NonNull String key) {
        return Optional.ofNullable(this.vouchers.get(key));
    }

    public Optional<Voucher> get(final @NonNull ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasKey(Constants.VOUCHER_ITEM_KEY)) {
            return get(nbtItem.getString(Constants.VOUCHER_ITEM_KEY));
        }
        return Optional.empty();
    }

    public int size() {
        return this.vouchers.size();
    }

    public Collection<Voucher> all() {
        return this.vouchers.values();
    }
}
