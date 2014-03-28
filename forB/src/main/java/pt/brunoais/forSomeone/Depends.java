package pt.brunoais.forSomeone;

import pt.brunoais.dependsTOP.DependsTOP;

public class Depends extends DependsTOP{
	private static final long serialVersionUID = 1L;

	public String depends() {
		return dependsTOP() + " A depends on me";
	}
}
