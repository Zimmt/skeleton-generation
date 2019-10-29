package util;

public class Position {
    private float x;
    private float y;
    private float z;

    public Position(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String objData() {
        return String.format("v %f %f %f", x, y, z);
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float z() {
        return z;
    }
}
