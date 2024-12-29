package dev.idank.r2d2.git;

public enum Platform {
    GITLAB("(gitlab)"),
    GITHUB("(github)");

    private final String name;

    Platform(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Platform fromName(String name) {
        for (Platform platform : Platform.values()) {
            if (platform.getName().equalsIgnoreCase(name))
                return platform;
        }

        throw new IllegalArgumentException("Unknown platform name: " + name);
    }

}
