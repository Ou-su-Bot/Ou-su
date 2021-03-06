package me.skiincraft.ousubot.view.emotes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.skiincraft.beans.annotation.Component;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class GenericsEmotes {

    private final List<GenericEmote> emotes = new ArrayList<>();

    public void loadEmotes(String path) {
        try {
            loadEmotes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadEmotes(Path path) throws IOException {
        if (!Files.exists(path)) {
            path.toFile().mkdir();
        }

        emotes.clear();
        AtomicInteger loaded = new AtomicInteger();
        DirectoryStream<Path> stream = Files.newDirectoryStream(path);
        stream.forEach(path1 -> {
            if (path1.toFile().getName().toLowerCase().contains(".emotejson")) {
                try {
                    Gson gson = new Gson();
                    GenericEmote[] genericEmotes = gson.fromJson(new FileReader(path1.toFile()), GenericEmote[].class);
                    emotes.addAll(Arrays.asList(genericEmotes));
                    loaded.getAndIncrement();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        stream.close();
        System.out.println(loaded + " Emote Pack carregados com sucesso.");
    }

    public List<GenericEmote> parseEmotes(List<Emote> emotes) {
        return emotes.stream().map(emote -> new GenericEmote(emote.getName(), emote.getIdLong(), Objects.requireNonNull(emote.getGuild(), "Guild is null").getIdLong(), emote.isAnimated()))
                .collect(Collectors.toList());
    }

    public List<GenericEmote> parseEmotes(Guild guild) {
        return parseEmotes(guild.getEmotes());
    }

    public List<GenericEmote> getEmotes() {
        return emotes;
    }

    public GenericEmote getEmoteEquals(String nameequals) {
        return emotes.stream()
                .filter(emote -> emote.getName().equalsIgnoreCase(nameequals))
                .findFirst().orElse(getEmotes().get(0));
    }

    public String getEmoteAsMentionEquals(String nameequals) {
        return emotes.stream()
                .filter(emote -> emote.getName().equalsIgnoreCase(nameequals))
                .map(GenericEmote::getAsMention)
                .findFirst().orElse(getEmotes().get(0).getAsMention());
    }

    @Nullable
    public GenericEmote getEmote(String name) {
        return emotes.stream()
                .filter(emote -> emote.getName().contains(name))
                .findFirst().orElse(null);
    }

    public String getEmoteAsMention(String name) {
        return emotes.stream()
                .filter(emote -> emote.getName().contains(name))
                .map(GenericEmote::getAsMention)
                .findFirst().orElse(getEmotes().get(0).getAsMention());
    }

    public void saveEmotes(String path, List<GenericEmote> emotes) {
        try {
            Path locale = Paths.get(path);
            if (!Files.exists(locale)) {
                Files.createDirectories(locale);
            }
            File file = new File(locale + "/" + emotes.get(0).getGuildId() + ".emotejson");
            if (!file.exists()) {
                Files.createFile(Paths.get(file.getAbsolutePath()));
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(emotes));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
