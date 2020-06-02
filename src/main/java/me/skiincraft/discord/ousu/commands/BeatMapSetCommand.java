package me.skiincraft.discord.ousu.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.skiincraft.api.ousu.beatmaps.Beatmap;
import me.skiincraft.api.ousu.exceptions.InvalidBeatmapException;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.embeds.BeatmapEmbed;
import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.events.DefaultReaction;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.utils.Emoji;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class BeatMapSetCommand extends Commands {

	public BeatMapSetCommand() {
		super("ou!", "beatmapset", "ou!beatmapset <id>", null);
	}

	@Override
	public String[] helpMessage(LanguageManager lang) {
		return lang.translatedArrayHelp("OSU_HELPMESSAGE_BEATMAP");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Osu;
	}

	@Override
	public void action(String[] args, String label, TextChannel channel) {
		if (args.length == 0) {
			sendUsage().queue();
			return;
		}

		if (args.length == 1) {
			try {
				List<Beatmap> osuBeat = OusuBot.getOsu().getBeatmapSet(Integer.valueOf(args[0]));

				sendEmbedMessage(TypeEmbed.LoadingEmbed()).queue(loadmessage -> {
					List<EmbedBuilder> bmb = new ArrayList<EmbedBuilder>();
					for (Beatmap b : osuBeat) {
						bmb.add(BeatmapEmbed.beatmapEmbed(b, channel.getGuild()));
					}

					EmbedBuilder[] bm = new EmbedBuilder[bmb.size()];
					bmb.toArray(bm);
					loadmessage.editMessage(bm[0].build()).queue();
					loadmessage.addReaction("U+25C0").queue();
					loadmessage.addReaction("U+25FC").queue();
					loadmessage.addReaction("U+25B6").queue();

					ReactionMessage.beatHistory.add(new DefaultReaction(getUser().getId(), loadmessage.getId(), bm, 0));
					try {
						loadmessage.getChannel()
								.sendFile(osuBeat.get(0).getBeatmapPreview(),
										bm[0].build().getTitle().replace(Emoji.HEADPHONES.getAsMention(), "") + ".mp3")
								.queue();
					} catch (IOException e) {
						e.printStackTrace();
					}

				});
			} catch (InvalidBeatmapException e) {
				String[] msg = getLang().translatedArrayOsuMessages("INEXISTENT_BEATMAPID");

				MessageEmbed build = TypeEmbed.WarningEmbed(msg[0], StringUtils.commandMessage(msg)).build();
				sendEmbedMessage(build).queue();
				return;
			} catch (NumberFormatException e) {
				String[] msg = getLang().translatedArrayOsuMessages("USE_NUMBERS");

				MessageEmbed build = TypeEmbed.WarningEmbed(msg[0], StringUtils.commandMessage(msg)).build();
				sendEmbedMessage(build).queue();
				return;
			}

		}
	}
}