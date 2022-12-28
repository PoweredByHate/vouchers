package org.killjoy.vouchers.inject;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.killjoy.vouchers.menu.MenuFactory;

public class MenuModule extends AbstractModule {
    @Override
    protected void configure() {
        this.install(new FactoryModuleBuilder().build(MenuFactory.class));
    }
}
