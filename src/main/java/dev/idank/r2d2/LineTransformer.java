package dev.idank.r2d2;

@FunctionalInterface
public interface LineTransformer {
    String transform(String line);
}