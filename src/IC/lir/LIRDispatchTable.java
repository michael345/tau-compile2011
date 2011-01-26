package IC.lir;
import java.util.LinkedList;
import java.util.List;

import IC.lir.parameter.LIRLabel;

public class LIRDispatchTable {
    LIRLabel name;
    List<LIRLabel> labels;
    
    public LIRDispatchTable(String name) {
        super();
        this.name = new LIRLabel(name);
        this.labels = new LinkedList<LIRLabel>();
    }
    
    public LIRDispatchTable(LIRLabel name) {
        super();
        this.name = name;
        this.labels = new LinkedList<LIRLabel>();
    }
    
    
    public LIRLabel getLabel() { 
        return name;
    }

    public String getName() {
        return name.toString();
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
    
    public String toString() { 
        StringBuffer out = new StringBuffer(name + ": [");
        for (LIRLabel label : labels) { 
            out.append(label + ",");
        }
        if (labels.size() > 0) { 
            out.deleteCharAt(out.length()-1);//delete the last comma //TODO: Maybe without -1
        }
        out.append("]");
        return out.toString();
    }
    
    
    
    
}
