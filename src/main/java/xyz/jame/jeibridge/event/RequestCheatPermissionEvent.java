package xyz.jame.jeibridge.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class RequestCheatPermissionEvent extends PlayerEvent
{
    private static final HandlerList handlers = new HandlerList();
    private boolean hasPermission;

    public RequestCheatPermissionEvent(@NotNull Player who, boolean hasPermission)
    {
        super(who);
        this.hasPermission = hasPermission;
    }

    @Override
    public @NotNull HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public boolean hasPermission()
    {
        return hasPermission;
    }

    public void setHasPermission(boolean hasPermission)
    {
        this.hasPermission = hasPermission;
    }
}
