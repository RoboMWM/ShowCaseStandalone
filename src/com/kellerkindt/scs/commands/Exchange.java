/*
 * ShowCaseStandalone - A Minecraft-Bukkit-API Shop Plugin
 * Copyright (C) 2016-08-16 22:43 +02 kellerkindt (Michael Watzko) <copyright at kellerkindt.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kellerkindt.scs.commands;

import com.kellerkindt.scs.ShopManipulator;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.exceptions.MissingOrIncorrectArgumentException;
import com.kellerkindt.scs.interfaces.MultiStageCommand;
import com.kellerkindt.scs.interfaces.RunLater;
import com.kellerkindt.scs.internals.NamedUUID;
import com.kellerkindt.scs.shops.ExchangeShop;
import com.kellerkindt.scs.shops.Shop;
import com.kellerkindt.scs.utilities.Term;
import com.kellerkindt.scs.utilities.Utilities;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 *
 * @author michael <michael at kellerkindt.com>
 */
public class Exchange extends Create {

    public static final String ARG_SECOND_THIS = "that";
    
    public Exchange (ShowCaseStandalone scs, String ... permissions) {
        super(scs, permissions, 4);
    }
    
    @Override
    protected boolean tabCompleteForArgumentSize(int size) {
        return size <= 2;
    }
    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        // prepare
        Player       player        = (Player)sender;
        ExchangeShop shop        = new ExchangeShop(
                scs,
                UUID.randomUUID(),
                new NamedUUID(
                        player.getUniqueId(),
                        player.getName()
                ),
                null,
                null,
                null
                );


        shop.setItemStack(Utilities.getItemStack(player, args[0]));

        setAmount    (shop,              args[2]);
        shop.setPrice(Double.parseDouble(args[3]));


        // second ItemStack allows post-selection
        Utilities.getItemStack(player, args[1], (exchangeItemStack) -> {
            // set the ItemStack
            shop.setExchangeItemStack(exchangeItemStack);

            // start making this shop available
            registerShopToCreate(player, shop);

        }, (runLater) -> {
            scs.registerRunLater(player, new MultiStageCommand() {
                @Override
                public void execute(CommandSender sender, String[] args) throws CommandException {
                    runLater.trigger();
                }

                @Override
                public void abort(Player player) {
                    runLater.abort(player);
                }
            });
            scs.sendMessage(player, Term.INFO_SHOP_EXCHANGE_SPECIFY_ITEM.get());
        });

    }
}
