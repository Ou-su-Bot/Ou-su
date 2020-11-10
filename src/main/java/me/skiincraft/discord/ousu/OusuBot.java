package me.skiincraft.discord.ousu;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.skiincraft.api.ousu.OusuAPI;
import me.skiincraft.api.ousu.exceptions.TokenException;
import me.skiincraft.discord.core.CoreStarter;
import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.core.common.PresenceUpdater;
import me.skiincraft.discord.core.common.reactions.ReactionListeners;
import me.skiincraft.discord.core.common.reactions.Reactions;
import me.skiincraft.discord.core.configuration.Language;
import me.skiincraft.discord.core.plugin.OusuPlugin;
import me.skiincraft.discord.ousu.commands.*;
import me.skiincraft.discord.ousu.commands.owner.PermissionCommand;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;
import me.skiincraft.discord.ousu.listener.BeatmapTracking;
import me.skiincraft.discord.ousu.listener.ReceivedListener;
import net.dv8tion.jda.api.entities.Activity;

import java.io.*;
import java.util.Arrays;
import java.util.Locale;

public class OusuBot extends OusuPlugin {

	private static OusuAPI api;
	private static OusuBot instance;
	private static PresenceUpdater presenceUpdater;

	public static OusuBot getInstance() {
		return instance;
	}
	
	private static long apiLast;

	/**
	 * Utilizado para pegar API
	 * Como tenho 2 codigos, isso revesa o uso deles.
	 */
	public static OusuAPI getApi() {
		boolean nulls = Arrays.asList(getTokens()).contains(null) || Arrays.asList(getTokens()).size() == 1;
		if (System.currentTimeMillis() - apiLast >= 2000) {
			for (String string : getTokens()) {
				if (string == null)
					continue;
				if (nulls) {
					apiLast = System.currentTimeMillis();
					return api = new OusuAPI(string, false);
				}
				if (api == null) {
					return api = new OusuAPI(string, false); 
				}
				if (api.getToken().equals(string)) {
					continue;
				}
				apiLast = System.currentTimeMillis();
				return api = new OusuAPI(string, false);
			}
		}
		return api;
	}

	public void onEnable() {
		instance = this;

		/*
		Registrando comandos.
		(?) Pensando em colocar um ClassGetter pra fazer isso.
		 */
		OusuCore.registerCommand(new BeatmapCommand());
		OusuCore.registerCommand(new BeatmapSetCommand());
		OusuCore.registerCommand(new CardCommand());
		OusuCore.registerCommand(new HelpCommand());
		OusuCore.registerCommand(new InviteCommand());
		OusuCore.registerCommand(new LanguageCommand());
		OusuCore.registerCommand(new PingCommand());
		OusuCore.registerCommand(new PrefixCommand());
		OusuCore.registerCommand(new RankingCommand());
		OusuCore.registerCommand(new RecentuserCommand());
		OusuCore.registerCommand(new RestartCommand());
		OusuCore.registerCommand(new SearchCommand());
		OusuCore.registerCommand(new SayCommand());
		OusuCore.registerCommand(new SkinsCommand());
		OusuCore.registerCommand(new TopUserCommand());
		OusuCore.registerCommand(new UserCommand());
		OusuCore.registerCommand(new VoteCommand());
		OusuCore.registerCommand(new PermissionCommand());
		OusuCore.registerCommand(new MalCommand());

		/*
		Registrando eventos.
		(?) Pensando em colocar um ClassGetter pra fazer isso.
		 */
		ReactionListeners listeners = new ReactionListeners();
		Reactions.of(listeners);
		OusuCore.registerListener(listeners);

		OusuCore.registerListener(new ReceivedListener());
		OusuCore.registerListener(new BeatmapTracking());

		/*
		Criando arquivos necessarios para inicialização.
		(?) Sem nenhum desses arquivos o bot não ira funcionar.
		 */
		File file = new File(OusuCore.getAssetsPath().toFile().getAbsolutePath() + "/ousutoken.json");
		try {
			if (!file.exists()) {
				JsonObject object = new JsonObject();
				object.add("Api_1", JsonNull.INSTANCE);
				object.add("Api_2", JsonNull.INSTANCE);

				System.err.println("Criando necessarios para inicialização.");

				FileWriter filew = new FileWriter(file);
				filew.write(object.toString());
				filew.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		OusuCore.addLanguage(new Language(new Locale("pt", "BR")));
		OusuCore.addLanguage(new Language(new Locale("en", "US")));

		presenceUpdater = new PresenceUpdater(Arrays.asList(Activity.listening("ou!help for help."),
				Activity.watching("{guildsize} Servidores online."),
				Activity.listening("ou!vote on Discord Bots")));

		try {
			GenericsEmotes.loadEmotes(OusuCore.getAssetsPath().toFile().getAbsolutePath() + "/emotes/");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String[] getTokens() {
		try {
			InputStream input = new File(OusuCore.getAssetsPath().toFile().getAbsolutePath() + "/ousutoken.json").toURI().toURL().openStream();
			JsonObject object = new JsonParser().parse(new InputStreamReader(input)).getAsJsonObject();
			boolean allIsNull = object.get("Api_1").isJsonNull() && object.get("Api_2").isJsonNull();

			if (allIsNull) {
				throw new TokenException("As keys Token da API do ousu estão nulas.", null);
			}
			
			String key1 = (object.get("Api_1").isJsonNull()) ? null :  object.get("Api_1").getAsString();
			String key2 = (object.get("Api_2").isJsonNull()) ? null :  object.get("Api_2").getAsString();
			
			return new String[] {key1, key2};
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static PresenceUpdater getPresenceUpdater() {
		return presenceUpdater;
	}

}
