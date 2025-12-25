package net.dzikoysk.funnyguilds.feature.gui;

import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GuiActionHandler extends AbstractFunnyListener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.CHEST) {
            return;
        }

        InventoryHolder inventoryHolder = inventory.getHolder();
        if (!(inventoryHolder instanceof FunnyHolder)) {
            return;
        }

        FunnyHolder funnyHolder = (FunnyHolder) inventoryHolder;

        // Allow clicking in player's inventory (bottom part) to pick up items
        // Only cancel and handle clicks in the GUI inventory (top part)
        if (event.getClickedInventory() == null) {
            event.setCancelled(true);
            return;
        }
        
        // If clicking in player's own inventory
        if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
            // Handle SHIFT+click to deposit items into vault
            if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                // Let the FunnyHolder handle shift-click from player inventory
                funnyHolder.handleShiftClick(event);
            }
            // Don't cancel non-shift clicks - let the player pick up items
            return;
        }

        // Clicking in the GUI inventory - cancel default behavior and handle
        event.setCancelled(true);
        funnyHolder.handleClick(event);
    }

}
