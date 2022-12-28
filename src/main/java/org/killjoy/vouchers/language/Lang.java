package org.killjoy.vouchers.language;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static java.lang.String.format;
import static org.killjoy.vouchers.util.MessageParser.parse;

public class Lang {

    private final @Nullable ConfigurationNode root;

    @Inject
    public Lang(final @Named("dataFolder") Path dataFolder, final @NonNull JavaPlugin plugin) {
        Path languageFilePath = dataFolder.resolve("lang.yml");
        if (Files.notExists(languageFilePath)) {
            plugin.saveResource("lang.yml", false);
        }

        final YamlConfigurationLoader loader = build(languageFilePath);
        try {
            root = loader.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException("Caught error trying to load language", e);
        }
    }

    public @NonNull Component c(final @NonNull String key) {
        return c(key, Map.of());
    }

    public @NonNull Component c(final @NonNull String key, final @NonNull Map<String, Object> placeholders) {
        @Nullable String raw;

        if (root != null) {
            raw = root.node(key).getString();
        } else {
            raw = "<red>Configuration object is null.";
        }

        if (raw == null) {
            raw = format("<red>Missing value for key '%s'", key);
        }

        for (var entry : placeholders.entrySet()) {
            raw = raw.replaceAll("%" + entry.getKey(), String.valueOf(entry.getValue()));
        }

        return parse(raw);
    }

    private @NonNull YamlConfigurationLoader build(final Path source) {
        return YamlConfigurationLoader.builder().path(source).build();
    }
}
