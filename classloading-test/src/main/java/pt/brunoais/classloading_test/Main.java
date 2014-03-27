package pt.brunoais.classloading_test;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	public static final Logger logger = Logger.getLogger( Main.class.getName() );
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		logger.setLevel(Level.FINER);
//		logger.setLevel(Level.OFF);
        ConsoleHandler handler = new ConsoleHandler();
        // PUBLISH this level
        handler.setLevel(Level.FINER);
        logger.addHandler(handler);

		logger.log(Level.FINEST, "loading");
		
		ClassLoader loader1 = new ClassLoadingClassLoader(Main.class.getClassLoader(), "A");
		ClassLoader loader2 = new ClassLoadingClassLoader(Main.class.getClassLoader(), "B");
//		ClassLoader loader3 = new ClassLoadingClassLoader(Main.class.getClassLoader(), "C");
		
		Class<?> unsuspecterClass1 = loader1.loadClass("pt.brunoais.classloading_tests.classes.SandboxMain");
		Class<?> unsuspecterClass2 = loader2.loadClass("pt.brunoais.classloading_tests.classes.SandboxMain");
//		Class<?> unsuspecterClass3 = loader3.loadClass("pt.brunoais.classloading_tests.classes.SandboxMain");
		
		Object victim1 = unsuspecterClass1.newInstance();
		Object victim2 = unsuspecterClass2.newInstance();
//		Object victim3 = unsuspecterClass3.newInstance();
		
		unsuspecterClass1.getMethod("actA").invoke(victim1);

		unsuspecterClass2.getMethod("actB").invoke(victim2);

//		unsuspecterClass3.getMethod("act").invoke(victim3);
		
		

		
		
	}
}
