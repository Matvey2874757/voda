package com.example.vodateleport;

/**
 * A saved teleport point.
 */
public record SavedPosition(String name, double x, double y, double z) {
    @Override
    public String toString() {
        return "%s [%.2f, %.2f, %.2f]".formatted(name, x, y, z);
    }
}
