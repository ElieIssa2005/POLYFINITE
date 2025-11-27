package com.eliemichel.polyfinite.game;

/**
 * Represents a star reward milestone tied to reaching a specific wave.
 */
public class WaveMilestone {

    private int wave;
    private int starsReward;

    public WaveMilestone(int wave, int starsReward) {
        this.wave = wave;
        this.starsReward = starsReward;
    }

    public int getWave() {
        return wave;
    }

    public void setWave(int wave) {
        this.wave = wave;
    }

    public int getStarsReward() {
        return starsReward;
    }

    public void setStarsReward(int starsReward) {
        this.starsReward = starsReward;
    }

    public boolean isReached(int currentWave) {
        return currentWave >= wave;
    }
}
