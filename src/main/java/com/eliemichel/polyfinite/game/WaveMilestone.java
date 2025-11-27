package com.eliemichel.polyfinite.game;

/**
 * Represents a star reward milestone tied to reaching a specific wave.
 */
public class WaveMilestone {

    public static final int STAR_REWARD = 1;

    private int wave;
    private int starsReward;

    public WaveMilestone(int wave, int starsReward) {
        this.wave = wave;
        this.starsReward = STAR_REWARD;
    }

    public WaveMilestone(int wave) {
        this.wave = wave;
        this.starsReward = STAR_REWARD;
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
        this.starsReward = STAR_REWARD;
    }

    public boolean isReached(int currentWave) {
        return currentWave >= wave;
    }

    public static java.util.ArrayList<WaveMilestone> normalize(java.util.ArrayList<WaveMilestone> raw) {
        java.util.ArrayList<WaveMilestone> normalized = new java.util.ArrayList<>();

        if (raw != null) {
            for (WaveMilestone milestone : raw) {
                if (milestone != null && milestone.getWave() > 0) {
                    normalized.add(new WaveMilestone(milestone.getWave()));
                }
            }
        }

        normalized.sort(java.util.Comparator.comparingInt(WaveMilestone::getWave));

        int[] defaultWaves = new int[]{5, 10, 20};
        int defaultIndex = 0;
        while (normalized.size() < 3 && defaultIndex < defaultWaves.length) {
            normalized.add(new WaveMilestone(defaultWaves[defaultIndex++]));
        }

        while (normalized.size() < 3) {
            int nextWave = normalized.get(normalized.size() - 1).getWave() + 5;
            normalized.add(new WaveMilestone(nextWave));
        }

        if (normalized.size() > 3) {
            normalized = new java.util.ArrayList<>(normalized.subList(0, 3));
        }

        return normalized;
    }
}
