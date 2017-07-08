package liuyuns.ezlog;

import liuyuns.ezlog.BuildConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class LogConfig {
  public static final boolean logEnabled = isDebugBuild();

  private static Boolean sDebug;

  /**
   * @return {@code true} if this is a debug build, {@code false} if it is a production build.
   */
  public static boolean isDebugBuild() {
    if (sDebug == null) {
      try {
        final Class<?> activityThread = Class.forName("android.app.ActivityThread");
        final Method currentPackage = activityThread.getMethod("currentPackageName");
        final String packageName = (String) currentPackage.invoke(null, (Object[]) null);
        final Class<?> buildConfig = Class.forName(packageName + ".BuildConfig");
        final Field DEBUG = buildConfig.getField("DEBUG");
        DEBUG.setAccessible(true);
        sDebug = DEBUG.getBoolean(null);
      } catch (final Throwable t) {
        final String message = t.getMessage();
        if (message != null && message.contains("BuildConfig")) {
          // Proguard obfuscated build. Most likely a production build.
          sDebug = false;
        } else {
          sDebug = BuildConfig.DEBUG;
        }
      }
    }

    return sDebug;
  }
}
