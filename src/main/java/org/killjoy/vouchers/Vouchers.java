package org.killjoy.vouchers;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.killjoy.vouchers.command.MainCommand;
import org.killjoy.vouchers.inject.CommandModule;
import org.killjoy.vouchers.inject.MenuModule;
import org.killjoy.vouchers.inject.PluginModule;
import org.killjoy.vouchers.inject.SingletonModule;
import org.killjoy.vouchers.listener.ChatListener;
import org.killjoy.vouchers.listener.InteractListener;

public final class Vouchers extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        final @NonNull Injector injector = Guice.createInjector(
                new PluginModule(this),
                new SingletonModule(),
                new MenuModule(),
                new CommandModule()
        );

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(injector.getInstance(InteractListener.class), this);
        pm.registerEvents(injector.getInstance(ChatListener.class), this);

        MainCommand mainCommand = injector.getInstance(MainCommand.class);
        mainCommand.register();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
