package org.killjoy.vouchers.menu;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.killjoy.vouchers.menu.impl.EditMenu;
import org.killjoy.vouchers.voucher.Voucher;

public interface MenuFactory {

    EditMenu newEditMenu(final @NonNull Voucher voucher);
}
