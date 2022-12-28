package org.killjoy.vouchers.voucher.configurate;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.killjoy.vouchers.voucher.Voucher;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class VoucherSerializer implements TypeSerializer<Voucher> {
    public static final VoucherSerializer INSTANCE = new VoucherSerializer();

    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String LORE = "lore";
    private static final String COMMANDS = "commands";

    private VoucherSerializer() {
    }

    @Override
    public Voucher deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String key = String.valueOf(node.key());
        Voucher voucher = new Voucher(key);

        final String nameString = nonVirtualNode(node, NAME).getString();
        if (nameString != null) {
            voucher.name(nameString);
        }

        final String typeString = nonVirtualNode(node, TYPE).getString();
        if (typeString != null) {
            voucher.material(typeString);
        }

        final List<String> lore = nonVirtualNode(node, LORE).getList(String.class);
        if (lore != null) {
            for (var line : lore) {
                voucher.lore(line);
            }
        }

        final List<String> commands = nonVirtualNode(node, COMMANDS).getList(String.class);
        if (commands != null) {
            for (var line : commands) {
                voucher.command(line);
            }
        }
        return voucher;
    }

    @Override
    public void serialize(Type type, @Nullable Voucher obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }

        node.set(obj.key());
        node.node(NAME).set(obj.name());
        node.node(TYPE).set(obj.material());
    }

    private ConfigurationNode nonVirtualNode(final ConfigurationNode source, final Object... path) throws SerializationException {
        if (!source.hasChild(path)) {
            throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node");
        }
        return source.node(path);
    }
}
