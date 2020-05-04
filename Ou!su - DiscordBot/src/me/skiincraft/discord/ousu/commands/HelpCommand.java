package me.skiincraft.discord.ousu.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class HelpCommand extends Commands {

	public HelpCommand() {
		super("ou!", "help");
	}

	public static List<Commands> commands = new ArrayList<Commands>();

	@Override
	public String[] helpMessage(LanguageManager lang) {
		return lang.translatedArrayHelp("HELPMESSAGE_HELP");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Ajuda;
	}

	@Override
	public void action(String[] args, User user, TextChannel channel) {
		if (isInsuficient()) {
			sendEmbedMessage(embed(channel.getGuild())).queue();
			return;
		}

		if (args.length == 2) {
			System.out.println(args[1]);
			sendEmbedMessage(emb(args[1], channel.getGuild())).queue();
			return;
		}

		return;
	}

	public EmbedBuilder emb(String comando, Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();

		for (Commands com : commands) {
			if (comando.equalsIgnoreCase(com.getCommand())) {
				embed.setTitle("Help <" + com.getCommand() + ">");
				if (com.helpMessage(getLang()) != null) {
					embed.setDescription(String.join("\n", com.helpMessage(getLang())));
				} else {
					embed.setDescription("Não foi definido nenhuma descrição para este comando :/");
				}

				if (com.hasAliases()) {
					String[] alias = com.getAliases().stream().toArray(String[]::new);
					embed.addField("Aliases", String.join("\n", alias), true);
				}

				embed.addField("Usage", com.getUsage().replace("ou!", new SQLAccess(guild).get("prefix")), true);
				return embed;
			}
		}
		return null;
	}

	public EmbedBuilder embed(Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();

		StringBuffer a = new StringBuffer();
		StringBuffer b = new StringBuffer();
		StringBuffer c = new StringBuffer();
		StringBuffer d = new StringBuffer();

		for (int i = 0; i < commands.size(); i++) {
			Commands comando = commands.get(i);
			String prefix = new SQLAccess(guild).get("prefix");

			if (comando.getCategoria() == CommandCategory.Administração) {
				a.append(prefix + comando.getCommand());
				a.append(",");
			}
			if (comando.getCategoria() == CommandCategory.Ajuda) {
				b.append(prefix + comando.getCommand());
				b.append(",");
			}
			if (comando.getCategoria() == CommandCategory.Osu) {
				c.append(prefix + comando.getCommand());
				c.append(",");
			}
			if (comando.getCategoria() == CommandCategory.Sobre) {
				d.append(prefix + comando.getCommand());
				d.append(",");
			}
		}

		String[] Adm = a.toString().split(",");
		Arrays.sort(Adm);
		String[] Ajuda = b.toString().split(",");
		Arrays.sort(Ajuda);
		String[] Osu = c.toString().split(",");
		Arrays.sort(Osu);
		String[] Sobre = d.toString().split(",");
		Arrays.sort(Sobre);

		embed.setDescription("Todos os comandos disponiveis no bot.");

		embed.addField("**" + CommandCategory.Administração.getCategoria() + "**", "`" + String.join("\n", Adm) + "`",
				true);
		embed.addField("**" + CommandCategory.Ajuda.getCategoria() + "**", "`" + String.join("\n", Ajuda) + "`", true);
		embed.addField("**" + CommandCategory.Osu.getCategoria() + "**", "`" + String.join("\n", Osu) + "`", true);
		embed.addField("**" + CommandCategory.Sobre.getCategoria() + "**", "`" + String.join("\n", Sobre) + "`", true);

		embed.setTitle("Lista de comandos");
		User user = OusuBot.getOusu().getJda().getUserById("247096601242238991");
		embed.setFooter(user.getName() + "#" + user.getDiscriminator() + " | Ou!su bot ™", user.getAvatarUrl());
		return embed;
	}
}