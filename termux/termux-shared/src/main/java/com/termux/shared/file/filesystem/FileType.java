package com.termux.shared.file.filesystem;

/**
 * The {@link Enum} that defines file types.
 */
public enum FileType {

    // 0000000
    NO_EXIST("no exist", 0),
    // 0000001
    REGULAR("regular", 1),
    // 0000010
    DIRECTORY("directory", 2),
    // 0000100
    SYMLINK("symlink", 4),
    // 0001000
    CHARACTER("character", 8),
    // 0010000
    FIFO("fifo", 16),
    // 0100000
    BLOCK("block", 32),
    // 1000000
    UNKNOWN("unknown", 64);

    private final String name;

    private final int value;

    FileType(final String name, final int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}

