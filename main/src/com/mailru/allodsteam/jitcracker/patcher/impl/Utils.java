package com.mailru.allodsteam.jitcracker.patcher.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author szagurskiy
 */
public final class Utils {

  public static byte[] getClassBytes(Class<?> clazz) {
    final String clazzResourceName = clazz.getCanonicalName().replace('.', '/') + ".class";
    final ClassLoader classLoader = clazz.getClassLoader() != null ? clazz.getClassLoader() : ClassLoader.getSystemClassLoader();
    final InputStream clazzStream = classLoader.getResourceAsStream(clazzResourceName);

    if (clazzStream == null) {
      throw new RuntimeException("Class resource not found: " + clazz.getCanonicalName());
    }

    try (final InputStream clazzStream0 = clazzStream) {
      final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      final byte[] buffer = new byte[1024];

      int bytesRead = clazzStream.read(buffer);
      while (bytesRead > 0) {
        byteStream.write(buffer, 0, bytesRead);
        bytesRead = clazzStream.read(buffer);
      }

      return byteStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
