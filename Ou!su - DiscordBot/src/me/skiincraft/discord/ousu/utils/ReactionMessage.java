package me.skiincraft.discord.ousu.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.skiincraft.discord.ousu.events.ReactionUtils;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.User;

public class ReactionMessage {

	public static List<ReactionUtils> osuHistory = new ArrayList<ReactionUtils>();
	public static Map<User, MessageBuilder> historyLastMessage = new HashMap<>();
	public static Map<User, String> removeAudioMessage = new HashMap<>();

}