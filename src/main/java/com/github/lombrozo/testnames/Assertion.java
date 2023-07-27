/*
 * MIT License
 *
 * Copyright (c) 2022-2023 Volodya
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.lombrozo.testnames;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.cactoos.list.ListOf;

/**
 * The assertion of the test method.
 *
 * @since 0.1.15
 */
public interface Assertion {

    /**
     * The assertion message explanation.
     * @return The assertion message explanation.
     */
    Optional<String> explanation();

    /**
     * The assertion arguments.
     *
     * @return The assertion arguments
     */
    List<String> arguments();

    /**
     * The assertion name.
     *
     * @return Name of assertion
     */
    String name();

    /**
     * Fake assertion.
     *
     * @since 0.1.15
     */
    class Fake implements Assertion {

        /**
         * The message.
         */
        private final String message;

        /**
         * Ctor.
         */
        public Fake() {
            this(UUID.randomUUID().toString());
        }

        /**
         * Ctor.
         * @param msg The message.
         */
        public Fake(final String msg) {
            this.message = msg;
        }

        @Override
        public Optional<String> explanation() {
            return Optional.ofNullable(this.message);
        }

        @Override
        public List<String> arguments() {
            return IntStream.range(0, 5)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());
        }

        @Override
        public String name() {
            return this.message;
        }
    }

    /**
     * Empty assertion.
     *
     * @since 0.1.15
     */
    class Empty implements Assertion {

        @Override
        public Optional<String> explanation() {
            return Optional.empty();
        }

        @Override
        public List<String> arguments() {
            return new ListOf<>();
        }

        @Override
        public String name() {
            return "";
        }
    }

}
