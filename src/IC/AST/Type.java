package IC.AST;

import IC.SymbolTables.SymbolTable;
import IC.TYPE.TypeTable;
import IC.TYPE.VoidType;


/**
 * Abstract base class for data type AST nodes.
 * 
 * @author Tovi Almozlino
 */
public abstract class Type extends ASTNode {

	/**
	 * Number of array 'dimensions' in data type. For example, int[][] ->
	 * dimension = 2.
	 */
	private int dimension = 0;

	/**
	 * Constructs a new type node. Used by subclasses.
	 * 
	 * @param line
	 *            Line number of type declaration.
	 */
	protected Type(int line) {
		super(line);
		setSemanticType(new VoidType(0));
	}

	public abstract String getName();

	public int getDimension() {
		return dimension;
	}

	public void incrementDimension() {
		++dimension;
	}
	
	public void setEnclosingScope(SymbolTable t) { 
	    if (getEnclosingScope() == null) { 
	        super.setEnclosingScope(t);
	    }
	}

    
}