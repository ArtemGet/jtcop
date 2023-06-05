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
package com.github.lombrozo.testnames.javaparser;

import com.github.lombrozo.testnames.Assertion;
import com.github.lombrozo.testnames.TestCase;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link AssertionOfJUnit}.
 *
 * @since 0.1.15
 */
class AssertionOfJUnitTest {

    /**
     * Method name for test with messages.
     */
    private static final String WITH_MESSAGES = "withMessages";

    /**
     * Method name for test without messages.
     */
    private static final String WITHOUT_MESSAGES = "withoutMessages";

    /**
     * Method name for test with special assertions.
     */
    private static final String SPECIAL_MESSAGES = "specialAssertions";

    @Test
    void parsesJUnitAssertionsAllPresent() {
        final int expected = 34;
        final int actual = AssertionOfJUnitTest.method(AssertionOfJUnitTest.WITH_MESSAGES)
            .assertions().size();
        MatcherAssert.assertThat(
            String.format("We expect to parse %d assertions, but was %s", expected, actual),
            actual,
            Matchers.is(expected)
        );
    }

    @Test
    void parsesJUnitAssertionsAllHaveExplanation() {
        MatcherAssert.assertThat(
            "All assertions should have explanation assertions",
            AssertionOfJUnitTest.method(AssertionOfJUnitTest.WITH_MESSAGES)
                .assertions()
                .stream()
                .map(Assertion::explanation)
                .allMatch(Optional::isPresent),
            Matchers.is(true)
        );
    }

    @Test
    void parsesJUnitAssertionsNoneHasEmptyExplanation() {
        MatcherAssert.assertThat(
            "All assertions should have JUnit explanation text",
            AssertionOfJUnitTest.method(AssertionOfJUnitTest.WITH_MESSAGES)
                .assertions().stream()
                .map(Assertion::explanation)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .noneMatch(String::isEmpty),
            Matchers.is(true)
        );
    }

    @Test
    void parsesAssertionsWithoutMessagesAllAreParsed() {
        final int expected = 17;
        final int actual = AssertionOfJUnitTest.method(AssertionOfJUnitTest.WITHOUT_MESSAGES)
            .assertions().size();
        MatcherAssert.assertThat(
            String.format("We expect to parse %d empty assertions, but was %s", expected, actual),
            actual,
            Matchers.is(expected)
        );
    }

    @Test
    void parsesAssertionsWithoutMessage() {
        final Collection<Assertion> assertions = AssertionOfJUnitTest.method(
                AssertionOfJUnitTest.WITHOUT_MESSAGES)
            .assertions();
        MatcherAssert.assertThat(
            String.format(
                "All assertions should be without assertion message, but was: %s",
                assertions
                    .stream()
                    .map(Assertion::explanation)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList())
            ),
            assertions
                .stream()
                .map(Assertion::explanation)
                .noneMatch(Optional::isPresent),
            Matchers.is(true)
        );
    }

    @Test
    void parsesAllSpecialAssertionsAllAreParsed() {
        final int expected = 6;
        final int actual = AssertionOfJUnitTest.method(AssertionOfJUnitTest.SPECIAL_MESSAGES)
            .assertions().size();
        MatcherAssert.assertThat(
            String.format("We expect to parse %d special assertions, but was %s", expected, actual),
            actual,
            Matchers.is(expected)
        );
    }

    @Test
    void ignoresFailAssertion() {
        MatcherAssert.assertThat(
            "We should add fake explanations for some assertions",
            AssertionOfJUnitTest.method(AssertionOfJUnitTest.SPECIAL_MESSAGES)
                .assertions()
                .stream()
                .map(Assertion::explanation)
                .allMatch(Optional::isPresent),
            Matchers.is(true)
        );
    }

    /**
     * Returns test case by name.
     * @param name Name of test case.
     * @return Test case.
     */
    private static TestCase method(final String name) {
        return JavaTestClasses.TEST_WITH_JUNIT_ASSERTIONS
            .javaParserClass().all().stream()
            .filter(method -> name.equals(method.name()))
            .findFirst()
            .orElseThrow(
                () -> {
                    throw new IllegalStateException(String.format("Method not found: %s", name));
                }
            );
    }
}
