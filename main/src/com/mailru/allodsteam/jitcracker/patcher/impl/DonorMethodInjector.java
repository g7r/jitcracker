package com.mailru.allodsteam.jitcracker.patcher.impl;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;

/**
 * @author szagurskiy
 */
public final class DonorMethodInjector implements MethodGenerator {

  private final Class<?> donorClass;

  private final String donorMethod;

  private final byte[] donorBytes;

  public DonorMethodInjector(Method donorMethod) {
    this.donorClass = donorMethod.getDeclaringClass();
    this.donorMethod = donorMethod.getName();
    this.donorBytes = Utils.getClassBytes(this.donorClass);
  }

  public void generate(final MethodVisitor mv) {
    final boolean[] successHolder = {false};
    final ClassReader donorClassReader = new ClassReader(donorBytes);
    donorClassReader.accept(new ClassVisitor(Opcodes.ASM4) {
      @Override
      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.equals(donorMethod)) {
          if (successHolder[0]) {
            throw new RuntimeException("More than one methods were patched");
          }

          successHolder[0] = true;
          return mv;
        } else {
          return null;
        }
      }
    }, 0);

    if (!successHolder[0]) {
      throw new RuntimeException("No methods were patched");
    }
  }
}
