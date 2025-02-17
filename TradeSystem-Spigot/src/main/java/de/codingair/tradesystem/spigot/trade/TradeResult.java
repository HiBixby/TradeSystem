package de.codingair.tradesystem.spigot.trade;

import de.codingair.tradesystem.spigot.trade.gui.layout.types.TradeIcon;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.impl.economy.EconomyIcon;
import de.codingair.tradesystem.spigot.utils.Lang;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TradeResult {
    private final Trade trade;
    private final Player player;
    private final int playerId;
    private final LinkedHashMap<ItemStack, Boolean> items = new LinkedHashMap<>();
    private final List<EconomyIcon<?>> economyIcons = new ArrayList<>();

    public TradeResult(@NotNull Trade trade, @NotNull Player player, int playerId) {
        this.trade = trade;
        this.player = player;
        this.playerId = playerId;
    }

    @NotNull
    List<String> buildItemReport() {
        return buildItemReport(item -> item.getType().name());
    }

    @NotNull
    public List<String> buildItemReport(Function<ItemStack, String> mapper) {
        List<String> lines = new ArrayList<>();

        for (Map.Entry<ItemStack, Boolean> e : items.entrySet()) {
            ItemStack item = e.getKey();
            boolean receive = e.getValue();

            String itemName = Lang.get("Trade_Finish_Report_Object_Item", player,
                    new Lang.P("amount", item.getAmount() + ""),
                    new Lang.P("item", formatName(mapper.apply(item)))
            );

            Lang.P info = new Lang.P("object", itemName);
            if (receive) lines.add(Lang.get("Trade_Finish_Report_Receive", player, info));
            else lines.add(Lang.get("Trade_Finish_Report_Give", player, info));
        }

        return lines;
    }

    @NotNull
    List<String> buildEconomyReport() {
        List<String> lines = new ArrayList<>();

        for (EconomyIcon<?> icon : economyIcons) {
            BigDecimal diff = icon.getOverallDifference(trade, playerId);
            if (diff.signum() == 0) continue;
            boolean receive = diff.signum() > 0;

            boolean singular = diff.equals(BigDecimal.ONE);
            if (receive) {
                Lang.P info = new Lang.P("object",
                        EconomyIcon.makeFancyString(diff, icon.isDecimal()) + " " + icon.getName(player, singular));
                lines.add(Lang.get("Trade_Finish_Report_Receive", player, info));
            } else {
                Lang.P info = new Lang.P("object",
                        EconomyIcon.makeFancyString(diff.negate(), icon.isDecimal()) + " " + icon.getName(player, singular));
                lines.add(Lang.get("Trade_Finish_Report_Give", player, info));
            }
        }

        return lines;
    }

    @NotNull
    public static String formatName(@NotNull String name) {
        StringBuilder b = new StringBuilder(name.toLowerCase());

        int i = 0;
        do {
            b.replace(i, i + 1, b.substring(i, i + 1).toUpperCase());
            i = b.indexOf("_", i);
            if (i >= 0) b.replace(i, i + 1, " ");
            i++;
        } while (i > 0 && i < b.length());

        return b.toString().trim();
    }

    /**
     * @return The current player.
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * @return The id of the current player. Either 0 or 1.
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * @return ALl items that were exchanged. The boolean value indicates whether the item was received (true) or given (false).
     */
    public LinkedHashMap<ItemStack, Boolean> getItems() {
        return items;
    }

    /**
     * @return All (original) economy icons that were exchanged. Changing any values result in modified trades!
     */
    public List<EconomyIcon<?>> getEconomyIcons() {
        return economyIcons;
    }

    void add(@Nullable ItemStack item, boolean receive) {
        if (item == null) return;
        items.put(item.clone(), receive);
    }

    void add(@Nullable TradeIcon icon) {
        if (icon == null) return;
        if (icon instanceof EconomyIcon) addEconomyIcon((EconomyIcon<?>) icon);
    }

    private void addEconomyIcon(@NotNull EconomyIcon<?> economyIcon) {
        economyIcons.add(economyIcon);
    }
}
