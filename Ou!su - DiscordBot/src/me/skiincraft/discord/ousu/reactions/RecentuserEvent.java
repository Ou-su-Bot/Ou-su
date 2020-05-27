package me.skiincraft.discord.ousu.reactions;

import java.util.List;

import me.skiincraft.discord.ousu.events.TopUserReaction;
import me.skiincraft.discord.ousu.manager.ReactionUtils;
import me.skiincraft.discord.ousu.manager.ReactionsManager;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class RecentuserEvent extends ReactionsManager {

	@Override
	public List<ReactionUtils> listHistory() {
		return ReactionMessage.recentHistory;
	}

	@Override
	public void action(User user, TextChannel channel, String emoji) {
		Object obj = getUtils().getObject();
		EmbedBuilder[] beatmap = (EmbedBuilder[]) obj;
		
		if (emoji.equalsIgnoreCase("◀")) {
			listHistory().remove(getUtils());

			int v = getUtils().getValue();
			if (v <= 0) {
				v = 0;
				listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, v));
				return;
			} else {
				v = getUtils().getValue() - 1;
			}

			EmbedBuilder embed = beatmap[v];

			channel.editMessageById(getEvent().getMessageId(), embed.build()).queue();
			listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, v));

		}

		// http://b.ppy.sh/preview/music.mp3

		if (emoji.equalsIgnoreCase("◼")) {

		}

		if (emoji.equalsIgnoreCase("▶")) {
			listHistory().remove(getUtils());
			int v = getUtils().getValue();
			v += 1;

			if (v >= beatmap.length) {
				listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, beatmap.length - 1));
				return;
			}

			EmbedBuilder embed = beatmap[v];

			channel.editMessageById(getEvent().getMessageId(), embed.build()).queue();
			listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, v));
		}
	}

}
