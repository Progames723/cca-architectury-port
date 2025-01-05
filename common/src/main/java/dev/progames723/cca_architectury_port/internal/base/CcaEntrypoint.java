package dev.progames723.cca_architectury_port.internal.base;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings({"removal", "unchecked"})
public class CcaEntrypoint<I extends ComponentRegistrationInitializer> {
	private final I instance;
	
	public CcaEntrypoint(Class<? extends I> entrypointClass) throws Exception {
		this(entrypointClass.getDeclaredConstructor().newInstance());
	}
	
	public CcaEntrypoint(I instance) {
		this.instance = instance;
	}
	
	public I getInstance() {
		return instance;
	}
	
	
	public static Collection<CcaEntrypoint<ComponentRegistrationInitializer>> getAllEntrypoints() {
		ArrayList<CcaEntrypoint<ComponentRegistrationInitializer>> list = new ArrayList<>();
		try (ScanResult result = new ClassGraph()
			.overrideClassLoaders(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) CcaEntrypoint.class::getClassLoader, AccessController.getContext(), new RuntimePermission("getClassLoader")))
			.rejectPackages("java", "jdk", "com.sun", "sun", "javax", "com.google")
			.enableAllInfo()
			.scan()
		) {
			for (ClassInfo info : result.getClassesImplementing(ComponentRegistrationInitializer.class)) {
				list.add(new CcaEntrypoint<>(info.loadClass(ComponentRegistrationInitializer.class).getDeclaredConstructor().newInstance()));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return list;
	}
	
	public static <I extends ComponentRegistrationInitializer> Collection<CcaEntrypoint<I>> getEntrypoints(Class<I> clazz) {
		ArrayList<CcaEntrypoint<I>> list = new ArrayList<>();
		try (ScanResult result = new ClassGraph()
			.overrideClassLoaders(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) CcaEntrypoint.class::getClassLoader, AccessController.getContext(), new RuntimePermission("getClassLoader")))
			.rejectPackages("java", "jdk", "com.sun", "sun", "javax", "com.google")
			.enableAllInfo()
			.scan()
		) {
			for (ClassInfo info : result.getClassesImplementing(clazz)) {
				list.add(new CcaEntrypoint<>(info.loadClass(clazz).getDeclaredConstructor().newInstance()));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return list;
	}
}
