package org.killjoy.vouchers.inject;

import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Function;

public class CommandModule extends AbstractModule {
    @Provides
    @Singleton
    public final CommandManager<CommandSender> provideCommandManager(final @NonNull JavaPlugin plugin) {
        try {
            final @NonNull Function<CommandSender, CommandSender> mapper = Function.identity();
            final @NonNull PaperCommandManager<CommandSender> manager = new PaperCommandManager<>(
                    plugin,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    mapper,
                    mapper
            );

            return manager;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize command manager", e);
        }
    }
}
