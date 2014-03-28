package pt.brunoais.classloading_test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	public static final Logger logger = Logger.getLogger( Main.class.getName() );
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		logger.setLevel(Level.INFO);
//		logger.setLevel(Level.OFF);
        ConsoleHandler handler = new ConsoleHandler();
        // PUBLISH this level
        handler.setLevel(Level.FINER);
        logger.addHandler(handler);

		logger.log(Level.FINEST, "loading");
		
		ClassLoader[] classLoader = new ClassLoader[1];
		
		loadAllDirectoryJarDependencies(classLoader);
		
		
		new Start().startAll(classLoader[0]);
		
		
	}
	
	
	
	
	
	
	private static void findJarsInDir(File rootFile, ArrayList<File> paths) {

		File[] list = rootFile.listFiles();

		if (list == null)
			return;

		for (File f : list) {
			if (f.isDirectory()) {
				Main.logger.log(Level.FINEST, "Dir:" + f.getAbsoluteFile());
				findJarsInDir(f, paths);
			} else {
				Main.logger.log(Level.FINEST, "File:" + f.getAbsoluteFile());
				if(f.getName().endsWith(".jar")){
					paths.add(f);
				}
			}
		}
	}
	
	
	
	public static void loadAllDirectoryJarDependencies(ClassLoader[] classLoader) {
		
		File baseDir = new File("./lib");

		Main.logger.log(Level.FINER, "lib dir:" + baseDir.getPath());
		
		if(baseDir.isDirectory()){
		
			ArrayList<File> paths = new ArrayList<File>(Math.max(baseDir.list().length, 100));
			
			findJarsInDir(baseDir, paths);
			
			ArrayList<URL> urls = new ArrayList<URL>(paths.size());

			for (File path : paths) {
				try {
					urls.add(new URL("jar:file:" + path.getAbsolutePath() + "!/"));
				} catch (MalformedURLException e) {
					Main.logger.log(Level.INFO, "Failed to convert path to URL", e);
				}
			}

			
			URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), Main.class.getClassLoader());
			
			classLoader[0] = urlClassLoader;

	        
			for (File path : paths) {
				JarFile jarFile;
				try {
					jarFile = new JarFile(path.getAbsolutePath());
					loadAllJarClasses(urlClassLoader, jarFile);
				} catch (IOException e) {
					Main.logger.log(Level.WARNING, "IOException while reading the .jar:\n" + path.getAbsolutePath(), e);
				} catch (ClassNotFoundException e) {
					Main.logger.log(Level.WARNING, "A class found in a .jar could not be found by the loader while reading jar\n" + path.getAbsolutePath(), e);
				}
			}
		}else{
			Main.logger.log(Level.FINER, "lib dir not found: " + baseDir.getAbsolutePath());
		}
	}

	
	public static void loadAllJarClasses(URLClassLoader cl, JarFile jarFile) throws IOException, ClassNotFoundException {

        Enumeration<JarEntry> elements = jarFile.entries();

        
        while (elements.hasMoreElements()) {
            JarEntry je = (JarEntry) elements.nextElement();
            if(je.isDirectory() || !je.getName().endsWith(".class")){
                continue;
            }
            
            // -6 because of .class
            String className = je.getName().substring(0,je.getName().length()-6);
            className = className.replace('/', '.');

            Class<?> loadedClass = cl.loadClass(className);
            
            Main.logger.log(Level.FINER, "Loaded:" + loadedClass.getName());
            

        }
	}
	
}
