package com.mailru.allodsteam.jitcracker.main;

import com.mailru.allodsteam.jitcracker.patcher.Patcher;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author szagurskiy
 */
public final class PatchRule implements TestRule {

  private final Set<Class<?>> patchedClasses = new HashSet<>();

  public PatchRule() {
  }

  public void patchMethod(Class<?> patchedClass, String patchedMethodName, Class<?> donorClass, String donorMethodName) {
    Patcher.patchMethod(patchedClass, patchedMethodName, donorClass, donorMethodName);
    patchedClasses.add(patchedClass);
  }

  public void patchMethod(Method patchedMethod, Method donorMethod) {
    Patcher.patchMethod(patchedMethod, donorMethod);
    patchedClasses.add(patchedMethod.getDeclaringClass());
  }

  @Override
  public Statement apply(Statement base, Description description) {
    return new UnpatchStatement(base);
  }

  private final class UnpatchStatement extends Statement {

    private final Statement base;

    private UnpatchStatement(Statement base) {
      this.base = base;
    }

    @Override
    public void evaluate() throws Throwable {
      try {
        base.evaluate();
      } finally {
        for (Class<?> patchedClass : patchedClasses) {
          Patcher.resetClass(patchedClass);
        }
      }
    }
  }
}
