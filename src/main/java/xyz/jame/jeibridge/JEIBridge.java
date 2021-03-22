package xyz.jame.jeibridge;

import org.bukkit.plugin.java.JavaPlugin;

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
}
