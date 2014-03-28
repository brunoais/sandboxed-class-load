package pt.brunoais.classloading_test;

import java.lang.reflect.InvocationTargetException;

public class Start {
	
	void startAll(ClassLoader classLoader) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
		ClassLoader loader1 = new ClassLoadingClassLoader(classLoader, "A");
		ClassLoader loader2 = new ClassLoadingClassLoader(classLoader, "B");
//		ClassLoader loader3 = new ClassLoadingClassLoader(Start.class.getClassLoader(), "C");
		
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
