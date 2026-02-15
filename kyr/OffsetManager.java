package kyr;

public class OffsetManager {
    private static final OffsetManager instance = new OffsetManager();
    private int currentOffset;

    private OffsetManager() {
        this.currentOffset = 0;
    }

    public static OffsetManager getInstance() {
        return instance;
    }

    public  int allocate() {
        int offset = currentOffset;
        currentOffset -= 4;  // 4 bytes for a bool or int
        return offset;
    }

    public void reset() {
        currentOffset = 0;
    }

    public int getCurrentOffset() {
        return currentOffset ;
    }
}