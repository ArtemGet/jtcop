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
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithMembers;
import com.github.lombrozo.testnames.Case;
import com.github.lombrozo.testnames.Cases;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Test cases code.
 *
 * @since 0.1.0
 */
public final class JavaTestCode implements Cases {

    /**
     * Path to java class.
     */
    private final Path path;

    /**
     * Ctor.
     *
     * @param file Path to the class
     */
    public JavaTestCode(final Path file) {
        this.path = file;
    }

    @Override
    public Collection<Case> all() {
        try {
            return StaticJavaParser.parse(this.path)
                .getChildNodes()
                .stream()
                .filter(JavaTestCode::isTestClass)
                .flatMap(this::testCases)
                .collect(Collectors.toList());
        } catch (final IOException | ParseProblemException ex) {
            throw new IllegalStateException(
                String.format("Failed to parse Java class by path %s", this.path),
                ex
            );
        }
    }

    /**
     * Checks methods in class.
     *
     * @param node The child node.
     * @return Stream of test cases.
     */
    private Stream<JavaParserCase> testCases(final Node node) {
        final NodeWithMembers<ClassOrInterfaceDeclaration> clazz = (NodeWithMembers) node;
        return clazz.getMethods()
            .stream()
            .filter(JavaTestCode::isTest)
            .map(
                method -> new JavaParserCase(
                    clazz.getNameAsString(),
                    method.getNameAsString(),
                    this.path
                )
            );
    }

    /**
     * Check whether node is class or not.
     * @param node Any node.
     * @return True if test class.
     */
    private static boolean isTestClass(final Node node) {
        return node instanceof ClassOrInterfaceDeclaration;
    }

    /**
     * Check if method test or parameterized test.
     *
     * @param method To check
     * @return Result as boolean
     */
    private static boolean isTest(final MethodDeclaration method) {
        return !method.isPrivate() && (method.isAnnotationPresent("Test")
            || method.isAnnotationPresent("ParameterizedTest"));
    }
}
