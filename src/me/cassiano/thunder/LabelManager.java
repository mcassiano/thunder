package me.cassiano.thunder;


public class LabelManager {

    private static final LabelManager instance = new LabelManager();
    private int counter = 0;

    private LabelManager() {
    }

    public static LabelManager get() {
        return instance;
    }

    private int increment() {
        return ++counter;
    }

    public void reset() {
        counter = 0;
    }

    public String newLabel() {
        return String.format("Label%d", increment());
    }
}
