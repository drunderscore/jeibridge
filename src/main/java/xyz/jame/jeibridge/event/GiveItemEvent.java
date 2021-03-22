package xyz.jame.jeibridge.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiveItemEvent extends PlayerEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private ItemStack item;
    private boolean canceled;

    public GiveItemEvent(@NotNull Player who, ItemStack item)
    {
        super(who);
        this.item = item;
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

    public ItemStack getItem()
    {
        return item;
    }

    public void setItem(ItemStack item)
    {
        this.item = item;
    }

    @Override
    public boolean isCancelled()
    {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel)
    {
        canceled = cancel;
    }
}
