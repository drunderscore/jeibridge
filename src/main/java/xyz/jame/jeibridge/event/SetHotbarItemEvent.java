package xyz.jame.jeibridge.event;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SetHotbarItemEvent extends PlayerEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private byte slot;
    private ItemStack item;
    private boolean canceled;

    public SetHotbarItemEvent(@NotNull Player who, ItemStack item, byte slot)
    {
        super(who);
        this.item = item;
        this.slot = slot;
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

    public byte getSlot()
    {
        return slot;
    }

    public void setSlot(byte slot)
    {
        Preconditions.checkArgument(slot > 0 && slot <= 8, "slot must be between 0-8");
        this.slot = slot;
    }
}
