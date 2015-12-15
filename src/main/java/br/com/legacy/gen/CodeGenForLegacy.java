package br.com.legacy.gen;

import br.com.legacy.classes.Other;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class CodeGenForLegacy {

	public static void main(String[] args) throws Exception {

		StringBuilder rootPackage = new StringBuilder("br.com.legacy.classes");
        Set<Class<?>> classes = getPackageContent(rootPackage.toString(), Other.class);

		File outputDir = new File("src/test/java/");
		outputDir.mkdirs();

		for (Class<?> clazz : classes) {

            //Add 'Test' suffix to test class
			StringBuilder testClassName = new StringBuilder(clazz.getName()).append("Test");

			JCodeModel codeModel = new JCodeModel();

			JDefinedClass definedClass = codeModel._class(testClassName.toString());

            //Create setUp method
			JMethod setupMethod = definedClass.method(JMod.PUBLIC, void.class, "setUp");

            //Annotate setUp method with @Before
			setupMethod.annotate(Before.class);

			for (Method sourceMethod : clazz.getDeclaredMethods()) {

                //Make private methods accessible
				sourceMethod.setAccessible(true);

                //Create a public method with void return
                JMethod method = definedClass.method(JMod.PUBLIC, void.class, buildMethodName(sourceMethod));

                //Annotate the method with @Test
                method.annotate(Test.class);

                //Adds TODO statement
                method.body().directStatement("//TODO Implement this!");

                //Create an invocation to Assert.fail with String argument
                method.body().staticInvoke(codeModel.ref(Assert.class),"fail").arg("Not implemented yet.");
			}

            //Build Test Class
			codeModel.build(outputDir);

        }
	}

    /**
     * Load all classes in provided package.
     * Ps. this method has adapted from Aleksander Blomskøld example. The original post can be found on StackOverFlow
     * {@literal http://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection}
     * @param packageName package to be loaded
     * @param exclusions Classes to be ignored
     * @return Set of classes loaded
     * @throws IOException
     */
	private static Set<Class<?>> getPackageContent(String packageName, Object ...exclusions) throws IOException{

		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());

		Reflections reflections = new Reflections(new ConfigurationBuilder()
		.setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
		.setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
		.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName))));

		Set<Class<?>> restricoes = new HashSet<Class<?>>();

		restricoes.addAll(reflections.getSubTypesOf(Object.class));
        restricoes.removeAll(Arrays.asList(exclusions));

		return restricoes;
	}

    /**
     * Build method name. The name is built through the original method name
     * added by the parameters type in method, if has any.
     * @param method method
     * @return mathod name
     */
    private static String buildMethodName(Method method){

        StringBuilder methodName = new StringBuilder(method.getName());

        for(Parameter parameter: method.getParameters()){
            methodName.append(parameter.getType().getSimpleName());
        }

        return  methodName.toString();
    }
}

