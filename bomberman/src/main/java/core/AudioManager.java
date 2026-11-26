package core;

import java.util.EnumMap;
import java.util.Map;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/** Loads and controls the game's background music and sound effects. */
public final class AudioManager {
    public enum Effect {
        STAGE_START("/sound/Stage start sound/05. Stage Start.mp3"),
        BOMB_EXPLOSION("/sound/bomb sound/Bomb Explodes PCM.wav"),
        LIFE_LOST("/sound/life lost sound/23. Life Lost (Unused Alternate).mp3"),
        LEVEL_COMPLETE("/sound/level complete sound/15. Level Complete.mp3"),
        GAME_OVER("/sound/Game over sound/25. Game Over (Unused Alternate).mp3"),
        FINAL_WIN("/sound/final win sound/16. Ending (Part 1).mp3");

        private final String resource;

        Effect(String resource) {
            this.resource = resource;
        }
    }

    private static final String MENU_MUSIC = "/sound/Main sound/01. Main Title.mp3";
    private static final String[] LEVEL_MUSIC = {
            "/sound/lv1 theme sound/06. Level Theme 1.mp3",
            "/sound/lv2 theme sound/07. Level Theme 2.mp3",
            "/sound/lv3 theme sound/08. Level Theme 3.mp3"
    };

    private static final Map<Effect, AudioClip> EFFECTS = new EnumMap<>(Effect.class);
    private static MediaPlayer musicPlayer;
    private static String currentMusic;

    private AudioManager() {}

    public static void playMenuMusic() {
        playMusic(MENU_MUSIC);
    }

    public static void playLevelMusic(int level) {
        if (level < 1 || level > LEVEL_MUSIC.length) {
            stopMusic();
            return;
        }
        playMusic(LEVEL_MUSIC[level - 1]);
    }

    public static void playEffect(Effect effect) {
        try {
            AudioClip clip = EFFECTS.get(effect);
            if (clip == null) {
                var resource = AudioManager.class.getResource(effect.resource);
                if (resource == null) {
                    System.err.println("Không tìm thấy sound: " + effect.resource);
                    return;
                }
                clip = new AudioClip(resource.toExternalForm());
                clip.setVolume(0.8);
                EFFECTS.put(effect, clip);
            }
            clip.play();
        } catch (RuntimeException e) {
            // Audio must never interrupt gameplay logic if a codec or media device fails.
            System.err.println("Không phát được sound " + effect + ": " + e.getMessage());
        }
    }

    public static void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.dispose();
            musicPlayer = null;
            currentMusic = null;
        }
    }

    public static void stopAll() {
        stopMusic();
        EFFECTS.values().forEach(AudioClip::stop);
    }

    private static void playMusic(String resourcePath) {
        if (resourcePath.equals(currentMusic) && musicPlayer != null
                && musicPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }
        stopMusic();
        var resource = AudioManager.class.getResource(resourcePath);
        if (resource == null) {
            System.err.println("Không tìm thấy nhạc: " + resourcePath);
            return;
        }
        MediaPlayer nextPlayer = new MediaPlayer(new Media(resource.toExternalForm()));
        musicPlayer = nextPlayer;
        currentMusic = resourcePath;
        nextPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        nextPlayer.setVolume(0.35);
        nextPlayer.setOnError(() -> System.err.println("Lỗi phát nhạc: " + nextPlayer.getError()));
        nextPlayer.play();
    }
}
