package uk.thecodingbadgers.minekart.util;

/**
 * Created by James on 30/04/2014.
 */
public final class MinecraftTime {

    private static final long TICK_LENGTH = 20L;

    private MinecraftTime() {}

    public static long fromSeconds(long seconds) {
        return seconds * TICK_LENGTH;
    }

}
