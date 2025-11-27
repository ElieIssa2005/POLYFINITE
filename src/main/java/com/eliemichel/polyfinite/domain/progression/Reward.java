package com.eliemichel.polyfinite.domain.progression;

public class Reward {
    private RewardType type;
    private int amount;

    public Reward(RewardType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public RewardType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public String getDisplayString() {
        return amount + " " + type.getDisplayName();
    }
}
