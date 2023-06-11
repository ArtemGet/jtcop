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

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.lombrozo.testnames.TestCase;
import com.github.lombrozo.testnames.TestClass;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cactoos.scalar.Mapped;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;

/**
 * Test cases code.
 *
 * @since 0.1.0
 */
public final class TestClassJavaParser implements TestClass {

    /**
     * Path to java class.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    private final Path path;

    /**
     * Parsed Java class.
     */
    private final Unchecked<JavaParserClass> unit;

    /**
     * Rules excluded for entire project.
     */
    private final Collection<String> exclusions;

    /**
     * Ctor.
     *
     * @param klass Path to the class
     */
    TestClassJavaParser(final Path klass) {
        this(klass, new Sticky<>(() -> StaticJavaParser.parse(klass)));
    }

    /**
     * Ctor.
     *
     * @param klass Path to the class
     * @param exclusions Rules excluded for entire project.
     */
    TestClassJavaParser(final Path klass, final Collection<String> exclusions) {
        this(klass, new Sticky<>(() -> StaticJavaParser.parse(klass)), exclusions);
    }

    /**
     * Ctor.
     *
     * @param klass Path to the class
     * @param stream Parsed Java class
     */
    TestClassJavaParser(final Path klass, final InputStream stream) {
        this(klass, new Sticky<>(() -> StaticJavaParser.parse(stream)));
    }

    /**
     * Ctor.
     *
     * @param klass Path to the class
     * @param parsed Parsed class.
     */
    private TestClassJavaParser(final Path klass, final Sticky<? extends CompilationUnit> parsed) {
        this(klass, parsed, Collections.emptySet());
    }

    /**
     * Ctor.
     *
     * @param klass Path to the class
     * @param stream Parsed Java class
     * @param exclusions Rules excluded for entire project.
     */
    TestClassJavaParser(
        final Path klass,
        final InputStream stream,
        final Collection<String> exclusions
    ) {
        this(klass, new Sticky<>(() -> StaticJavaParser.parse(stream)), exclusions);
    }

    /**
     * Ctor.
     *
     * @param klass Path to the class
     * @param parsed Parsed class.
     * @param exclusions Rules excluded for entire project.
     */
    TestClassJavaParser(
        final Path klass,
        final Sticky<? extends CompilationUnit> parsed,
        final Collection<String> exclusions
    ) {
        this.path = klass;
        this.unit = new Unchecked<>(new Mapped<>(JavaParserClass::new, parsed));
        this.exclusions = exclusions;
    }

    @Override
    public String name() {
        return this.path.getFileName().toString();
    }

    @Override
    public Collection<TestCase> all() {
        try {
            return this.unit.value()
                .methods(new TestsOnly())
                .map(method -> new TestCaseJavaParser(method, this))
                .collect(Collectors.toSet());
        } catch (final UncheckedIOException | ParseProblemException ex) {
            throw new IllegalStateException(
                String.format("Failed to parse Java class by path %s", this.path),
                ex
            );
        }
    }

    @Override
    public Path path() {
        return this.path;
    }

    @Override
    public Collection<String> suppressed() {
        return Stream.concat(
            this.unit.value().annotations().suppressed(),
            this.exclusions.stream()
        ).collect(Collectors.toSet());
    }

}
