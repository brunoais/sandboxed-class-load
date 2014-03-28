package pt.brunoais.classloading_test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class ClassLoadingClassLoader extends ClassLoader {
	
	@SuppressWarnings("unused")
	private String classType;
	private String basePath;
	
	private Map<String,Class<?>> jarClasses;
	
	public ClassLoadingClassLoader(String classType) {
		super();
		this.classType = classType;
		basePath = "." + File.separator +  "Classes" + classType;
		
		jarClasses = new HashMap<String, Class<?>>();
		
		loadAllDirectoryJarDependencies();
	}
	
	public ClassLoadingClassLoader(ClassLoader parent) {
		this(parent, "");
	}
	
	public ClassLoadingClassLoader(ClassLoader parent, String classType) {
		super(parent);
		
		this.classType = classType;
		basePath = "." + File.separator +  "Classes" + classType;
		
		jarClasses = new HashMap<String, Class<?>>();
		
		loadAllDirectoryJarDependencies();
		resolveLoadedJarClasses();

	}
	

	private void findJarsInDir(File rootFile, ArrayList<File> paths) {

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
	
	
	
	public void loadAllDirectoryJarDependencies() {
		
		File baseDir = new File(basePath + "/lib");

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
			
			
			URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), this);
			
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

	
	public void loadAllJarClasses(URLClassLoader cl, JarFile jarFile) throws IOException, ClassNotFoundException {

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
            
            jarClasses.put(loadedClass.getName(), loadedClass);
        }
	}
	

	private void resolveLoadedJarClasses() {
		for (Class<?> classDefinition : jarClasses.values()) {
			resolveClass(classDefinition);
		}
	}
	
	
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		// First check if it was in one of the .jar.
		Class<?> classFound = jarClasses.get(name);
		if (classFound != null) {
			return classFound;
		}
		try{
//			System.out.println("loading: " + name);
			// Then try to get java to include it
			// This will succeed if:
			// It was a class already successfully loaded with the code blow
			// It's a class that would be found without this custom classloader
			return super.loadClass(name);
		}catch(ClassNotFoundException e){
			
//			int splitPos = name.lastIndexOf('.');
//			String path = name.substring(0, splitPos).replace('.', File.separatorChar);
			String className = name.replace('.', File.separatorChar) + ".class";
//			String fileName = name.substring(splitPos + 1) + ".class";
//			String fileName = name.substring(splitPos + 1) + ".class";
			
			String fullFilePath = basePath + 
					File.separator +  "target" + File.separator +  "classes" +
					File.separator + className;
			
			try {
				Main.logger.log(Level.FINER, "loading class at:\n" + new File(fullFilePath).getCanonicalPath());
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			BufferedInputStream input = null;
			try {
				input = new BufferedInputStream(new FileInputStream(fullFilePath));
				
				byte[] buffer = new byte[1024];
				int sizeRead = 0;
				ByteArrayOutputStream accumulator = new ByteArrayOutputStream();
				
				while( (sizeRead = input.read(buffer, 0, buffer.length)) > 0){
					accumulator.write(buffer, 0, sizeRead);
				}
				byte[] classRead = accumulator.toByteArray();
				Class<?> newClass = super.defineClass(name, classRead, 0, classRead.length);
				
				return newClass;
				
			} catch (FileNotFoundException e1) {
				throw new ClassNotFoundException(e1.getMessage(), e1);
			} catch (IOException e1) {
				throw new ClassNotFoundException(e1.getMessage(), e1);
			}finally{
				try {
					if(input != null)
						input.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
//			throw new ClassNotFoundException();
		}
		
	}

	
	@Override
	public String toString() {
		return "It's me, ClassLoadingClassLoader don't worry";
	}
}
