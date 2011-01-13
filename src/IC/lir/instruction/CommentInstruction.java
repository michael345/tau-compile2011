package IC.lir.instruction;

public class CommentInstruction extends LIRInstruction {
    String comment;
    
    public CommentInstruction(String comment) {
        super();
        this.comment = comment;
    }

    public String toString() { 
        return "\n#" + comment;
        
    }
}
