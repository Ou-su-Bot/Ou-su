package me.skiincraft.ousubot.view.embeds;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import me.skiincraft.ousucore.OusuCore;
import me.skiincraft.ousucore.language.Language;
import me.skiincraft.ousubot.OusuBot;
import me.skiincraft.ousubot.view.emotes.GenericsEmotes;
import me.skiincraft.ousubot.view.token.ClassToken;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MessageModel {

    protected final Map<String, ClassToken<?>> tokenMap = new HashMap<>();
    private final Embed embed;
    private Language language;

    public MessageModel(String name, Language language) {
        this.language = language;
        InputStreamReader reader = reader(OusuBot.class.getResourceAsStream("/" + name + ".json"));
        this.embed = new Gson().fromJson(new JsonParser().parse(reader)
                .getAsJsonObject()
                .get("embeds").getAsJsonArray().get(0), Embed.class);
        this.addProperty("emotes", OusuCore.getInjector().getInstanceOf(GenericsEmotes.class));
        try {
            reader.close();
        } catch (IOException ignored) {}
    }

    public Embed getEmbed() {
        return embed;
    }

    public <T> void addProperty(String name, T item) {
        tokenMap.put(name.toLowerCase(), new ClassToken<>(item));
    }

    public <T> void removeProperty(T item) {
        for (Map.Entry<String, ClassToken<?>> itens : tokenMap.entrySet()) {
            if (item == itens.getValue()) {
                tokenMap.remove(itens.getKey());
                break;
            }
        }
    }

    public void removeProperty(String name) {
        tokenMap.remove(name.toLowerCase());
    }

    public GenericsEmotes getEmotes() {
        return (GenericsEmotes) tokenMap.get("emotes").getItem();
    }

    public EmbedBuilder getEmbedBuilder() {
        return embed.toMessageEmbed(language, tokenMap);
    }

    public Language getLanguage() {
        return language;
    }

    public MessageModel setLanguage(Language language) {
        this.language = language;
        return this;
    }

    private InputStreamReader reader(InputStream inputStream) {
        return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    }

}
