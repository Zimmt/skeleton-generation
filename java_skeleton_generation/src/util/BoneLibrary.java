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
    private static final String defaultKey = "cube";
    private Map<String, Obj> boneDictionary;

    public BoneLibrary() {
        boneDictionary = new HashMap<>();
        Obj cubeObj = loadBoneObj(defaultKey);
        if (cubeObj == null) {
            System.err.println("Could not find .obj file for cube!");
        }
    }

    /**
     * Loads obj if it is not present yet.
     * Do NOT change obj instances!
     * @return obj for the bone with the specified name (or for cube if this bone has no obj)
     */
    public Obj getBoneObj(String name, boolean mirroredVersion) {
        String key = mirroredVersion ? name + "_mirrored" : name;
        Obj boneObj = boneDictionary.get(key);
        if (boneObj == null) {
            boneObj = loadBoneObj(key);
        }
        if (boneObj == null) {
            boneObj = boneDictionary.get(defaultKey);
        }
        return boneObj;
    }

    public Obj getDefaultObj() {
        return boneDictionary.get(defaultKey);
    }

    private Obj loadBoneObj(String name) {
        String pathName = objPath + name + ".obj";
        Obj boneObj;
        try {
            boneObj = ObjReader.read((new FileInputStream(new File(pathName))));
        } catch (IOException e) {
            return null;
        }
        boneDictionary.put(name, boneObj);
        return boneObj;
    }
}
