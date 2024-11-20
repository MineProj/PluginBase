/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.io.build;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Abstracts building an instance of {@code T}.
 *
 * @param <T> the type of instances to build.
 * @param <B> the type of builder subclass.
 * @since 2.12.0
 */
public abstract class AbstractOriginSupplier<T, B extends AbstractOriginSupplier<T, B>> extends AbstractSupplier<T, B> {

    /**
     * Constructs a new byte array origin for a byte array.
     *
     * @param origin the byte array.
     * @return a new byte array origin.
     */
    protected static AbstractOrigin.ByteArrayOrigin newByteArrayOrigin(final byte[] origin) {
        return new AbstractOrigin.ByteArrayOrigin(origin);
    }

    /**
     * Constructs a new CharSequence origin for a CharSequence.
     *
     * @param origin the CharSequence.
     * @return a new file origin.
     * @since 2.13.0
     */
    protected static AbstractOrigin.CharSequenceOrigin newCharSequenceOrigin(final CharSequence origin) {
        return new AbstractOrigin.CharSequenceOrigin(origin);
    }

    /**
     * Constructs a new file origin for a file.
     *
     * @param origin the file.
     * @return a new file origin.
     */
    protected static AbstractOrigin.FileOrigin newFileOrigin(final File origin) {
        return new AbstractOrigin.FileOrigin(origin);
    }

    /**
     * Constructs a new file origin for a file path.
     *
     * @param origin the file path.
     * @return a new file origin.
     */
    protected static AbstractOrigin.FileOrigin newFileOrigin(final String origin) {
        return new AbstractOrigin.FileOrigin(new File(origin));
    }

    /**
     * Constructs a new input stream origin for a file.
     *
     * @param origin the input stream.
     * @return a new input stream origin.
     */
    protected static AbstractOrigin.InputStreamOrigin newInputStreamOrigin(final InputStream origin) {
        return new AbstractOrigin.InputStreamOrigin(origin);
    }

    /**
     * Constructs a new output stream origin for a file.
     *
     * @param origin the output stream.
     * @return a new output stream origin.
     */
    protected static AbstractOrigin.OutputStreamOrigin newOutputStreamOrigin(final OutputStream origin) {
        return new AbstractOrigin.OutputStreamOrigin(origin);
    }

    /**
     * Constructs a new path origin for a file.
     *
     * @param origin the path.
     * @return a new path origin.
     */
    protected static AbstractOrigin.PathOrigin newPathOrigin(final Path origin) {
        return new AbstractOrigin.PathOrigin(origin);
    }

    /**
     * Constructs a new path name origin for a path name.
     *
     * @param origin the path name.
     * @return a new path name origin.
     */
    protected static AbstractOrigin.PathOrigin newPathOrigin(final String origin) {
        return new AbstractOrigin.PathOrigin(Paths.get(origin));
    }

    /**
     * Constructs a new reader origin for a reader.
     *
     * @param origin the reader.
     * @return a new reader origin.
     */
    protected static AbstractOrigin.ReaderOrigin newReaderOrigin(final Reader origin) {
        return new AbstractOrigin.ReaderOrigin(origin);
    }

    /**
     * Constructs a new reader origin for a URI.
     *
     * @param origin the URI.
     * @return a new URI origin.
     */
    protected static AbstractOrigin.URIOrigin newURIOrigin(final URI origin) {
        return new AbstractOrigin.URIOrigin(origin);
    }

    /**
     * Constructs a new writer origin for a file.
     *
     * @param origin the writer.
     * @return a new writer .
     */
    protected static AbstractOrigin.WriterOrigin newWriterOrigin(final Writer origin) {
        return new AbstractOrigin.WriterOrigin(origin);
    }

    /**
     * The underlying origin.
     */
    private AbstractOrigin<?, ?> origin;

    /**
     * Checks whether the origin is null.
     *
     * @return the origin.
     * @throws IllegalStateException if the {@code origin} is {@code null}.
     */
    protected AbstractOrigin<?, ?> checkOrigin() {
        if (origin == null) {
            throw new IllegalStateException("origin == null");
        }
        return origin;
    }

    /**
     * Gets the origin.
     *
     * @return the origin.
     */
    protected AbstractOrigin<?, ?> getOrigin() {
        return origin;
    }

    /**
     * Tests whether the origin is null.
     *
     * @return whether the origin is null.
     */
    protected boolean hasOrigin() {
        return origin != null;
    }

    /**
     * Sets a new origin.
     *
     * @param origin the new origin.
     * @return this
     */
    public B setByteArray(final byte[] origin) {
        return setOrigin(newByteArrayOrigin(origin));
    }

    /**
     * Sets a new origin.
     *
     * @param origin the new origin.
     * @return this
     * @since 2.13.0
     */
    public B setCharSequence(final CharSequence origin) {
        return setOrigin(newCharSequenceOrigin(origin));
    }

    /**
     * Sets a new origin.
     *
     * @param origin the new origin.
     * @return this
     */
    public B setFile(final File origin) {
        return setOrigin(newFileOrigin(origin));
    }

    /**
     * Sets a new origin.
     *
     * @param origin the new origin.
     * @return this
     */
    public B setFile(final String origin) {
        return setOrigin(newFileOrigin(origin));
    }

    /**
     * Sets a new origin.
     *
     * @param origin the new origin.
     * @return this
     */
    public B setInputStream(final InputStream origin) {
        return setOrigin(newInputStreamOrigin(origin));
    }

    /**
     * Sets a new origin.
     *
     * @param origin the new origin.
     * @return this
     */
    protected B setOrigin(final AbstractOrigin<?, ?> origin) {
        this.origin = origin;
        return asThis();
    }

    /**
     * Sets a new origin.
     *
     * @param origin the new origin.
     * @return this
     */
    public B setOutputStream(final OutputStream origin) {
        return setOrigin(newOutputStreamOrigin(origin));
    }

    /**
     * Sets a new origin.
     *
     * @param origin the new origin.
     * @return this
     */
    public B setPath(final Path origin) {
        return setOrigin(newPathOrigin(origin));
    }

    /**
     * Sets a new origin.
     *
     * @param origin the new origin.
     * @return this
     */
    public B setPath(final String origin) {
        return setOrigin(newPathOrigin(origin));
    }

    /**
     * Sets a new origin.
     *
     * @param origin the new origin.
     * @return this
     */
    public B setReader(final Reader origin) {
        return setOrigin(newReaderOrigin(origin));
    }

    /**
     * Sets a new origin.
     *
     * @param origin the new origin.
     * @return this
     */
    public B setURI(final URI origin) {
        return setOrigin(newURIOrigin(origin));
    }

    /**
     * Sets a new origin.
     *
     * @param origin the new origin.
     * @return this
     */
    public B setWriter(final Writer origin) {
        return setOrigin(newWriterOrigin(origin));
    }
}