package com.example.vanishclient;

public final class VanishState {
    private static boolean localVisualHide;
    private static boolean packetDesync;

    private VanishState() {
    }

    public static boolean isLocalVisualHide() {
        return localVisualHide;
    }

    public static boolean isPacketDesync() {
        return packetDesync;
    }

    public static void setLocalVisualHide(boolean value) {
        localVisualHide = value;
    }

    public static void setPacketDesync(boolean value) {
        packetDesync = value;
    }
}
