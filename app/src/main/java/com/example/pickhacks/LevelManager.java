package com.example.pickhacks;

import android.util.Log;

import com.google.ar.sceneform.collision.Box;

import java.util.ArrayList;
import java.util.List;

public class LevelManager
{
    private List levels;
    private Box[] currentLevel;
    private int levelNum = 0;

    public LevelManager()
    {
        levels = new ArrayList<Box[]>();

    }

    public void IncreaseLevel()
    {
        levelNum++;

        currentLevel = (Box[])levels.get(levelNum - 1);
    }
    public void DecreaseLevel()
    {
        levelNum--;
        if (levelNum <= 0)
        {
            levelNum = levels.size();
        }
        currentLevel = (Box[])levels.get(levelNum - 1);
    }
    public List<Box[]> getAllLevels()
    {
        return levels;
    }
    public Box[] getLevel(int index)
    {
        return (Box[])levels.get(index);
    }

    public Box[] getCurrentLevel()
    {
        return currentLevel;
    }
    public void addLevel(Box[] level)
    {
        levels.add(level);
    }
    public void setLevel(int index)
    {
        levelNum = index;
        Log.d("RATIO", "" + levelNum);
        currentLevel = (Box[])levels.get(index);
    }
    public int getLevelNum()
    {
        return levelNum;
    }







}
