package pt.brunoais.dependsTOP;

import java.io.Serializable;

public class DependsTOP implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private static String myIndent = "Top depending B ";
	
	public String dependsTOP() {
		return "Top depending B ";
	}
}
