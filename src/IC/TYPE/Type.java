package IC.TYPE;

public abstract class Type {
    int id;
    public abstract String toString();
    public int getID() {return id;}
    public boolean equals(Type other) {
        return (toString().compareTo(other.toString()) == 0);
    }
}
