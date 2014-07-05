package com.cwt.penrose.misc;

/**
 * Created by max on 7/5/14.
 */
public class HexPoint {
    public int r, g, b;

    public HexPoint() {
        r = g = b = 0;
    }

    public HexPoint(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public boolean equals(Object obj) {
        HexPoint that = (HexPoint) obj;

        return that.r == r && that.g == g && that.b == b;
    }
}
