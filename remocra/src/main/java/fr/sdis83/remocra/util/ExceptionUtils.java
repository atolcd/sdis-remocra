/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.sdis83.remocra.util;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;

//import net.sourceforge.htmlunit.corejs.javascript.WrappedException;

public class ExceptionUtils {
    /**
     * Return a reasonable guess about the exception cause, removing any
     * exception that is only meant as a wrapper
     */
    public static Throwable getNestedException(Throwable e) {
        if (e == null) {
            return null;
        }
        Throwable lastVisited = null;
        while (e != lastVisited) {
            lastVisited = e;
            if (e instanceof InvocationTargetException) {
                e = ((InvocationTargetException) e).getTargetException();
                // } else if (e instanceof WrappedException) {
                // e = e.getCause();
            } else if (e instanceof ServletException) {
                e = ((ServletException) e).getRootCause();
            }
            if (e == null) {
                e = lastVisited;
                break;
            }
        }
        return e;
    }

    /**
     * Return the nested exception with the expected exception class
     * 
     * @param e
     *            the exception to start the search
     * @param expectedClass
     *            the expected class.
     * @return the nested exception with the expected class, null if none
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T getNestedExceptionWithClass(Throwable e, Class<T> expectedClass) {
        while (e != null && !expectedClass.isAssignableFrom(e.getClass())) {
            if (e instanceof InvocationTargetException) {
                e = ((InvocationTargetException) e).getTargetException();
                // } else if (e instanceof WrappedException) {
                // e = e.getCause();
            } else if (e instanceof ServletException) {
                e = ((ServletException) e).getRootCause();
            } else {
                e = e.getCause();
            }
        }
        return (T) e;
    }
}
