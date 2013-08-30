package com.mailru.allodsteam.jitcracker.patcher;

import com.mailru.allodsteam.jitcracker.patcher.impl.ClassPatcher;
import com.mailru.allodsteam.jitcracker.patcher.impl.DonorMethodInjector;
import com.mailru.allodsteam.jitcracker.patcher.impl.InstrumentationProvider;
import com.mailru.allodsteam.jitcracker.patcher.impl.Utils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Arrays;

/**
 * @author szagurskiy
 */
public final class Patcher {

  public static void patchMethod(Class<?> clazz, String patchedMethodName, String donorMethodName) {
    patchMethod(clazz, patchedMethodName, clazz, donorMethodName);
  }

  public static void patchMethod(Class<?> patchedClass, String patchedMethodName, Class<?> donorClass, String donorMethodName) {
    final Method patchedMethod = findMethod(patchedClass, patchedMethodName);
    final Method donorMethod = findMethod(donorClass, donorMethodName);

    patchMethod(patchedMethod, donorMethod);
  }

  private static Method findMethod(Class<?> patchedClass, String patchedMethodName) {
    final Method[] declaredMethods = patchedClass.getDeclaredMethods();
    Method patchedMethod = null;
    for (Method declaredMethod : declaredMethods) {
      if (declaredMethod.getName().equals(patchedMethodName)) {
        if (patchedMethod == null) {
          patchedMethod = declaredMethod;
        } else {
          throw new RuntimeException("Multiple methods with same name found: class=" + patchedClass + ", methodName=" + patchedMethodName);
        }
      }
    }

    if (patchedMethod == null) {
      throw new RuntimeException("Method not found: class=" + patchedClass + ", methodName=" + patchedMethodName);
    }

    return patchedMethod;
  }

  public static void patchMethod(Method patchedMethod, Method donorMethod) {
    if (!Arrays.equals(patchedMethod.getParameterTypes(), donorMethod.getParameterTypes())) {
      throw new RuntimeException("Patched and donor methods signature doesn't match: patchedMethod=" + patchedMethod + ", donorMethod=" + donorMethod);
    }

    final Instrumentation instr = InstrumentationProvider.getInstrumentation();

    final ClassPatcher transformer = new ClassPatcher(patchedMethod, new DonorMethodInjector(donorMethod));
    instr.addTransformer(transformer, true);

    try {
      instr.retransformClasses(patchedMethod.getDeclaringClass());
      if (transformer.getThrowable() != null) {
        throw new RuntimeException(transformer.getThrowable());
      }
    } catch (UnmodifiableClassException e) {
      throw new RuntimeException(e);
    } finally {
      instr.removeTransformer(transformer);
    }
  }

  public static void resetClass(Class<?> clazz) {
    final Instrumentation instr = InstrumentationProvider.getInstrumentation();
    final byte[] originalClassBytes = Utils.getClassBytes(clazz);
    final ClassFileTransformer transformer = new ClassFileTransformer() {
      @Override
      public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return originalClassBytes;
      }
    };

    instr.addTransformer(transformer, true);

    try {
      instr.retransformClasses(clazz);
    } catch (UnmodifiableClassException e) {
      throw new RuntimeException(e);
    } finally {
      instr.removeTransformer(transformer);
    }
  }
}
