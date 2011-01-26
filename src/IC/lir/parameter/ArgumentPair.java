package IC.lir.parameter;

public class ArgumentPair {
    String param;
    String value;

    
    public ArgumentPair(String param, String value) {
        super();
        this.param = param;
        this.value = value;
    }



    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(param);
        builder.append("=");
        builder.append(value);
        return builder.toString();
    }
    
    
}
