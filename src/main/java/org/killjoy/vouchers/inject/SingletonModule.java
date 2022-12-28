package org.killjoy.vouchers.inject;

import com.google.inject.AbstractModule;
import org.killjoy.vouchers.language.Lang;
import org.killjoy.vouchers.voucher.VoucherHandler;
import org.killjoy.vouchers.voucher.VoucherRegistry;

public class SingletonModule extends AbstractModule {
    @Override
    protected void configure() {
        this.bind(VoucherRegistry.class).asEagerSingleton();
        this.bind(VoucherHandler.class).asEagerSingleton();

        this.bind(Lang.class).asEagerSingleton();
    }
}
