package com.mailru.allodsteam.jitcracker.patcher.impl;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

/**
 * @author szagurskiy
 */
public final class ClassPatcher implements ClassFileTransformer {

  private final String classInternalName;

  private final String methodName;

  private final String methodDescriptor;

  private final MethodGenerator methodGenerator;

  private Throwable throwable = null;

  public ClassPatcher(Method patchedMethod, MethodGenerator methodGenerator) {
    this.classInternalName = Type.getType(patchedMethod.getDeclaringClass()).getInternalName();
    this.methodName = patchedMethod.getName();
    this.methodDescriptor = Type.getMethodDescriptor(patchedMethod);
    this.methodGenerator = methodGenerator;
  }

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                          byte[] classfileBuffer) throws IllegalClassFormatException {
    try {
      if (!className.equals(this.classInternalName)) {
        return classfileBuffer;
      }

      final ClassReader classReader = new ClassReader(classfileBuffer);
      final ClassWriter classWriter = new ClassWriter(0);
      classReader.accept(new Visitor(classWriter), 0);

      return classWriter.toByteArray();
    } catch (Throwable t) {
      throwable = t;
      throw t;
    }
  }

  public Throwable getThrowable() {
    return throwable;
  }

  private final class Visitor extends ClassVisitor {

    private boolean methodFound = false;

    public Visitor(ClassWriter classWriter) {
      super(Opcodes.ASM4, classWriter);
    }

    @Override
    public void visitEnd() {
      if (!methodFound) {
        throw new RuntimeException("No methods were found");
      }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
      final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
      if (name.equals(methodName) && desc.equals(methodDescriptor)) {
        if (methodFound) {
          throw new RuntimeException("More than one methods were found");
        }

        methodFound = true;
        methodGenerator.generate(mv);
        return null;
      } else {
        return mv;
      }
    }
  }
}
