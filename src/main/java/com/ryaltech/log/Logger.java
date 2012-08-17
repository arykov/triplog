package com.ryaltech.log;

import org.slf4j.LoggerFactory;

/**
 * Logging class that brings Java5 features to slf4j. In addition to the regular
 * debug, info, warn, and error methods from slf4j, this class implements
 * equivalents that take a variable number of args and formats them using
 * {@link String#format(String, Object...)}. Example usage is shown below:
 * 
 * <pre>
 * public class MyClass {
 * 	private static final Logger log = Logger.getLogger(MyClass.class);
 * 
 * 	public void myMethod(String name) {
 * 		log.debug(&quot;Hi, my name is %s&quot;, name);
 * 	}
 * }
 * </pre>
 * 
 * <p>
 * Note that we implement the formatted methods separately, rather than just
 * adding varargs to the standard methods, to avoid mistakes like this, which
 * cause an error in {@link String#format(String, Object...)} due to the
 * spurious percent sign:
 * </p>
 * 
 * <pre>
 * log.debug(&quot;Your discount is 10%&quot;);
 * </pre>
 * 
 * 
 * @author Rykov
 */

public class Logger {
	public static Logger getLogger(Class<? extends Object> clazz) {
		return new Logger(LoggerFactory.getLogger(clazz.getName()));
	}

	private org.slf4j.Logger impl;

	private Logger(org.slf4j.Logger impl) {
		this.impl = impl;
	}

	public void debug(String message) {
		this.impl.debug(message);
	}

	public void debug(String message, Object... args) {
		if (this.impl.isDebugEnabled()) {
			this.impl.debug(String.format(message, args));
		}
	}

	public void error(String message) {
		this.impl.error(message);
	}

	public void error(String message, Object... args) {
		this.impl.error(String.format(message, args));
	}

	public void error(Throwable t, String message) {
		this.impl.error(message, t);
	}

	public void error(Throwable t, String message, Object... args) {
		this.impl.error(String.format(message, args), t);
	}

	public void info(String message) {
		this.impl.info(message);
	}

	public void info(String message, Object... args) {
		if (this.impl.isInfoEnabled()) {
			this.impl.info(String.format(message, args));
		}
	}

	public void warn(String message) {
		this.impl.warn(message);
	}

	public void warn(String message, Object... args) {
		this.impl.warn(String.format(message, args));
	}

	public void warn(Throwable t, String message) {
		this.impl.error(message, t);
	}

	public void warn(Throwable t, String message, Object... args) {
		this.impl.error(String.format(message, args), t);
	}

}
