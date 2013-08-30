package com.mailru.allodsteam.jitcracker.patcher.impl;

import org.objectweb.asm.MethodVisitor;

/**
 * @author szagurskiy
 */
public interface MethodGenerator {
  void generate(MethodVisitor mv);
}
