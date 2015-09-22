package io.github.aritzhack._2048;

import com.google.common.base.Preconditions;

/**
 * @author Aritz Lopez
 */
public class Mat4i {

    private int[] values = new int[16];

    private Mat4i() {}

    public static Mat4i empty() {
        return new Mat4i();
    }

    public static Mat4i identity() {
        return diagonal(1);
    }

    public static Mat4i diagonal(int diag) {
        Mat4i m = empty();
        m.values[0 + 0*4] = diag;
        m.values[1 + 1*4] = diag;
        m.values[2 + 2*4] = diag;
        m.values[3 + 3*4] = diag;
        return m;
    }

    public static Mat4i full(int value) {
        Mat4i m = empty();
        for (int i = 0; i<m.values.length; i++) m.values[i] = value;
        return m;
    }

    public static Mat4i fromArray(int[] values) {
        Preconditions.checkArgument(values.length == 16, "Values length is not 16!");

        Mat4i m = empty();
        m.values = values;
        return m;
    }

    public int get(int x, int y) {
        Preconditions.checkArgument(x<4 && x>=0, "Coordinates must be between 0 and 3, both inclusive!");
        Preconditions.checkArgument(y<4 && y>=0, "Coordinates must be between 0 and 3, both inclusive!");
        return get(x + y*4);
    }

    public int get(int index) {
        Preconditions.checkArgument(index<16 && index>=0, "Index must be between 0 and 15, both inclusive!");
        return values[index];
    }

    public void set(int x, int y, int value) {
        Preconditions.checkArgument(x<4 && x>=0, "Coordinates must be between 0 and 3, both inclusive!");
        Preconditions.checkArgument(y<4 && y>=0, "Coordinates must be between 0 and 3, both inclusive!");
        this.set(x + y*4, value);
    }

    public void set(int index, int value) {
        Preconditions.checkArgument(index<16 && index>=0, "Index must be between 0 and 15, both inclusive!");
        this.values[index] = value;
    }
}
