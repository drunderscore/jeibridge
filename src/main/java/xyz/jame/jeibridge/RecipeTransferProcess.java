package xyz.jame.jeibridge;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RecipeTransferProcess
{
    private final Map<Integer, Integer> recipe;
    private final List<Integer> craftingSlots;
    private final List<Integer> inventorySlots;
    private final InventoryView inv;
    private final Player ply;
    private final boolean max;

    RecipeTransferProcess(Map<Integer, Integer> recipe,
                          List<Integer> craftingSlots,
                          List<Integer> inventorySlots,
                          InventoryView inv,
                          Player ply, boolean max)
    {
        this.recipe = recipe;
        this.craftingSlots = craftingSlots;
        this.inventorySlots = inventorySlots;
        this.inv = inv;
        this.ply = ply;
        this.max = max;
    }
}
