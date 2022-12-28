package org.killjoy.vouchers.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.meta.CommandMeta;
import com.google.inject.Inject;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.killjoy.vouchers.language.Lang;
import org.killjoy.vouchers.menu.MenuFactory;
import org.killjoy.vouchers.menu.impl.EditMenu;
import org.killjoy.vouchers.voucher.Voucher;
import org.killjoy.vouchers.voucher.VoucherHandler;
import org.killjoy.vouchers.voucher.VoucherRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MainCommand {

    private final @NonNull CommandManager<CommandSender> manager;
    private final @NonNull Lang lang;
    private final @NonNull VoucherRegistry registry;
    private final @NonNull VoucherHandler handler;
    private final @NonNull MenuFactory menuFactory;

    @Inject
    public MainCommand(final @NonNull CommandManager<CommandSender> manager,
                       final @NonNull Lang lang,
                       final @NonNull VoucherRegistry registry,
                       final @NonNull VoucherHandler handler,
                       final @NonNull MenuFactory menuFactory) {
        this.manager = manager;
        this.lang = lang;
        this.registry = registry;
        this.handler = handler;
        this.menuFactory = menuFactory;
    }

    public void register() {
        final Command.Builder<CommandSender> root = this.manager.commandBuilder("vouchers")
                .meta(CommandMeta.DESCRIPTION, "Base plugin command")
                .handler(this::root);

        final @NonNull CommandArgument<CommandSender, String> keyArg = StringArgument.<CommandSender>builder("key")
                .withSuggestionsProvider((ctx, arg) -> {
                    final @NonNull List<String> list = new ArrayList<>(registry.size());

                    for (final @NonNull Voucher voucher : registry.all()) {
                        list.add(voucher.key());
                    }

                    return list;
                })
                .build();

        final Command.Builder<CommandSender> create = root.literal("create")
                .meta(CommandMeta.DESCRIPTION, "Create a voucher")
                .argument(keyArg)
                .handler(this::create);
        final Command.Builder<CommandSender> edit = root.literal("edit")
                .meta(CommandMeta.DESCRIPTION, "Edit a voucher")
                .argument(keyArg.copy())
                .handler(this::edit);

        // vouchers give <target> <key> <amount>
        final @NonNull CommandArgument<CommandSender, Player> playerArg = PlayerArgument.of("player");
        final @NonNull CommandArgument<CommandSender, Integer> amountArg = IntegerArgument.<CommandSender>builder("amount")
                .asOptional()
                .build();
        final Command.Builder<CommandSender> give = root.literal("give")
                .meta(CommandMeta.DESCRIPTION, "Give a voucher")
                .argument(playerArg)
                .argument(keyArg.copy())
                .argument(amountArg)
                .handler(this::give);

        manager.command(root)
                .command(create)
                .command(edit)
                .command(give);
    }

    private void root(CommandContext<CommandSender> ctx) {
        final @NonNull CommandSender sender = ctx.getSender();

    }

    private void create(CommandContext<CommandSender> ctx) {
        final @NonNull CommandSender sender = ctx.getSender();
        if (!(sender instanceof final @NonNull Player player)) {
            sender.sendMessage(lang.c("command-requires-player"));
            return;
        }

        final @NonNull Optional<@NonNull String> keyOptional = ctx.getOptional("key");
        if (keyOptional.isEmpty()) {
            sender.sendMessage(lang.c("create-needs-key"));
            return;
        }

        String key = keyOptional.get();

        if (registry.get(key).isPresent()) {
            sender.sendMessage(lang.c("create-key-present"));
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            sender.sendMessage(lang.c("must-be-holding-item"));
            return;
        }

        Voucher voucher = new Voucher(key);
        voucher.material(item.getType().toString());

        registry.register(voucher);
        handler.save(voucher);

        EditMenu editMenu = this.menuFactory.newEditMenu(voucher);
        editMenu.showTo(player);
    }

    private void edit(CommandContext<CommandSender> ctx) {
        final @NonNull CommandSender sender = ctx.getSender();
        if (!(sender instanceof final @NonNull Player player)) {
            sender.sendMessage(lang.c("command-requires-player"));
            return;
        }

        final @NonNull Optional<@NonNull String> keyOptional = ctx.getOptional("key");
        if (keyOptional.isEmpty()) {
            sender.sendMessage(lang.c("specify-key"));
            return;
        }

        String key = keyOptional.get();

        if (registry.get(key).isEmpty()) {
            sender.sendMessage(lang.c("key-absent", Map.of("key", key)));
            return;
        }

        Voucher voucher = registry.get(key).get();
        EditMenu editMenu = this.menuFactory.newEditMenu(voucher);
        editMenu.showTo(player);
    }

    // vouchers give <target> <key> <amount>
    private void give(CommandContext<CommandSender> ctx) {
        final @NonNull CommandSender sender = ctx.getSender();

        final @NonNull Optional<@NonNull Player> playerOptional = ctx.getOptional("player");
        if (playerOptional.isEmpty()) {
            sender.sendMessage(lang.c("specify-player"));
            return;
        }

        final @NonNull Player target = playerOptional.get();
        final @NonNull Optional<@NonNull String> keyOptional = ctx.getOptional("key");

        if (keyOptional.isEmpty()) {
            sender.sendMessage(lang.c("specify-key"));
            return;
        }

        final @NonNull String key = keyOptional.get();

        if (registry.get(key).isEmpty()) {
            sender.sendMessage(lang.c("key-absent", Map.of("key", key)));
            return;
        }

        final @NonNull Voucher voucher = registry.get(key).get();
        final @NonNull ItemStack item = voucher.toItemStack();
        final @NonNull Optional<@NonNull Integer> amountOptional = ctx.getOptional("amount");

        if (amountOptional.isPresent()) {
            int amount = amountOptional.get();
            int newAmount = item.getAmount();
            item.setAmount(newAmount * amount);
        }

        target.getWorld().dropItem(target.getLocation(), item);
    }
}
