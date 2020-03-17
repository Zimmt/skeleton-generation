package skeleton;

import util.CubicBezierCurve;

import javax.vecmath.Point2d;
import javax.vecmath.Point2f;
import javax.vecmath.Tuple2f;
import java.util.List;
import java.util.Random;

public class SpineData {

    private CubicBezierCurve neck;
    private CubicBezierCurve back;
    private CubicBezierCurve tail;

    private Tuple2f chestIntervalOnBack;

    public SpineData(List<Point2d> spinePoints) {
        if (spinePoints.size() != 10) {
            System.err.println("Cannot create spine!");
        }
        this.neck = new CubicBezierCurve(new Point2f(spinePoints.get(0)), new Point2f(spinePoints.get(1)), new Point2f(spinePoints.get(2)), new Point2f(spinePoints.get(3)));
        this.back = new CubicBezierCurve(new Point2f(spinePoints.get(3)), new Point2f(spinePoints.get(4)), new Point2f(spinePoints.get(5)), new Point2f(spinePoints.get(6)));
        this.tail = new CubicBezierCurve(new Point2f(spinePoints.get(6)), new Point2f(spinePoints.get(7)), new Point2f(spinePoints.get(8)), new Point2f(spinePoints.get(9)));
        this.chestIntervalOnBack = new Point2f(0f, (new Random().nextFloat()));
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
}