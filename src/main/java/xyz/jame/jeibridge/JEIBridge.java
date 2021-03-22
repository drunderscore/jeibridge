package xyz.jame.jeibridge;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class JEIBridge extends JavaPlugin
{
    public static final String CHEAT_PERMISSION = "jeibridge.cheat";
    public static final String JEI_CHANNEL = "jei:channel";

    @Override
    public void onEnable()
    {
        getServer().getMessenger().registerIncomingPluginChannel(this, JEI_CHANNEL, new JEIIncomingMessageHandler(this));
        getServer().getMessenger().registerOutgoingPluginChannel(this, JEI_CHANNEL);
    }

    public void sendCheatPermission(Player ply, boolean permission)
    {
        ply.sendPluginMessage(this, JEIBridge.JEI_CHANNEL, new byte[]
                {
                        (byte) PacketId.ClientBound.CHEAT_PERMISSION.ordinal(),
                        (byte) (permission ? 1 : 0)
                });
    }

    public boolean hasPermission(@NotNull Player player)
    {
        return player.hasPermission(CHEAT_PERMISSION);
    }
}
