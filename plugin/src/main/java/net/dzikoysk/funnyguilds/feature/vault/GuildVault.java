package net.dzikoysk.funnyguilds.feature.vault;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

/**
 * Represents the guild vault containing items and money.
 */
public class GuildVault {

    private final UUID guildUuid;
    private double balance;
    private final List<ItemStack> items;
    private boolean dirty = false;

    public GuildVault(UUID guildUuid) {
        this.guildUuid = guildUuid;
        this.balance = 0.0;
        this.items = new ArrayList<>();
    }

    public GuildVault(UUID guildUuid, double balance, List<ItemStack> items) {
        this.guildUuid = guildUuid;
        this.balance = balance;
        this.items = new ArrayList<>(items);
    }

    public UUID getGuildUuid() {
        return this.guildUuid;
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        this.balance = Math.max(0, balance);
        this.dirty = true;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            this.dirty = true;
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            this.dirty = true;
            return true;
        }
        return false;
    }

    public boolean canAfford(double amount) {
        return this.balance >= amount;
    }

    public List<ItemStack> getItems() {
        return new ArrayList<>(this.items);
    }

    public int getItemCount() {
        return this.items.size();
    }

    public void addItem(ItemStack item) {
        if (item != null && !item.getType().isAir()) {
            this.items.add(item.clone());
            this.dirty = true;
        }
    }

    public boolean removeItem(int index) {
        if (index >= 0 && index < this.items.size()) {
            this.items.remove(index);
            this.dirty = true;
            return true;
        }
        return false;
    }

    public ItemStack getItem(int index) {
        if (index >= 0 && index < this.items.size()) {
            return this.items.get(index).clone();
        }
        return null;
    }

    public void setItems(List<ItemStack> items) {
        this.items.clear();
        for (ItemStack item : items) {
            if (item != null && !item.getType().isAir()) {
                this.items.add(item.clone());
            }
        }
        this.dirty = true;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void markClean() {
        this.dirty = false;
    }

    public void markDirty() {
        this.dirty = true;
    }
}
