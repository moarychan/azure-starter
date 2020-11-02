package com.microsoft.cas.discovery.event.binding;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.stream.config.BindingServiceProperties;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Given a prefix, iterates between all the binding that match the prefix in a
 * round robin fashion. For example given the following bindings:
 *
 * spring:
 *   cloud:
 *     stream:
 *       bindings:
 *         my-output-1:
 *           destination: my-queue-1
 *         my-output-2:
 *           destination: my-queue-2
 *         my-output-3
 *          destination: my-queue-3
 *
 * Then calling `nextBinding("my-")` multiple times will produce the binding
 * sequence ["my-queue-1", "my-queue-2", "my-queue-3", "my-queue-1" ...]
 */
@Slf4j
public class RoundRobinBindingSelector implements BindingSelector {

    @Value
    private static class Binding {
        AtomicInteger index;
        List<String> bindings;

        public Binding(@NotNull String[] bindings, boolean useDefault) {
            this.bindings = new LinkedList<>(Arrays.asList(bindings));
            if (useDefault) {
                log.info("Adding default binder usage");
                this.bindings.add(null);
            }
            this.index = new AtomicInteger();
        }

        public String next() {
            return bindings.get(index.getAndUpdate(i -> i < bindings.size() - 1 ? i + 1 : 0));
        }
    }

    private final BindingServiceProperties bindingProperties;

    private final Map<Pair<String, Boolean>, Binding> byPrefix;

    public RoundRobinBindingSelector(BindingServiceProperties bindingProperties) {
        this.bindingProperties = bindingProperties;
        this.byPrefix = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String nextBinding(String regex, boolean useDefault) {
        Pair<String, Boolean> key = Pair.of(regex, useDefault);
        String next = byPrefix.computeIfAbsent
                (key, p -> buildBinding(p.getLeft(), p.getRight())).next();
        log.debug("Binding is '{}'", next);
        return next;
    }

    @NotNull
    private Binding buildBinding(String regex, boolean useDefault) {
        return new Binding(
                bindingProperties.getBindings().keySet().stream()
                        .filter(s -> s.matches(regex)).toArray(String[]::new),
                useDefault
        );
    }

}
