package com.wuxibus.app.util;

import android.util.Log;

import java.util.Hashtable;

/**
 * 用于显示错误提示
 * @author tianru@touch-spring.com 
 * @qq 2385024463@qq.com
 * @data 2014-7-31
 *
 */
public class Log4j {
	private final static boolean logFlag = true;

	public final static String tag = "bus";
	private final static int logLevel = Log.VERBOSE;
	private static Hashtable<String, Log4j> sLoggerTable = new Hashtable<String, Log4j>();
	private String mClassName;

	private static Log4j log;
	public static boolean flag = true;

	private static final String JAMES = "bus ";

	private Log4j(String name) {
		mClassName = name;
	}

	/**
	 * 
	 * @param className
	 * @return
	 */
	@SuppressWarnings("unused")
	private static Log4j getLogger(String className) {
		Log4j classLogger = (Log4j) sLoggerTable.get(className);
		if (classLogger == null) {
			classLogger = new Log4j(className);
			sLoggerTable.put(className, classLogger);
		}
		return classLogger;
	}

	/**
	 * Purpose:Mark user two
	 * 
	 * @return
	 */
	public static Log4j Log() {
		if (log == null) {
			log = new Log4j(JAMES);
		}
		return log;
	}

	/**
	 * Get The Current Function Name
	 * 
	 * @return
	 */
	private String getFunctionName() {
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		if (sts == null) {
			return null;
		}
		for (StackTraceElement st : sts) {
			if (st.isNativeMethod()) {
				continue;
			}
			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}
			if (st.getClassName().equals(this.getClass().getName())) {
				continue;
			}
			return mClassName + "[ " + Thread.currentThread().getName() + ": "
					+ st.getFileName() + ":" + st.getLineNumber() + " "
					+ st.getMethodName() + " ]";
		}
		return null;
	}

	/**
	 * The Log Level:i
	 * 
	 * @param str
	 */
	public void i(Object str) {
		if (flag) {
			if (logFlag) {
				if (logLevel <= Log.INFO) {
					String name = getFunctionName();
					if (name != null) {
						Log.i(tag, name + " - " + str);
					} else {
						Log.i(tag, str.toString());					
					}
				}
			}
		} else {
			return;
		}

	}

	/**
	 * The Log Level:d
	 * 
	 * @param str
	 */
	public void d(Object str) {
		if (flag) {
			if (logFlag) {
				if (logLevel <= Log.DEBUG) {
					String name = getFunctionName();
					if (name != null) {
						Log.d(tag, name + " - " + str);
					} else {
						Log.d(tag, str.toString());
					}
				}
			}
		}
	}

	/**
	 * The Log Level:V
	 * 
	 * @param str
	 */
	public void v(Object str) {
		if (flag) {
			if (logFlag) {
				if (logLevel <= Log.VERBOSE) {
					String name = getFunctionName();
					if (name != null) {
						Log.v(tag, name + " - " + str);
					} else {
						Log.v(tag, str.toString());
					}
				}
			}
		} else {
			return;
		}
	}

	/**
	 * The Log Level:w
	 * 
	 * @param str
	 */
	public void w(Object str) {
		if (flag) {
			if (logFlag) {
				if (logLevel <= Log.WARN) {
					String name = getFunctionName();
					if (name != null) {
						Log.w(tag, name + " - " + str);
					} else {
						Log.w(tag, str.toString());
					}
				}
			}
		} else {
			return;
		}
	}

	/**
	 * The Log Level:e
	 * 
	 * @param str
	 */
	public void e(Object str) {
		if (flag) {
			if (logFlag) {
				if (logLevel <= Log.ERROR) {
					String name = getFunctionName();
					if (name != null) {
						Log.e(tag, name + " - " + str);
					} else {
						Log.e(tag, str.toString());
					}
				}
			}
		} else {
			return;
		}
	}

	/**
	 * The Log Level:e
	 * 
	 * @param ex
	 */
	public void e(Exception ex) {
		if (logFlag) {
			if (logLevel <= Log.ERROR) {
				Log.e(tag, "error", ex);
			}
		}
	}

	/**
	 * The Log Level:e
	 * 
	 * @param log
	 * @param tr
	 */
	public void e(String log, Throwable tr) {
		if (logFlag) {
			String line = getFunctionName();
			Log.e(tag, "{Thread:" + Thread.currentThread().getName() + "}"
					+ "[" + mClassName + line + ":] " + log + "\n", tr);
		}
	}
}
