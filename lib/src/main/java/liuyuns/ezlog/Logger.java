package liuyuns.ezlog;

import android.util.Log;

import java.util.HashSet;

public final class Logger {

  public interface LogImpl {
    void debug(String tag, String message);

    void info(String tag, String message);

    void warn(String tag, String message);

    void error(String tag, String message, Exception e);
  }

  public static class LogImplConsole implements LogImpl {
    public void info(String tag, String message) {
      System.out.print(tag);
      System.out.print(": ");
      System.out.println(message);
    }

    public void error(String tag, String message, Exception e) {
      System.err.print(tag);
      System.err.print(": ");
      System.err.print(message);
      if (e != null) {
        e.printStackTrace();
      }
    }

    public void debug(String tag, String message) {
      info(tag, message);
    }

    public void warn(String tag, String message) {
      info(tag, message);
    }
  }

  public static class LogImplAndroid implements LogImpl {
    public void info(String tag, String message) {
      Log.i(tag, message);
    }

    public void debug(String tag, String message) {
      Log.d(tag, message);
    }

    public void warn(String tag, String message) {
      Log.w(tag, message);
    }

    public void error(String tag, String message, Exception e) {
      Log.e(tag, message, e);
    }
  }

  private static LogImpl sLogImpl;

  private String tagName;
  private int indent = 1;

  static {
    if (sLogImpl == null) {
      try {
        int sdk = android.os.Build.VERSION.SDK_INT;
        sLogImpl = new LogImplAndroid();
      } catch (Exception e) {
        sLogImpl = new LogImplConsole();
      }
    }
  }

  private Logger(String tagName) {
    this.tagName = tagName;

    if (this.tagName == null || this.tagName.length() == 0) {
      this.tagName = "General";
    }
  }

  public void ee() {
    if (LogConfig.logEnabled) {
      logEnter(1, "");
      logExit(1, "");
    }
  }

  public void ee(String msgFormat, Object... args) {
    if (LogConfig.logEnabled) {
      String msg = String.format(msgFormat, args);
      logEnter(1, msg);
      logExit(1, msg);
    }
  }

  public void enter() {
    if (LogConfig.logEnabled)
      logEnter(1, "");
  }

  public void enter(String message) {
    if (LogConfig.logEnabled) {
      logEnter(1, message);
    }
  }

  public void enter(String msgFormat, Object... args) {
    if (LogConfig.logEnabled) {
      String msg = String.format(msgFormat, args);
      logEnter(1, msg);
    }
  }

  public void exit() {
    if (LogConfig.logEnabled) {
      logExit(1, "");
    }
  }

  public void exit(String message) {
    if (LogConfig.logEnabled) {
      logExit(1, message);
    }
  }

  public void exit(String msgFormat, Object... args) {
    if (LogConfig.logEnabled) {
      String msg = String.format(msgFormat, args);

      logExit(1, msg);
    }
  }


  public void info(String message) {
    if (LogConfig.logEnabled) {
      logInfo(message);
    }
  }

  public void info(String msgFormat, Object... args) {
    if (LogConfig.logEnabled) {
      String message = String.format(msgFormat, args);
      logInfo(message);
    }
  }


  public void warn(String message) {
    if (LogConfig.logEnabled) {
      logWarning(message);
    }
  }

  public void warn(String format, Object... args) {
    if (LogConfig.logEnabled) {
      String message = String.format(format, args);
      logWarning(message);
    }
  }

  public void error(Exception e) {
    this.error(e.toString());
  }

  public void error(String message) {
    logError(message, null);
  }

  public void error(String msgFormat, Object... args) {
    String msg = String.format(msgFormat, args);
    error(msg);
  }

  public void error(Exception e, String msgFormat, Object... args) {
    String message = String.format(msgFormat, args);
    logError(message, e);
  }

  boolean indentTooLargeWarned = false;

  private void logEnter(int stackTraceNumber, String message) {
    if (LogConfig.logEnabled) {
      String enterPrefix = buildEnterPrefixString(stackTraceNumber + 1);

      String prefixSpaces = getPrefixSpaces();

      String logMsg = String.format("%s%s%s", prefixSpaces, enterPrefix, message);
      sLogImpl.debug(tagName, logMsg);

      if (indent < 20) {
        indent += 2;
      } else {
        if (!indentTooLargeWarned) {
          warn("Indent is too large: %d", indent);
          indentTooLargeWarned = true;
        }
      }
    }
  }

  private void logExit(int stackTraceNumber, String message) {
    if (LogConfig.logEnabled) {
      String exitPrefix = buildExitPrefixString(stackTraceNumber + 1);

      indent -= 2;

      String prefixSpaces = String.format(String.format("%%%ds", indent), "");

      String logMessage = String.format("%s%s%s", prefixSpaces, exitPrefix, message);

      sLogImpl.debug(tagName, logMessage);
    }
  }

  private String buildEnterPrefixString(int stackTraceNumber) {
    Exception exception = new Exception();
    StackTraceElement[] stackTraceElements = exception.getStackTrace();
    StackTraceElement stackTrace = stackTraceElements[stackTraceNumber + 1];
    return String.format("!entering %s::%s. ",
        stackTrace.getClassName(), stackTrace.getMethodName());
  }

  private String buildExitPrefixString(int stackTraceNumber) {
    Exception exception = new Exception();
    StackTraceElement[] stackTraceElements = exception.getStackTrace();
    StackTraceElement stackTrace = stackTraceElements[stackTraceNumber + 1];
    return String.format("~exiting %s::%s. ",
        stackTrace.getClassName(), stackTrace.getMethodName());
  }

  private String getPrefixSpaces() {
    return String.format(String.format("%%%ds", indent), "");
  }

  private void logInfo(String message) {
    sLogImpl.info(tagName, getPrefixSpaces() + message);
  }

  private void logWarning(String message) {
    sLogImpl.warn(tagName, getPrefixSpaces() + message);
  }

  private void logError(String message, Exception e) {
    sLogImpl.error(tagName, getPrefixSpaces() + message, e);
  }

  private final static HashSet<Logger> loggers = new HashSet<Logger>();

  public static Logger getLogger(String tagName) {
    if (tagName == null || tagName.length() == 0) {
      return General;
    }

    Logger logInstance = null;
    synchronized (Logger.class) {
      for (Logger logger : loggers) {
        if (tagName.equals(logger.tagName)) {
          logInstance = logger;
          break;
        }
      }

      if (logInstance == null) {
        logInstance = new Logger(tagName);
        loggers.add(logInstance);
      }
    }

    return logInstance;
  }

  public static Logger General = Logger.getLogger("General");

}
