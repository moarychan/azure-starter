package com.microsoft.cas.discovery.event.binding;

/**
 * Given a prefix, iterates between all the binding that match the prefix. The
 * iteration order is determined by the implementing class. For example given
 * the following bindings
 *
 * spring:
 *   cloud:
 *     stream:
 *       bindings:
 *         my-output-1:
 *           destination: queue-1
 *         my-output-2:
 *           destination: queue-2
 *         your-output-1
 *          destination: queue-3
 *         your-output-2
 *          destination: queue-4
 *
 * Then calling `nextBinding("my-")` will iterate over the bindings
 * "my-output-1", "my-output-2" only.
 */
public interface BindingSelector {

    /**
     * Receive the next binding that abides to the given regular expression.
     * @param regex Regular expression to match the bindings
     * @return The next binding the abides the expression, in a cyclic manner.
     */
    default String nextBinding(String regex) {
        return nextBinding(regex, false);
    }

    /**
     * Receive the next binding that abides to the given regular expression,
     * with an option to add the default binder even if it does not abide to the
     * expression.
     * @param regex Regular expression to match the bindings
     * @param useDefaultBinding If true will add the default binder even if it
     *                          does not conform to the regular expression.
     * @return The next binding the abides the expression, in a cyclic manner.
     */
    String nextBinding(String regex, boolean useDefaultBinding);

}
