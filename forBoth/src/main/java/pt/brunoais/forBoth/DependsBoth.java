package pt.brunoais.forBoth;

public class DependsBoth {
	
	private static String wasHere;
	
	static{
		wasHere = "No one was here";
	}
	
	public static synchronized String dependsBoth() {
		return wasHere;
	}

	public static synchronized String setWasHere(String wasHere) {
		String was = DependsBoth.wasHere;
		DependsBoth.wasHere = wasHere;
		return was;
	}
}
