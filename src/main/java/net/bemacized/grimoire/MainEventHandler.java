package net.bemacized.grimoire;

import net.bemacized.grimoire.chathandlers.*;
import net.bemacized.grimoire.data.models.preferences.GuildPreferences;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainEventHandler extends ListenerAdapter {

	private List<ChatHandler> chatHandlers;

	public MainEventHandler() {
		// Make sure chatHandlers is initialized
		if (chatHandlers == null) chatHandlers = new ArrayList<>();
		// Register handlers
		List<Class<? extends ChatHandler>> handlerClasses = Arrays.asList(
				EvalHandler.class,
				HelpHandler.class,
				CommandHandler.class,
				InlineCardHandler.class,
				InlinePriceHandler.class
		);
		Collections.reverse(handlerClasses);
		handlerClasses.forEach(this::registerChatHandler);
	}

	private void registerChatHandler(Class<? extends ChatHandler> handler) {
		// Obtain top handler
		ChatHandler lastHandler = (!chatHandlers.isEmpty()) ? chatHandlers.get(0) : new ChatHandler(null) {
			@Override
			protected void handle(MessageReceivedEvent e, GuildPreferences guildPreferences, ChatHandler next) {
			}
		};

		// Instantiate & Register handler with top handler
		try {
			chatHandlers.add(0, handler.getDeclaredConstructor(ChatHandler.class).newInstance(lastHandler));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (!chatHandlers.isEmpty() && !e.getAuthor().isBot() && !e.getAuthor().getId().equals(Grimoire.getInstance().getDiscord().getSelfUser().getId()))
			new Thread(() -> this.chatHandlers.get(0).handle(e)).start();
	}


}
