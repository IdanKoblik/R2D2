package dev.idank.r2d2;

@FunctionalInterface
public interface LineValidator {
    boolean isValid(String line);
}
