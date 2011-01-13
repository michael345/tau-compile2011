package IC.lir;
import java.util.LinkedList;
import java.util.List;

import IC.lir.parameter.LIRLabel;

public class LIRDispatchTable {
    String name;
    List<LIRLabel> labels;
    
    public LIRDispatchTable(String name, List<LIRLabel> labels) {
        super();
        this.name = name;
        this.labels = new LinkedList<LIRLabel>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LIRLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<LIRLabel> labels) {
        this.labels = labels;
    }
    
    public void addLabel(LIRLabel label)  {
        this.labels.add(label);
    }
    
    
    
    
}
