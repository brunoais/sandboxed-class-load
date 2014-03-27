package pt.brunoais.classloading_tests.classes;

import pt.brunoais.forBoth.DependsBoth;
import pt.brunoais.forSomeone.Depends;

public class SandboxMain {
	
	ClassA a;
	ClassB b;
	Depends depends;
	ClassB c;
	
	
	public void actA() {
		a = new ClassA();
		
		System.out.println(a.beA());
		System.out.println(a.beB());
		
		
		b = new ClassB();

		System.out.println(b.beB());
		
		depends = new Depends();
		
		System.out.println(depends.depends());
		
		
		System.out.println("Mr. A says: " + DependsBoth.setWasHere("A was here"));

		c = new ClassB();
		
		System.out.println("A got B again." + c.beB());
	}
	
	
	Class<DependsBoth> dependsBothISee() {
		return DependsBoth.class;
	}
}
