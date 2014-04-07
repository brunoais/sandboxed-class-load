package pt.brunoais.classloading_test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;


public class MainTest {

	private ClassLoader loader1;
	private ClassLoader loader2;

	private Class<?> SandboxMainClass1;
	private Class<?> SandboxMainClass2;

	private Object victim1;
	private Object victim2;
	private ClassLoader[] classLoader;
	
	@Before
	public void prepare() {

		classLoader = new ClassLoader[1];
		
		Main.loadAllDirectoryJarDependencies(classLoader);
		
		
	}
	

	private void loadBasics() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {

		loader1 = new ClassLoadingClassLoader(classLoader[0], "A");
		loader2 = new ClassLoadingClassLoader(classLoader[0], "B");

		SandboxMainClass1 = loader1
				.loadClass("pt.brunoais.classloading_tests.classes.SandboxMain");
		SandboxMainClass2 = loader2
				.loadClass("pt.brunoais.classloading_tests.classes.SandboxMain");

		victim1 = SandboxMainClass1.newInstance();
		victim2 = SandboxMainClass2.newInstance();

	}

	@Test
	public void classesCreatedAreNotTheSame() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		loadBasics();

		assertNotEquals(
				"The class of the 1st instance must not be the same as the one on the 2nd instance",
				victim1.getClass(), victim2.getClass());

	}

	@Test(expected = ClassCastException.class)
	public void castingTwoToOneIsImpossible() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		loadBasics();

		SandboxMainClass1.cast(victim2);

	}

	@Test(expected = ClassCastException.class)
	public void castingOneToTwoIsImpossible() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		loadBasics();

		SandboxMainClass2.cast(victim1);

	}

	@Test()
	public void castingToHimselfIsOK() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		loadBasics();

		SandboxMainClass1.cast(victim1);

		SandboxMainClass2.cast(victim2);

	}

	@Test()
	public void bothHaveTheSameFullName() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		loadBasics();
		
		assertEquals("The name of both classes must be the same", victim1.getClass().getName(), victim2.getClass().getName());

	}

	@Test
	public void classesCreatedHaveDifferentSetOfMethods()
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		loadBasics();

		try {
			SandboxMainClass1.getMethod("actA").invoke(victim1);
		} catch (NoSuchMethodException e) {
			fail("The first class loaded must have the method actA()");
		}

		try {
			SandboxMainClass2.getMethod("actB").invoke(victim2);
		} catch (NoSuchMethodException e) {
			fail("The second class loaded must have the method actB()");
		}

		fail("just fail");
		try {
			SandboxMainClass1.getMethod("actB").invoke(victim1);
			fail("The first class loaded must NOT have the method actB()");
		} catch (NoSuchMethodException e) {
		}

		try {
			SandboxMainClass2.getMethod("actA").invoke(victim2);
			fail("The second class loaded must NOT have the method actA()");
		} catch (NoSuchMethodException e) {
		}

	}
	

	@Test()
	public void instancesCreatedInsideAreDifferent() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		loadBasics();
		
		assertEquals("The name of both classes must be the same", victim1.getClass().getName(), victim2.getClass().getName());
		
	}


	@Test()
	public void creatingMoreInstancesOnSameLoaderCreatesTheSame() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		loadBasics();
		

		Object victim3 = SandboxMainClass1.newInstance();
		Object victim4 = SandboxMainClass2.newInstance();
		
		assertEquals("Instances from the same loader must use the same class1", victim3.getClass(), victim1.getClass());
		assertEquals("Instances from the same loader must use the same class2", victim4.getClass(), victim2.getClass());
		

		assertNotEquals("Instances from a different loader must use different classes1", victim3.getClass(), victim2.getClass());
		assertNotEquals("Instances from a different loader must use different classes2", victim4.getClass(), victim1.getClass());
		
	}
	@Test()
	public void theresNoClashInNames() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		loadBasics();

		SandboxMainClass1.getMethod("actA").invoke(victim1);
		SandboxMainClass2.getMethod("actB").invoke(victim2);
		
		assertEquals("The name of both classes must be the same", victim1.getClass().getName(), victim2.getClass().getName());
		
	}
	

	@Test()
	public void classesCreatedByCustomLoaderLoadTheirOwnClasses() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NoSuchFieldException {
		loadBasics();

		SandboxMainClass1.getMethod("actA").invoke(victim1);
		SandboxMainClass2.getMethod("actB").invoke(victim2);
		
		Field aField1 = SandboxMainClass1.getDeclaredField("a");
		Field bField1 = SandboxMainClass1.getDeclaredField("b");
		Field dependsField1 = SandboxMainClass1.getDeclaredField("depends");
		Field cField1 = SandboxMainClass1.getDeclaredField("c");
		
		aField1.setAccessible(true);
		bField1.setAccessible(true);
		dependsField1.setAccessible(true);
		cField1.setAccessible(true);

		Object a1 = aField1.get(victim1);
		Object b1 = bField1.get(victim1);
		Object depends1 = dependsField1.get(victim1);
		Object c1 = cField1.get(victim1);
		
		
		Field aField2 = SandboxMainClass2.getDeclaredField("a");
		Field bField2 = SandboxMainClass2.getDeclaredField("b");
		Field dependsField2 = SandboxMainClass2.getDeclaredField("depends");
		Field cField2 = SandboxMainClass2.getDeclaredField("c");
		
		aField2.setAccessible(true);
		bField2.setAccessible(true);
		dependsField2.setAccessible(true);
		cField2.setAccessible(true);
		
		Object a2 = aField2.get(victim2);
		Object b2 = bField2.get(victim2);
		Object depends2 = dependsField2.get(victim2);
		Object c2 = cField2.get(victim2);
		
		
		assertNotEquals("fields(1) must not contain instances of the same class", a1.getClass(), a2.getClass());
		assertNotEquals("fields(2) must not contain instances of the same class", b1.getClass(), b2.getClass());
		assertNotEquals("fields(3) must not contain instances of the same class", depends1.getClass(), depends2.getClass());
		assertNotEquals("fields(4) must not contain instances of the same class", c1.getClass(), c2.getClass());
		
	}
	

	@Test()
	public void globalClassesAreShared() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NoSuchFieldException {
		loadBasics();

		SandboxMainClass1.getDeclaredMethod("actA").invoke(victim1);
		SandboxMainClass2.getDeclaredMethod("actB").invoke(victim2);
		
//		for (Method method : unsuspecterClass2.getMethods()) {
//			System.err.println(method.getName());
//		}
		
		Method method1 = SandboxMainClass1.getDeclaredMethod("dependsBothISee");
		method1.setAccessible(true);
		Object seenByBoth1 = method1.invoke(victim1);
		
		Method method2 = SandboxMainClass2.getDeclaredMethod("dependsBothISee");
		method2.setAccessible(true);
		Object seenByBoth2 = method2.invoke(victim2);
		
		assertEquals("The class one sees must match the class that the otherone sees", seenByBoth1, seenByBoth2);

	}
	

	@Test()
	public void classesThatdependOnClassesThatDependOnClassesEtc() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NoSuchFieldException {
		loadBasics();

		SandboxMainClass1.getDeclaredMethod("actA").invoke(victim1);
		SandboxMainClass2.getDeclaredMethod("actB").invoke(victim2);

		Field dependsField1 = SandboxMainClass1.getDeclaredField("depends");
		dependsField1.setAccessible(true);
		Object depends1 = dependsField1.get(victim1);
		
		Field dependsField2 = SandboxMainClass2.getDeclaredField("depends");
		dependsField2.setAccessible(true);
		Object depends2 = dependsField2.get(victim2);
		
		
		Class<?> depends1ExtendingClass = depends1.getClass().getSuperclass();
		Class<?> depends2ExtendingClass = depends2.getClass().getSuperclass();
		
		assertNotEquals("The class that depends extends must be different, as different class loaders were used", depends1ExtendingClass, depends2ExtendingClass);
		
		System.err.println(depends1ExtendingClass.getName() + "  " + depends2ExtendingClass.getName());
		
		assertEquals("Even though they are different. They share the same name", depends1ExtendingClass.getName(), depends2ExtendingClass.getName());
		

		// The dependsTOP (from projectB) implements java.io.Serializable. The other one doesn't
		
		Class<?>[] depends1ExtendingClassInterfaces = depends1ExtendingClass.getInterfaces();
		Class<?>[] depends2ExtendingClassInterfaces = depends2ExtendingClass.getInterfaces();

		System.err.println(depends1ExtendingClassInterfaces.length + "   " + depends2ExtendingClassInterfaces.length);
		
		assertNotEquals("As the 2nd implements Serializable and the 1st one doesn't, the number of interfaces must not be the same", depends1ExtendingClassInterfaces.length, depends2ExtendingClassInterfaces.length);
		
		for (Class<?> depends1ExtendingClassInterface : depends1ExtendingClassInterfaces) {
			if(depends1ExtendingClassInterface.equals(java.io.Serializable.class)){
				fail("DependsTop for ClassA implements java.io.Serializable.class");
			}
		}

		boolean foundSerializable = false;
		

		System.err.println(Arrays.toString(depends1ExtendingClassInterfaces));
		for (Class<?> depends2ExtendingClassInterface : depends2ExtendingClassInterfaces) {
			if(depends2ExtendingClassInterface.equals(java.io.Serializable.class)){
				foundSerializable = true;
				break;
			}
		}

		if(!foundSerializable){
			fail("DependsTop for ClassB does not implement java.io.Serializable.class");
		}
		
	}

}
