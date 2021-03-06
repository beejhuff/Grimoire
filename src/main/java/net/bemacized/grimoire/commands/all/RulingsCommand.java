package net.bemacized.grimoire.commands.all;

import net.bemacized.grimoire.commands.CardBaseCommand;
import net.bemacized.grimoire.data.models.card.MtgCard;
import net.bemacized.grimoire.data.models.mtgjson.MtgJsonCard;
import net.bemacized.grimoire.data.models.preferences.GuildPreferences;
import net.bemacized.grimoire.utils.MTGUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RulingsCommand extends CardBaseCommand {

	@Override
	public String name() {
		return "rulings";
	}

	@Override
	public String[] aliases() {
		return new String[]{"rules", "ruling"};
	}

	@Override
	public String description() {
		return "Retrieves the current rulings of the specified card.";
	}

	@Override
	protected MessageEmbed getEmbedForCard(MtgCard card, GuildPreferences guildPreferences, MessageReceivedEvent e) {
		// Let's check if there are any rulings
		if (card.getRulings().length == 0)
			return errorEmbedFormat("There are no rulings for **'%s'**.", card.getName()).get(0);

		// Show the rulings
		EmbedBuilder eb = new EmbedBuilder()
				.setColor(MTGUtils.colorIdentitiesToColor(card.getColorIdentity()))
				.setTitle(card.getName(), guildPreferences.getCardUrl(card))
				.setDescription("**Rulings**");

		String TOO_MANY_RULINGS = "**You can find %s more rulings for this card on [Gatherer](" + card.getGathererUrl() + ")**";
		int rulesNotShown = 0;

		String lastDate = null;
		for (MtgJsonCard.Ruling ruling : card.getRulings()) {
			if (ruling.getText().length() > 1024) {
				rulesNotShown++;
				continue;
			}
			EmbedBuilder ebTmp = new EmbedBuilder((eb.build()));
			ebTmp.addField((lastDate != null && lastDate.equalsIgnoreCase(ruling.getDate())) ? "" : ruling.getDate(), ruling.getText(), false);
			lastDate = ruling.getDate();
			if (ebTmp.build().getLength() > 4000 - TOO_MANY_RULINGS.length() - 10) {
				rulesNotShown += card.getRulings().length - eb.getFields().size();
				break;
			} else {
				eb = ebTmp;
			}
		}

		if (eb.getFields().isEmpty()) TOO_MANY_RULINGS = TOO_MANY_RULINGS.replaceAll(" more ", " ");
		else if (eb.getFields().size() == 1) TOO_MANY_RULINGS = TOO_MANY_RULINGS.replaceAll("rulings", "ruling");
		if (rulesNotShown > 0) eb.addField("", String.format(TOO_MANY_RULINGS, rulesNotShown), false);

		return eb.build();
	}
}