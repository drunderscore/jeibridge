package xyz.jame.jeibridge;

import com.comphenix.protocol.utility.StreamSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import xyz.jame.jeibridge.event.GiveItemEvent;
import xyz.jame.jeibridge.event.RequestCheatPermissionEvent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;

public class JEIIncomingMessageHandler implements PluginMessageListener
{
    private final JEIBridge bridge;
    private final HashMap<PacketId.ServerBound, BiConsumer<Player, ByteBuffer>> handlers = new HashMap<>();

    public JEIIncomingMessageHandler(JEIBridge bridge)
    {
        this.bridge = bridge;
        handlers.put(PacketId.ServerBound.CHEAT_PERMISSION_REQUEST, this::onCheatPermissionRequest);
        handlers.put(PacketId.ServerBound.RECIPE_TRANSFER, this::onRecipeTransfer);
        handlers.put(PacketId.ServerBound.GIVE_ITEM, this::onGiveItem);
    }

    private void sendCheatPermission(Player ply, boolean permission)
    {
        ply.sendPluginMessage(bridge, JEIBridge.JEI_CHANNEL, new byte[]
                {
                        (byte) PacketId.ClientBound.CHEAT_PERMISSION.ordinal(),
                        (byte) (permission ? 1 : 0)
                });
    }

    private void onGiveItem(Player player, ByteBuffer buffer)
    {
        if (!bridge.hasPermission(player))
        {
            sendCheatPermission(player, false);
            return;
        }

        var itemBytes = new byte[buffer.array().length - 1];
        System.arraycopy(buffer.array(), 1, itemBytes, 0, itemBytes.length);
        try
        {
            // TODO: Take the give mode into account
            var event = new GiveItemEvent(player, StreamSerializer.getDefault().deserializeItemStackFromByteArray(itemBytes));
            Bukkit.getPluginManager().callEvent(event);
            if (event.getItem() != null && !event.isCancelled())
                player.getInventory().addItem(event.getItem());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message)
    {
        bridge.getSLF4JLogger().debug("From {}: {}", player.getName(), Arrays.toString(message));
        var buffer = ByteBuffer.wrap(message);
        var id = buffer.get();
        if (id < 0 || id >= PacketId.ServerBound.VALUES.length)
        {
            bridge.getSLF4JLogger().warn("Player {} sent invalid packet ID {}", player.getName(), id);
            return;
        }

        var packetId = PacketId.ServerBound.VALUES[id];
        bridge.getSLF4JLogger().debug("Received packet {} from {}", packetId.name(), player.getName());
        var func = handlers.get(packetId);
        if (func == null) // This is a valid packet, but we don't handle it.
            return;

        try
        {
            func.accept(player, buffer);
        }
        catch (Exception e)
        {
            bridge.getSLF4JLogger().warn("Player {} caused exception from network request.", player.getName());
            e.printStackTrace();
        }
    }

    private void onCheatPermissionRequest(Player ply, ByteBuffer buffer)
    {
        var event = new RequestCheatPermissionEvent(ply, bridge.hasPermission(ply));
        Bukkit.getPluginManager().callEvent(event);
        sendCheatPermission(ply, event.hasPermission());
    }

    private void onRecipeTransfer(Player ply, ByteBuffer buffer)
    {
        var recipeSize = readVarInt(buffer);
        var recipe = new HashMap<Integer, Integer>(recipeSize);
        for (var i = 0; i < recipeSize; i++)
            recipe.put(readVarInt(buffer), readVarInt(buffer));

        var craftingSlotsSize = readVarInt(buffer);
        var craftingSlots = new ArrayList<Integer>(craftingSlotsSize);
        for (var i = 0; i < craftingSlotsSize; i++)
            craftingSlots.add(readVarInt(buffer));

        var inventorySlotsSize = readVarInt(buffer);
        var inventorySlots = new ArrayList<Integer>(inventorySlotsSize);
        for (var i = 0; i < inventorySlotsSize; i++)
            inventorySlots.add(readVarInt(buffer));

        var maxTransfer = buffer.get() == 1;
        // TODO: figure out what this means
        var requireCompleteSets = buffer.get() == 1;

        var openInv = ply.getOpenInventory();

        if (!(openInv.getTopInventory() instanceof CraftingInventory))
            return;

        var craftingInv = (CraftingInventory) openInv.getTopInventory();

        // TODO: Take maxTransfer amd requireCompleteSets into account
        // TODO: Make this code not shitty
        var itemCountMap = new HashMap<Integer, Integer>();
        // Count how many items from each inventory slot is needed
        for (var kv : recipe.entrySet())
        {
            itemCountMap.compute(kv.getValue(), (key, oldVal) ->
            {
                if (oldVal != null)
                    return oldVal + 1;
                return 1;
            });
        }

        // Check if the player actually has enough items to complete the recipe
        for (var kv : itemCountMap.entrySet())
        {
            var item = openInv.getItem(kv.getKey());
            if (item == null || item.getAmount() < kv.getValue())
                return;
        }

        // Return items in the crafting grid to the players inventory
        // IntelliJ hurt itself in confusion!
        // noinspection ConstantConditions
        if (!openInv.getBottomInventory().addItem(Arrays.stream(craftingInv.getMatrix()).filter(Objects::nonNull).toArray(ItemStack[]::new)).isEmpty())
            return;

        openInv.getTopInventory().clear();


        for (var kv : recipe.entrySet())
        {
            var item = openInv.getItem(kv.getValue());
            if (item == null)
                return;
            openInv.setItem(craftingSlots.get(kv.getKey()), item.asOne());
            item.subtract();
        }
    }

    // Thanks to the smart people at https://wiki.vg/Data_types
    public static int readVarInt(ByteBuffer buffer)
    {
        int numRead = 0;
        int result = 0;
        byte read;
        do
        {
            read = buffer.get();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5)
                throw new RuntimeException("VarInt is too big");

        } while ((read & 0b10000000) != 0);

        return result;
    }
}
