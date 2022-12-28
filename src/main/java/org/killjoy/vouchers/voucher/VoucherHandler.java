package org.killjoy.vouchers.voucher;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.killjoy.vouchers.voucher.configurate.VoucherSerializer;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;

public final class VoucherHandler {

    private final GsonConfigurationLoader loader;
    private final VoucherRegistry registry;

    private final Logger logger;

    @Inject
    public VoucherHandler(final @NonNull JavaPlugin plugin,
                          final @NonNull @Named("dataFolder") Path dataFolder,
                          final @NonNull VoucherRegistry registry,
                          final @NonNull Logger logger) {
        Path vouchersFilePath = dataFolder.resolve("vouchers.json");
        if (Files.notExists(vouchersFilePath)) {
            plugin.saveResource("vouchers.json", false);
        }

        this.loader = build(vouchersFilePath);
        this.registry = registry;
        this.logger = logger;

        try {
            this.reload();
        } catch (ConfigurateException e) {
            throw new RuntimeException("Caught exception trying to load vouchers", e);
        }

        logger.info(format("Loaded %s voucher(s) from file.", registry.size()));
    }

    public void reload() throws ConfigurateException {
        var root = loader.load();

        for (var entry : root.childrenMap().entrySet()) {
            var node = entry.getValue();
            var voucher = node.get(Voucher.class);

            if (voucher != null) {
                registry.register(voucher);
            }
        }
    }

    public void save(Voucher voucher) {
        try {
            var root = loader.load();
            var node = root.node(voucher.key());
            node.set(voucher);
            loader.save(root);
        } catch (ConfigurateException e) {
            logger.error(String.format("Caught exception trying to save voucher %s", voucher.key()), e);
        }
    }

    private @NonNull GsonConfigurationLoader build(final Path source) {
        return GsonConfigurationLoader.builder()
                .path(source)
                .defaultOptions(opts -> opts.serializers(builder -> builder.register(Voucher.class, VoucherSerializer.INSTANCE)))
                .build();
    }
}
