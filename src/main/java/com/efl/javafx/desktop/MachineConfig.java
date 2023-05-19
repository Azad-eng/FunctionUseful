package com.efl.javafx.desktop;


import java.awt.*;

/**
 * @author EFL-tjl
 */
public class MachineConfig {

    private final static double[] MIN = {0, 0, 0};
    private final static double[] MAX = {48, 27, 60};

    private static int[] gridNum = new int[]{1, 1};

    public final static int X_DIR = 0, Y_DIR = 1, Z_DIR = 2;

    private static boolean pro = false;

    public static boolean clipPrint = false;
    public static boolean comboPrint = false;

    private static int type = 8601;
    private static int projectWidth = 1920, projectHeight = 1080;

    public final static boolean ENABLE_REGIONS_PRINT = true;

    private static boolean showPlatform = true;

    public static double maxIntensity = 20;

    /**
     * 0=EFL 1=Admin 2=User
     */
    public static int USER = 2;


}
