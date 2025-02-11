package de.blockbuild.musikbot.core;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.blockbuild.musikbot.Bot;
import de.blockbuild.musikbot.configuration.GuildConfiguration;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

public class GuildMusicManager {
	private final AudioPlayer player;
	private final TrackScheduler trackScheduler;
	public final GuildConfiguration config;
	private final Bot bot;
	private final Guild guild;
	public List<AudioTrack> tracks;
	private Boolean isQueue, autoPlay;

	public GuildMusicManager(AudioPlayerManager playerManager, Guild guild, Bot bot) {
		this.bot = bot;
		this.guild = guild;
		this.player = playerManager.createPlayer();
		this.trackScheduler = new TrackScheduler(bot, this);
		this.player.addListener(trackScheduler);
		this.autoPlay = false;
		this.config = new GuildConfiguration(bot, this);

		if (config.isAutoConnectEnabled()) {
			if (config.getAutoConnectVoiceChannelId() == 0) {
				bot.joinDiscordVoiceChannel(guild);
			} else {
				if (!bot.joinDiscordVoiceChannel(guild, config.getAutoConnectVoiceChannelId())
						&& config.isDefaultTextChannelEnabled()) {
					TextChannel channel = bot.getTextChannelById(config.getDefaultTextChannel());
					StringBuilder builder = new StringBuilder();
					builder.append(" Missing permission or there is no channel called `")
							.append(config.getDefaultTextChannel()).append("`.");
					channel.sendMessage(builder.toString());

				}
			}
			if (!(config.getAutoConnectTrack() == null)) {
				playerManager.loadItemOrdered(playerManager, config.getAutoConnectTrack(),
						new BasicResultHandler(this.getAudioPlayer(), null, null));
			}
		}
	}

	public Guild getGuild() {
		return this.guild;
	}

	public AudioPlayerSendHandler getSendHandler() {
		return new AudioPlayerSendHandler(player, bot);
	}

	public TrackScheduler getTrackScheduler() {
		return this.trackScheduler;
	}

	public AudioPlayer getAudioPlayer() {
		return this.player;
	}

	public void setVolume(int volume) {
		player.setVolume(volume);
		config.setVolume(volume);
	}

	public int getVolume() {
		return player.getVolume();
	}

	public Boolean isQueue() {
		return this.isQueue;
	}

	public void setIsQueue(boolean bool) {
		this.isQueue = bool;
	}

	public Boolean isAutoPlay() {
		return this.autoPlay;
	}

	public void setIsAutoPlay(boolean bool) {
		this.autoPlay = bool;
	}
}
