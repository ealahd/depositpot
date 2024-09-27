package com.potdeposit;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import java.util.Arrays;


@Slf4j
@PluginDescriptor(
	name = "ToA Potion Deposit"
)
public class PotDeposit extends Plugin
{
	@Inject
	private Client client;

	final int NEXUSROOM = 14160;

	// ~~stole from~~ inspired by https://github.com/mad-s/easy-unnote/blob/main/src/main/java/easyunnote/EasyUnnotePlugin.java
	// ~~stole from~~ inspired by https://github.com/oohwooh/no-use-players/blob/master/src/main/java/com/oohwooh/NoUsePlayerPlugin.java

	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{
		// The menu is not rebuilt when it is open, so don't swap or else it will
		// repeatedly swap entries

		//This one we stole from the ToA plugin by https://github.com/LlemonDuck/tombs-of-amascut/blob/main/src/main/java/com/duckblade/osrs/toa/util/RaidStateTracker.java
		final LocalPoint lp = client.getLocalPlayer().getLocalLocation();
		if (lp == null) {
			return; // Player's location is not available
		}

// Convert the LocalPoint directly to WorldPoint and check region
		final int regionID = WorldPoint.fromLocalInstance(client, lp).getRegionID();

		if (regionID != NEXUSROOM) {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Current region ID: " + WorldPoint.fromLocalInstance(client, lp).getRegionID(), null);
			return; // The player is not in the Nexus room
		}

		if (client.getGameState() != GameState.LOGGED_IN || client.isMenuOpen()) {
			return;
		}
		final Widget selectedWidget = client.getSelectedWidget();
		if (selectedWidget == null) {
			return;
		}
		final int itemId = selectedWidget.getItemId();
		if (itemId <= 0 || !client.isWidgetSelected()) {
			return;
		}


		MenuEntry[] menuEntries = client.getMenuEntries();
		MenuEntry[] newEntries = Arrays.stream(menuEntries)
				.filter(e -> {
					switch (e.getType()) {
						case WIDGET_TARGET_ON_PLAYER:
							return false;
						default:
							return true;
					}
				})
				.toArray(MenuEntry[]::new);

		client.setMenuEntries(newEntries);
	}
}
