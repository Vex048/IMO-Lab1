package instance;

public class Node {
    private final int x;
    private final int y;
    private final int reward;

    public Node(int x, int y, int reward) {
        this.x = x;
        this.y = y;
        this.reward = reward;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getReward() { return reward; }
}
