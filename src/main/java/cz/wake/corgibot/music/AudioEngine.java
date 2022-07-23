package cz.wake.corgibot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.TimeUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AudioEngine {

    private static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private static final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

    static {
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public synchronized static GuildMusicManager getGuildAudioPlayer(Guild guild) {

        GuildMusicManager musicManager = musicManagers.get(guild.getIdLong());

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager, guild);
            musicManagers.put(guild.getIdLong(), musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public static void getSong(TextChannel channel) {

        AudioTrack track = getGuildAudioPlayer(channel.getGuild()).player.getPlayingTrack();
        GuildWrapper gw = BotManager.getCustomGuild(channel.getGuild().getId());

        if (track == null) {
            channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                    .setTitle(I18n.getLoc(gw, "commands.music.embed-title"))
                    .setDescription("Nothing is currently playing!")
                    .build()).queue();
            return;
        }

        long duration = track.getDuration() / 1000;

        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                .setTitle(I18n.getLoc(gw, "commands.music.embed-title"))
                .setDescription(String.format(I18n.getLoc(gw, "commands.music.now-playing"), track.getInfo().title) + "\n:hourglass_flowing_sand: " + TimeUtils.secondsToTime(duration, gw))
                .build()).queue();

    }

    public static void changeVolume(TextChannel channel, int volume) {

        GuildWrapper gw = BotManager.getCustomGuild(channel.getGuild().getId());

        if (volume > 100)
            volume = 100;
        else if (volume < 1)
            volume = 1;

        getGuildAudioPlayer(channel.getGuild()).player.setVolume(volume);

        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                .setTitle(I18n.getLoc(gw, "commands.music-volume.embed-title"))
                .setDescription(String.format(I18n.getLoc(gw, "commands.music-volume.changed"), volume))
                .build()).queue();

    }

    public static void loadAndPlay(Member member, MessageChannel channel, AudioChannel voice, String trackUrl) {

        GuildMusicManager musicManager = getGuildAudioPlayer(voice.getGuild());
        GuildWrapper gw = BotManager.getCustomGuild(voice.getGuild().getId());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {

                long duration = track.getDuration() / 1000;

                if (duration > 600 && !PermissionUtil.checkPermission(member, Permission.MANAGE_CHANNEL)) {
                    channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                            .setTitle(I18n.getLoc(gw, "commands.music-volume.embed-title"))
                            .setDescription(String.format(I18n.getLoc(gw, "commands.music.max-length"), "10"))
                            .build()).queue();
                    return;

                } else {
                    channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                            .setTitle(I18n.getLoc(gw, "commands.music-volume.embed-title"))
                            .setDescription(String.format(I18n.getLoc(gw, "commands.music.added-track"), track.getInfo().title) + "**\n:hourglass_flowing_sand: " + TimeUtils.secondsToTime(duration, gw))
                            .build()).queue();
                }

                play(voice.getGuild(), voice, musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {

                long totalDuration = 0;
                long totalTracks = 0;
                long failedTracks = 0;

                if (playlist.getTracks().size() > 1000) {
                    channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                            .setTitle(I18n.getLoc(gw, "commands.music-volume.embed-title"))
                            .setDescription(String.format(I18n.getLoc(gw, "commands.music.playlist-limit"), "1000"))
                            .build()).queue();
                    playlist.getTracks().clear();
                    return;
                }

                for (AudioTrack track : playlist.getTracks()) {

                    if (track.getDuration() > 600 && !PermissionUtil.checkPermission(member, Permission.MANAGE_CHANNEL)) {
                        playlist.getTracks().remove(track);
                        failedTracks++;
                    } else {
                        totalDuration = totalDuration + track.getDuration();
                        totalTracks++;

                        play(voice.getGuild(), voice, musicManager, track);
                    }
                }

                totalDuration = totalDuration / 1000;

                channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                        .setTitle(I18n.getLoc(gw, "commands.music-volume.embed-title"))
                        .setDescription(String.format(I18n.getLoc(gw, "commands.music.added-playlist"), playlist.getName()) +  "**\n:hourglass_flowing_sand: " + TimeUtils.secondsToTime(totalDuration, gw) + "\n" +
                                ":headphones: " + String.format(I18n.getLoc(gw, "commands.music.track-amount"), totalTracks))
                        .build()).queue();

                if (failedTracks > 0) {
                    channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                            .setTitle(I18n.getLoc(gw, "commands.music-volume.embed-title"))
                            .setDescription(String.format(I18n.getLoc(gw, "commands.music.tracks-not-loaded-limit"), failedTracks, "10"))
                            .build()).queue();
                }
            }

            @Override
            public void noMatches() {
                channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                        .setTitle(I18n.getLoc(gw, "commands.music-volume.embed-title"))
                        .setDescription(I18n.getLoc(gw, "commands.music.no-track-found"))
                        .build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException ex) {
                channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                        .setTitle(I18n.getLoc(gw, "commands.music-volume.embed-title"))
                        .setDescription(String.format(I18n.getLoc(gw, "commands.music.track-failed"), ex.getMessage()))
                        .build()).queue();
            }
        });
    }


    public static void play(Guild guild, AudioChannel voice, GuildMusicManager musicManager, AudioTrack track) {

        connectVoice(guild.getAudioManager(), voice);
        musicManager.scheduler.queue(track);
    }

    public static void skipTrack(TextChannel channel) {

        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        GuildWrapper gw = BotManager.getCustomGuild(channel.getGuild().getId());

        if (musicManager.player.getPlayingTrack() == null) {
            channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                    .setTitle(I18n.getLoc(gw, "commands.music-volume.embed-title"))
                    .setDescription(I18n.getLoc(gw, "commands.music.nothing-to-skip"))
                    .build()).queue();
            return;
        }

        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                .setTitle(I18n.getLoc(gw, "commands.music-volume.embed-title"))
                .setDescription(I18n.getLoc(gw, "commands.music.skipped"))
                .build()).queue();
        musicManager.scheduler.nextTrack();
    }

    public static void stop(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        GuildWrapper gw = BotManager.getCustomGuild(channel.getGuild().getId());

        musicManager.scheduler.getQueue().clear();
        musicManager.player.destroy();
        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                .setTitle(I18n.getLoc(gw, "commands.music-volume.embed-title"))
                .setDescription(I18n.getLoc(gw, "commands.music.stopped"))
                .build()).queue();
    }

    public static void connectVoice(AudioManager audioManager, AudioChannel voice) {

        if (audioManager.isConnected()) {
            if (!Objects.equals(audioManager.getConnectedChannel(), voice)) {
                audioManager.closeAudioConnection();
            }
        }
        audioManager.openAudioConnection(voice);
        audioManager.setSelfDeafened(true);
    }
}