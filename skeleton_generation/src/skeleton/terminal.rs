use super::assembly::SkeletonPart;

/** Summarizes all terminal skeleton parts that can be part of a skeleton.
 ** Terminal skeleton parts cannot be subdivided any further. **/
pub trait Terminal: SkeletonPart {}

/** Simple bone that is only a connection between two points **/
struct SimpleBone {
    start: (f32, f32, f32),
    end: (f32, f32, f32),
    joints: Vec<Joint> // all attached joints
}

impl SkeletonPart for SimpleBone {
    fn is_terminal(&self) -> bool {
        true
    }
}
impl Terminal for SimpleBone {}


/** Joint that connects two bones **/
struct Joint {
    position: (f32, f32, f32),
    connected_bones: (SimpleBone, SimpleBone)
    // here constraints can be added
}

impl SkeletonPart for Joint {
    fn is_terminal(&self) -> bool {
        true
    }
}
impl Terminal for Joint {}
