package com.potdeposit;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import java.util.Arrays;


@Slf4j
@PluginDescriptor(
	name = "PotDeposit"
)
public class PotDeposit extends Plugin
{
	@Inject
	private Client client;

	final int NEXUSROOM = 14160;

	@Inject
	private PotDepositConfig config;

	// ~~stole from~~ inspired by https://github.com/mad-s/easy-unnote/blob/main/src/main/java/easyunnote/EasyUnnotePlugin.java
	// ~~stole from~~ inspired by https://github.com/oohwooh/no-use-players/blob/master/src/main/java/com/oohwooh/NoUsePlayerPlugin.java

	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{
		// The menu is not rebuilt when it is open, so don't swap or else it will
		// repeatedly swap entries
		if (client.getLocalPlayer().getWorldLocation().getRegionID() != NEXUSROOM) {
			return;
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

	@Provides
	PotDepositConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PotDepositConfig.class);
	}
}
