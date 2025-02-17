package de.codingair.tradesystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.SuccessPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.tradesystem.proxy.packets.TradeCheckFinishPacket;
import de.codingair.tradesystem.spigot.TradeSystem;
import de.codingair.tradesystem.spigot.trade.ProxyTrade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TradeCheckFinishPacketHandler implements ResponsiblePacketHandler<TradeCheckFinishPacket, SuccessPacket> {

    @Override
    public @NotNull CompletableFuture<SuccessPacket> response(@NotNull TradeCheckFinishPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        ProxyTrade t = TradeSystem.proxy().getTrade(packet.getRecipient(), packet.getSender());

        boolean success;
        if (t == null) success = false;
        else success = t.receiveFinishCheck();

        return CompletableFuture.completedFuture(new SuccessPacket(success));
    }
}
