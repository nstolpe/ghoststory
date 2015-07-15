package com.hh.ghoststory.utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by nils on 7/15/15.
 */
public class ClassFunction {
	/**
	 * Attempts to call `String method` (which accepts arguments `Object... params` owned by `Object caller`.
	 * Move this to util or at least abstract screen.
	 *
	 * This can be used to call functions with return values or without (those without will return null). When
	 * calling `call` on a method with a return value, it is probably desirous to cast the result object to the
	 * type actually returned by the method being invoked.
	 *
	 * @param caller    The class that owns the method that should be called.
	 * @param method    The method that should be called.
	 * @param params    Parameters required by `method`.
	 * @return          Object (this value should be cast to the actual method's return type)
	 */
	public static Object call(Object caller, String method, Object... params) {
		Method realMethod = null;
		Object result = null;

		Class[] paramClasses = new Class[params.length];

		for (int i = 0; i < params.length; i++) paramClasses[i] = params[i].getClass();

		try {
			realMethod = caller.getClass().getMethod(method, paramClasses);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		try {
			result = realMethod.invoke(caller, params);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return result;
	}
}
