package com.eliemichel.polyfinite.game;

import java.util.ArrayList;

public class Chapter {
    private int chapterNumber;
    private String chapterName;
    private ArrayList<LevelInfo> levels;

    public Chapter(int chapterNumber, String chapterName) {
        this.chapterNumber = chapterNumber;
        this.chapterName = chapterName;
        this.levels = new ArrayList<>();
    }

    public void addLevel(LevelInfo level) {
        levels.add(level);
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public String getChapterName() {
        return chapterName;
    }

    public ArrayList<LevelInfo> getLevels() {
        return levels;
    }
}