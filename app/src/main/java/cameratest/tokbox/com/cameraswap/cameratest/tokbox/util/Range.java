package cameratest.tokbox.com.cameraswap.cameratest.tokbox.util;

/**
 * Created by ardy on 3/3/16.
 */
public class Range {
    private int mLowerBound;
    private int mUpperBound;

    public Range(int lowerBound, int upperBound) {
        mLowerBound = lowerBound;
        mUpperBound = upperBound;
    }

    public int getLowerBound() {
        return mLowerBound;
    }

    public int getmUpperBound() {
        return mUpperBound;
    }
}
