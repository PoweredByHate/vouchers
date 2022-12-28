package org.killjoy.vouchers.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class MessageParser {
    private MessageParser() {
    }

    public static @NonNull Component parse(final @NonNull String value) {
        return MiniMessage.miniMessage().deserialize(value);
    }

    public static @NonNull List<Component> parse(final @NonNull List<String> value) {
        List<Component> parsed = new ArrayList<>();
        for (var line : value) {
            parsed.add(parse(line));
        }
        return parsed;
    }

    public static @NonNull String from(final @NonNull Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }
}
