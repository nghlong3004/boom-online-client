package com.vn.nghlong3004.client.util;

import static com.vn.nghlong3004.client.constant.AudioConstant.CLICK;
import static com.vn.nghlong3004.client.constant.AudioConstant.VOLUME_START;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sound.sampled.*;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
public class AudioUtil {
  private volatile Clip[] songs, effects;
  private volatile int currentSongId = 0;
  private volatile float volume = VOLUME_START;
  private final AtomicBoolean songMute;
  private final AtomicBoolean effectMute;
  private volatile boolean wasPlayingBeforeMute;

  private final ExecutorService audioExec =
      Executors.newSingleThreadExecutor(
          new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
              Thread t = new Thread(r, "audio-exec");
              t.setDaemon(true);
              return t;
            }
          });

  public AudioUtil() {
    songMute = new AtomicBoolean(true);
    effectMute = new AtomicBoolean(false);
    loadSongs();
    loadEffects();
    audioExec.execute(
        () -> {
          safeUpdateSongVolume();
          safeUpdateEffectsVolume();
        });
    Runtime.getRuntime().addShutdownHook(new Thread(this::close, "audio-shutdown"));
  }

  private void loadSongs() {
    String[] names = {
      "desert", "land", "town", "underwater", "xmas", "soundMenu", "soundGame", "online"
    };
    songs = new Clip[names.length];
    for (int i = 0; i < songs.length; i++) {
      songs[i] = getClip(names[i]);
    }
  }

  private void loadEffects() {
    String[] effectNames = {
      "move",
      "set_boom",
      "start",
      "click",
      "boom_bang",
      "item",
      "win",
      "touch",
      "lose",
      "win",
      "eat_item"
    };
    effects = new Clip[effectNames.length];
    for (int i = 0; i < effects.length; i++) {
      effects[i] = getClip(effectNames[i]);
    }
  }

  private Clip getClip(String name) {
    URL url = getClass().getResource("/sounds/" + name + ".wav");
    if (url == null) {
      throw new RuntimeException("Audio file not found: " + name);
    }
    try (AudioInputStream audio = AudioSystem.getAudioInputStream(url)) {
      Clip c = AudioSystem.getClip();
      c.open(audio);
      return c;
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
      throw new RuntimeException("Cannot load audio: " + name, e);
    }
  }

  public void setVolume(float volume) {
    if (volume < 0f) {
      volume = 0f;
    }
    if (volume > 1f) {
      volume = 1f;
    }
    this.volume = volume;
    if (audioExec.isShutdown()) {
      return;
    }
    audioExec.execute(
        () -> {
          safeUpdateSongVolume();
          safeUpdateEffectsVolume();
        });
  }

  public void stopSong() {
    if (audioExec.isShutdown()) {
      return;
    }
    audioExec.execute(
        () -> {
          Clip c = songs[currentSongId];
          if (c != null && c.isActive()) {
            c.stop();
            c.setMicrosecondPosition(0);
          }
        });
  }

  public void playEffect(int effect) {

    if (audioExec.isShutdown()) {
      return;
    }
    audioExec.execute(
        () -> {
          if (effect < 0 || effect >= effects.length) {
            return;
          }
          Clip c = effects[effect];
          if (c == null) {
            return;
          }
          if (c.isActive()) {
            c.stop();
          }
          c.setMicrosecondPosition(0);
          setClipMuteIfSupported(c, effectMute.get());
          c.start();
        });
  }

  public void playSong(int song) {
    if (audioExec.isShutdown()) {
      return;
    }
    audioExec.execute(
        () -> {
          if (song < 0 || song >= songs.length) {
            return;
          }
          Clip cur = songs[currentSongId];
          if (cur != null && cur.isActive()) {
            cur.stop();
          }

          currentSongId = song;
          Clip next = songs[currentSongId];
          if (next == null) {
            return;
          }

          safeUpdateSongVolume();
          setClipMuteIfSupported(next, songMute.get());

          next.setMicrosecondPosition(0);
          next.loop(Clip.LOOP_CONTINUOUSLY);
          wasPlayingBeforeMute = !songMute.get();
        });
  }

  public void toggleSongMute() {
    if (audioExec.isShutdown()) {
      return;
    }
    audioExec.execute(
        () -> {
          songMute.set(!songMute.get());
          Clip cur = songs[currentSongId];
          if (cur == null) {
            return;
          }

          if (songMute.get()) {
            wasPlayingBeforeMute = cur.isActive();
            setClipMuteIfSupported(cur, true);
            if (cur.isActive()) {
              cur.stop();
            }
          } else {
            setClipMuteIfSupported(cur, false);
            if (wasPlayingBeforeMute) {
              cur.setMicrosecondPosition(0);
              cur.loop(Clip.LOOP_CONTINUOUSLY);
            }
          }
        });
  }

  public void toggleEffectMute() {
    if (audioExec.isShutdown()) {
      return;
    }
    audioExec.execute(
        () -> {
          effectMute.set(!effectMute.get());
          for (Clip c : effects) {
            setClipMuteIfSupported(c, effectMute.get());
          }
          if (!effectMute.get()) {
            playEffect(CLICK);
          }
        });
  }

  public boolean isSongMuted() {
    return songMute.get();
  }

  public boolean isEffectMuted() {
    return effectMute.get();
  }

  public void close() {
    try {
      audioExec.shutdownNow();
    } catch (Exception ignored) {
    }
    safeCloseClips(songs);
    safeCloseClips(effects);
  }

  private void safeUpdateSongVolume() {
    Clip c = songs[currentSongId];
    if (c != null) {
      setClipVolumeIfSupported(c, volume);
    }
  }

  private void safeUpdateEffectsVolume() {
    for (Clip c : effects) {
      setClipVolumeIfSupported(c, volume);
    }
  }

  private static void setClipMuteIfSupported(Clip c, boolean mute) {
    if (c == null) {
      return;
    }
    if (c.isControlSupported(BooleanControl.Type.MUTE)) {
      BooleanControl bc = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
      bc.setValue(mute);
    }
  }

  private static void setClipVolumeIfSupported(Clip c, float vol01) {
    if (c == null) {
      return;
    }
    if (c.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
      FloatControl gc = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
      float dB;
      if (vol01 <= 0f) {
        dB = gc.getMinimum();
      } else {
        dB = (float) (20.0 * Math.log10(vol01));
        if (dB < gc.getMinimum()) {
          dB = gc.getMinimum();
        }
        if (dB > gc.getMaximum()) {
          dB = gc.getMaximum();
        }
      }
      gc.setValue(dB);
    } else if (c.isControlSupported(FloatControl.Type.VOLUME)) {
      FloatControl vc = (FloatControl) c.getControl(FloatControl.Type.VOLUME);
      vc.setValue(Math.max(0f, Math.min(1f, vol01)));
    }
  }

  private static void safeCloseClips(Clip[] arr) {
    if (arr == null) {
      return;
    }
    for (Clip c : arr) {
      if (c == null) {
        continue;
      }
      try {
        if (c.isActive()) {
          c.stop();
        }
        c.drain();
        c.close();
      } catch (Exception ignored) {
      }
    }
  }
}
