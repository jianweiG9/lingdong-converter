/*
 * Document Converter - A local document converter based on LibreOffice
 * Migrated from JODConverter project
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

package com.docconverter.core.util;

import java.util.Collection;

/**
 * Contains assertions helper functions for parameter validation.
 * 
 * <p>This utility class provides various assertion methods to validate method parameters
 * and object states. All methods throw appropriate runtime exceptions when validation fails.
 * 
 * @author Document Converter Team
 * @since 1.0.0
 */
public final class AssertUtils {

    /**
     * Validates that the argument condition is {@code true}.
     *
     * @param expression The boolean expression to validate
     * @param message The exception message to use if the assertion fails
     * @throws IllegalArgumentException If expression is {@code false}
     */
    public static void isTrue(final boolean expression, final String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates that the specified string is neither {@code null}, nor empty, nor blank (only whitespace).
     *
     * @param str The string to validate
     * @param message The exception message to use if the assertion fails
     * @throws NullPointerException If the string is {@code null}
     * @throws IllegalArgumentException If the string is blank
     */
    public static void notBlank(final String str, final String message) {
        if (str == null) {
            throw new NullPointerException(message);
        }
        if (str.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return;
            }
        }
        throw new IllegalArgumentException(message);
    }

    /**
     * Validates that the specified argument collection is neither {@code null} nor empty.
     *
     * @param collection The collection to validate
     * @param message The exception message to use if the assertion fails
     * @param <T> The type of the elements in the collection
     * @throws NullPointerException If the collection is {@code null}
     * @throws IllegalArgumentException If the collection is empty
     */
    public static <T> void notEmpty(final Collection<T> collection, final String message) {
        if (collection == null) {
            throw new NullPointerException(message);
        }
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates that the specified argument array is neither {@code null} nor empty.
     *
     * @param array The array to validate
     * @param message The exception message to use if the assertion fails
     * @param <T> The type of the elements in the array
     * @throws NullPointerException If the array is {@code null}
     * @throws IllegalArgumentException If the array is empty
     */
    public static <T> void notEmpty(final T[] array, final String message) {
        if (array == null) {
            throw new NullPointerException(message);
        }
        if (array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates that the specified string is neither {@code null} nor empty.
     *
     * @param str The string to validate
     * @param message The exception message to use if the assertion fails
     * @throws NullPointerException If the string is {@code null}
     * @throws IllegalArgumentException If the string is empty
     */
    public static void notEmpty(final String str, final String message) {
        if (str == null) {
            throw new NullPointerException(message);
        }
        if (str.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates that the specified argument is not {@code null}.
     *
     * @param object The object to validate
     * @param message The exception message to use if the assertion fails
     * @throws NullPointerException If the object is {@code null}
     */
    public static void notNull(final Object object, final String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
    }

    // Suppresses default constructor, ensuring non-instantiability.
    private AssertUtils() {
        throw new AssertionError("Utility class must not be instantiated");
    }
}