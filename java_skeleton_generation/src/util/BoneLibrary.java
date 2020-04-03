package util;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BoneLibrary {

    private static final String objPath = "./boneLibrary/";
    private static final String lowResObjPath = "./boneLibrary/low_resolution/";
    private static final String defaultKey = "cube";
    private Map<String, Obj> boneDictionary;
    private Map<String, Obj> lowResBoneDictionary;

    public BoneLibrary() {
        boneDictionary = new HashMap<>();
        lowResBoneDictionary = new HashMap<>();
        Obj cubeObj = loadBoneObj(defaultKey, false);
        Obj lowResCube = loadBoneObj(defaultKey, true);
        if (cubeObj == null || lowResCube == null) {
            System.err.println("Could not find .obj file for cube!");
        }
    }

    /**
     * Loads obj if it is not present yet.
     * Do NOT change obj instances!
     * @return obj for the bone with the specified name (or for cube if this bone has no obj)
     */
    public Obj getBoneObj(String name, boolean mirroredVersion, boolean lowRes) {
        String key = mirroredVersion ? name + "_mirrored" : name;
        Obj boneObj = lowRes ? lowResBoneDictionary.get(key) : boneDictionary.get(key);
        if (boneObj == null) {
            boneObj = loadBoneObj(key, lowRes);
        }
        if (boneObj == null) {
            boneObj = boneDictionary.get(defaultKey);
        }
        return boneObj;
    }

    public Obj getDefaultObj() {
        return boneDictionary.get(defaultKey);
    }

    private Obj loadBoneObj(String name, boolean lowRes) {
        String pathPrefix = lowRes ? lowResObjPath : objPath;
        String pathName = pathPrefix + name + ".obj";
        Obj boneObj;
        try {
            boneObj = ObjReader.read((new FileInputStream(new File(pathName))));
        } catch (IOException e) {
            return null;
        }
        if (lowRes) {
            lowResBoneDictionary.put(name, boneObj);
        } else {
            boneDictionary.put(name, boneObj);
        }
        return boneObj;
    }
}
