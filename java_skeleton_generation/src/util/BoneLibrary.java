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
    private Map<String, Obj> boneDictionary;

    public BoneLibrary() {
        boneDictionary = new HashMap<>();
        Obj cubeObj = loadBoneObj("cube");
        if (cubeObj == null) {
            System.err.println("Could not find .obj file for cube!");
        }
    }

    /**
     * Loads obj if it is not present yet.
     * Do NOT change obj instances!
     * @return obj for the bone with the specified name (or for cube if this bone has no obj)
     */
    public Obj getBoneObj(String name) {
        Obj boneObj = boneDictionary.get(name);
        if (boneObj == null) {
            boneObj = loadBoneObj(name);
        }
        if (boneObj == null) {
            boneObj = boneDictionary.get("cube");
        }
        return boneObj;
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
