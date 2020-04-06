package skeleton;

import util.CubicBezierCurve;

import javax.vecmath.Point2d;
import javax.vecmath.Point2f;
import javax.vecmath.Tuple2f;
import java.util.List;
import java.util.Random;

public class SpineData {

    public static float vertebraYZScale = 10f;

    private CubicBezierCurve neck;
    private CubicBezierCurve back;
    private CubicBezierCurve tail;

    public static float maxRibYScale = 100f;
    private Tuple2f chestIntervalOnBack;

    public SpineData(List<Point2d> spinePoints) {
        if (spinePoints.size() != 10) {
            System.err.println("Cannot create spine!");
        }
        this.neck = new CubicBezierCurve(new Point2f(spinePoints.get(0)), new Point2f(spinePoints.get(1)), new Point2f(spinePoints.get(2)), new Point2f(spinePoints.get(3)));
        this.back = new CubicBezierCurve(new Point2f(spinePoints.get(3)), new Point2f(spinePoints.get(4)), new Point2f(spinePoints.get(5)), new Point2f(spinePoints.get(6)));
        this.tail = new CubicBezierCurve(new Point2f(spinePoints.get(6)), new Point2f(spinePoints.get(7)), new Point2f(spinePoints.get(8)), new Point2f(spinePoints.get(9)));
        this.chestIntervalOnBack = new Point2f(0f, (new Random().nextFloat())*2/3f);
        System.out.println("Chest interval: " + chestIntervalOnBack);
    }

    public CubicBezierCurve getPart(SpinePart spinePart) {
        switch (spinePart) {
            case NECK:
                return getNeck();
            case TAIL:
                return getTail();
            default: // BACK
                return getBack();
        }
    }

    public CubicBezierCurve[] getAll() {
        return new CubicBezierCurve[] {neck, back, tail};
    }

    public CubicBezierCurve getNeck() {
        return neck;
    }

    public CubicBezierCurve getBack() {
        return back;
    }

    public CubicBezierCurve getTail() {
        return tail;
    }

    public boolean isInChestInterval(float backSpinePosition) {
        return backSpinePosition <= chestIntervalOnBack.y;
    }

    /**
     * ! This function assumes, that the chest interval starts at 0 !
     * chest function: -[(1.5 * 1/intervalWidth * x - 0.8)^4 - 1] (only in chest interval)
     * chest function(backSpinePosition) * maxRibYScale = rib y length
     */
    public float getRibLength(float backSpinePosition) {
        if (!isInChestInterval(backSpinePosition)) {
            return 0;
        } else {
            return (float) -(Math.pow(1.8f / chestIntervalOnBack.y * backSpinePosition - 0.9f, 4) - 1f) * maxRibYScale;
        }
    }
}
