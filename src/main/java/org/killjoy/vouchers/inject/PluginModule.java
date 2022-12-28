package org.killjoy.vouchers.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.killjoy.vouchers.Vouchers;
import org.slf4j.Logger;

import java.nio.file.Path;

public class PluginModule extends AbstractModule {

    private final Vouchers plugin;

    public PluginModule(final @NonNull Vouchers plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        this.bind(Vouchers.class).toInstance(this.plugin);
        this.bind(JavaPlugin.class).toInstance(this.plugin);
    }

    @Provides
    @Named("dataFolder")
    public @NonNull Path provideDataFolder() {
        return this.plugin.getDataFolder().toPath();
    }

    @Provides
    public @NonNull Logger provideSLF4JLogger() {
        return this.plugin.getSLF4JLogger();
    }
}
