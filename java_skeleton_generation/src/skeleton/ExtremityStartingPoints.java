package skeleton;

import skeleton.elements.ExtremityKind;

import java.util.*;

public class ExtremityStartingPoints {

    private static int[] possibleWingPositions = new int[] {1, 2};
    private static int[] possibleLegPositions = new int[] {0, 1};
    private static int[] possibleArmPositions = new int[] {1, 2};
    private static int[] possibleFinPositions = new int[] {0, 1, 2};

    // a two element array of extremity kinds for each extremity starting point
    // the first entry concerns the extremity starting point that is nearest to the tail
    private List<ExtremityKind[]> extremityKindsForStartingPoints;

    private Random random = new Random();

    public ExtremityStartingPoints(boolean hasSecondShoulder) {
        if (hasSecondShoulder) {
            extremityKindsForStartingPoints = Arrays.asList(new ExtremityKind[2], new ExtremityKind[2], new ExtremityKind[2]);
        } else {
            extremityKindsForStartingPoints = Arrays.asList(new ExtremityKind[2], new ExtremityKind[2]);
        }
    }

    public int getStartingPointCount() {
        return extremityKindsForStartingPoints.size();
    }

    public ExtremityKind[] getExtremityKindsForStartingPoint(int startingPoint) {
        if (startingPoint >= extremityKindsForStartingPoints.size()) {
            return new ExtremityKind[0];
        } else {
            return extremityKindsForStartingPoints.get(startingPoint);
        }
    }

    public void setWings(int wingCount) {
        setKindAtPositions(ExtremityKind.WING, wingCount, possibleWingPositions);
    }

    public void setLegs(int legCount) {
        setKindAtPositions(ExtremityKind.FLOORED_LEG, legCount, possibleLegPositions);
    }

    public void setArms(int armCount) {
        setKindAtPositions(ExtremityKind.NON_FLOORED_LEG, armCount, possibleArmPositions);
    }

    public void setFins(int finCount) {
        setKindAtPositions(ExtremityKind.FIN, finCount, possibleFinPositions);
    }

    public int getFreeExtremityCountAtPosition(int position) {
        ExtremityKind[] kinds = getExtremityKindsForStartingPoint(position);
        int count = 0;
        for (ExtremityKind kind : kinds) {
            if (kind == null) {
                count++;
            }
        }
        return count;
    }

    public int getFreeWingOrArmCount() {
        int count = 0;
        for (int i = 0; i < possibleWingPositions.length; i++) {
            count += getFreeExtremityCountAtPosition(possibleWingPositions[i]);
        }
        return count;
    }

    public void setKindAtPosition(ExtremityKind kind, int position) {
        if (extremityKindsForStartingPoints.get(position)[0] == null) {
            extremityKindsForStartingPoints.get(position)[0] = kind;
        } else if (extremityKindsForStartingPoints.get(position)[1] == null){
            extremityKindsForStartingPoints.get(position)[1] = kind;
        } else {
            System.err.println("Cannot set kind in specified position!");
        }
    }

    private void setKindAtPositions(ExtremityKind kind, int count, int[] positions) {
        int toSet = count;
        while (toSet > 0) {
            List<Integer> possiblePositions = new ArrayList<>(positions.length);
            for (int i = 0; i < positions.length; i++) {
                if (getFreeExtremityCountAtPosition(positions[i]) > 0) {
                    possiblePositions.add(positions[i]);
                }
            }
            if (possiblePositions.isEmpty()) {
                System.err.println("Could not set all extremities");
                break;
            } else {
                int position = possiblePositions.get(random.nextInt(possiblePositions.size()));
                setKindAtPosition(kind, position);
                toSet--;
            }
        }
    }
}
