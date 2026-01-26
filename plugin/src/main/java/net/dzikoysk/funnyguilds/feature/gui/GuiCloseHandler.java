package net.dzikoysk.funnyguilds.feature.gui;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.database.DataSaveAsyncTask;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class GuiCloseHandler extends AbstractFunnyListener {

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.CHEST) {
            return;
        }

        if (!(inventory.getHolder() instanceof FunnyHolder)) {
            return;
        }

        // Schedule immediate async save of changed data to reduce chance of item loss
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        if (plugin == null) {
            return;
        }

        plugin.scheduleFunnyTasks(new DataSaveAsyncTask(plugin.getDataModel(), false));
    }
}
