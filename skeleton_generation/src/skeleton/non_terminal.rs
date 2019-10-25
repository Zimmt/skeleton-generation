use super::assembly::SkeletonPart;
use super::terminal::Terminal;

/** Summarizes all non terminal skeleton parts that can be part of a skeleton.
 ** Non terminal skeleton parts contain rules that replace them by one or more
 ** terminal or non terminal skeleton parts. These can be called by the replace function.
 ** If the replace function needs input the input_generator is 'asked'. **/
pub trait NonTerminal: SkeletonPart {
    fn replace(&self, input_generator: &dyn InputGenerator, new_parts: (&mut Vec<&dyn Terminal>, &mut Vec<&dyn NonTerminal>));
}

pub trait InputGenerator {
    fn get_int(&self, message: &str) -> i32;
    // here more possible inputs can be defined
}
