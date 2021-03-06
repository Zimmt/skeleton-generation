# skeleton-generation #

In this repository you find my master's thesis on the generation of vertebrate skeletons.
I implemented an algorithm that automatically generates (conditionally) random 3D-models of vertebrate skeletons (in .obj format).
Detailed information on how this is done can be found in my thesis `latex/thesis.pdf` (in german).

![example skeleton](java_skeleton_generation/example_skeletons/bird.jpg)

The algorithm uses 2D-graphics of vertebrate skeletons as basis to be able to position and scale the bones realistically.

Without further input random skeletons are generated. But it is also possible to generate skeletons with predefined properties or variations of an exisiting skeleton (see picture of UI and description below). The example above was generated with the following conditions: 2 legs and 2 wings.
Additionally it is possible to generate fantastic animals with more than for extremities and up to two shoulder girdles.

The generated skeletons consist of boxes in the lowest resolution. When higher resolutions are used the boxes are replaced by 3D-models of real vertebrate bones.

![ui](latex/graphics/gui.png)

# Usage #

To use the skeleton generator you only need the folder `executable_skeleton_generation` and java SE 11.
Execute the .jar file in the folder with the command `java -jar java_skeleton_generation.jar`.
The generator works with relative file paths, so the .jar file has to be executed inside the folder.

For the first run you don't need to do anything but click the "start generator" button. Then a file named `skeleton0.obj`should
be generated in the `executable_skeleton_generation` folder. You can view this file with a 3D editor of your choice
or by clicking the "choose skeleton" button next to "show skeleton".


## Generating random skeletons ##

Starting the generator without any other settings will generate random skeletons. 
You can generate several at once by entering the desired number next to "number of skeletons to generate".
They will be saved as `skeletonX.obj` with X from 0 to the desired number.
(This also means that skeletons of a previous run will possibly be overwritten!)

By changing the resolution you determine how the bones of the skeleton are represented. 
"only bounding boxes" will only generate boxes, with low and high resolution 3D models of real bones are used.


## Generating skeletons with predefined properties ##

In the middle part of the GUI you can enter properties you want your skeleton to have.
These include the number of legs, wings, arms and fins and the length of neck and tail.
If you want to generate fantastic skeletons you can allow more then one extremity per girdle (shoulder or pelvis)
and you can add a second shoulder girdle that will be placed on the neck (similar to centaurs).


## Saving and loading skeletons

You can save the meta data of a skeleton to file by ticking "save to file" and entering a file name **before** generating a skeleton.
Then the generator will output fileNameX.txt additionally to the obj.
This file can then be loaded into the generator by choosing it in "load from file".
This should then generate the exact same skeleton / .obj file again.

You can also use this mechanism to create variations of a saved skeleton.

As additional feature you can load predefined examples (and create variations from them).
When you do this (or create variations from one) all of your input in the middle part of the GUI, except neck and tail length, is also respected.
(If you are interested in the source of those examples (or if they seem strange and you want to see a reference animal)
look inside the `pca/Skelettbilder` folder.)

