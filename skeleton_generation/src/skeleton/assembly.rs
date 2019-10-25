use super::terminal::Terminal;
use super::non_terminal::{
    NonTerminal,
    InputGenerator
};

pub trait SkeletonPart {
    fn is_terminal(&self) -> bool;
}

struct Assembly<'a> {
    terminal_parts: Vec<&'a dyn Terminal>,
    non_terminal_parts: Vec<&'a dyn NonTerminal>
}

impl Assembly<'_> {
    pub fn finished(&self) -> bool {
        self.non_terminal_parts.is_empty()
    }

    /** Does one replacement step if not already finished. **/
    pub fn trigger_replacement(&mut self, input_generator: &dyn InputGenerator) {
        if !self.finished() {
            let non_terminal = self.non_terminal_parts.pop().unwrap();
            let mut new_terminals = Vec::new();
            let mut new_non_terminals = Vec::new();
            let new_parts = (&mut new_terminals, &mut new_non_terminals);
            non_terminal.replace(input_generator, new_parts);

            new_terminals.iter().for_each(|t| self.terminal_parts.push(t.clone()));
        }
        println!("Generation is already finished.");
    }
}
