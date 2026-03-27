package com.ramis.progresstracker.util;

public class CalculationUtils {

    /**
     * Вычисли уровень на основе XP
     */
    public static int calculateLevel(int totalXP) {
        return (totalXP / Constants.XP_PER_LEVEL) + 1;
    }

    /**
     * Вычисли XP до следующего уровня
     */
    public static int getXPToNextLevel(int level) {
        return Constants.XP_PER_LEVEL;
    }

    /**
     * Вычисли процент до следующего уровня
     */
    public static double getProgressPercentage(int totalXP, int level) {
        int currentLevelXP = (level - 1) * Constants.XP_PER_LEVEL;
        int xpInCurrentLevel = totalXP - currentLevelXP;
        return (xpInCurrentLevel * 100.0) / Constants.XP_PER_LEVEL;
    }

    /**
     * Применяй множитель за streak
     */
    public static int applyStreakBonus(int baseXP, int streakDays) {
        double bonus = 1.0 + (Math.min(streakDays, 30) * 0.01); // Max 30% bonus
        return (int) (baseXP * bonus);
    }

    /**
     * Вычисли точность
     */
    public static double calculateAccuracy(int correct, int total) {
        if (total == 0) return 0;
        return (correct * 100.0) / total;
    }

    /**
     * Нормализуй значение (0-100)
     */
    public static double normalize(double value) {
        return Math.max(0, Math.min(100, value));
    }

}
