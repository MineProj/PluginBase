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

package org.apache.commons.io.function;

import org.apache.commons.io.IOExceptionList;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import java.util.stream.*;

/**
 * Like {@link Stream} but throws {@link IOException}.
 *
 * @param <T> the type of the stream elements.
 * @since 2.12.0
 */
public interface IOStream<T> extends IOBaseStream<T, IOStream<T>, Stream<T>> {

    /**
     * Constructs a new IOStream for the given Stream.
     *
     * @param <T> the type of the stream elements.
     * @param stream The stream to delegate.
     * @return a new IOStream.
     */
    static <T> IOStream<T> adapt(final Stream<T> stream) {
        return IOStreamAdapter.adapt(stream);
    }

    /**
     * This class' version of {@link Stream#empty()}.
     *
     * @param <T> the type of the stream elements
     * @return an empty sequential {@code IOStreamImpl}.
     * @see Stream#empty()
     */
    static <T> IOStream<T> empty() {
        return IOStreamAdapter.adapt(Stream.empty());
    }

    /**
     * Like {@link Stream#iterate(Object, UnaryOperator)} but for IO.
     *
     * @param <T> the type of stream elements.
     * @param seed the initial element.
     * @param f a function to be applied to the previous element to produce a new element.
     * @return a new sequential {@code IOStream}.
     */
    static <T> IOStream<T> iterate(final T seed, final IOUnaryOperator<T> f) {
        Objects.requireNonNull(f);
        final Iterator<T> iterator = new Iterator<T>() {
            @SuppressWarnings("unchecked")
            T t = (T) IOStreams.NONE;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public T next() throws NoSuchElementException {
                try {
                    return t = t == IOStreams.NONE ? seed : f.apply(t);
                } catch (IOException e) {
                    final NoSuchElementException nsee = new NoSuchElementException();
                    nsee.initCause(e);
                    throw nsee;
                }
            }
        };
        return adapt(StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.IMMUTABLE), false));
    }

    /**
     * Null-safe version of {@link StreamSupport#stream(Spliterator, boolean)}.
     *
     * Copied from Apache Commons Lang.
     *
     * @param <T> the type of stream elements.
     * @param values the elements of the new stream, may be {@code null}.
     * @return the new stream on {@code values} or {@link Stream#empty()}.
     */
    @SuppressWarnings("resource") // call to #empty()
    static <T> IOStream<T> of(final Iterable<T> values) {
        return values == null ? empty() : adapt(StreamSupport.stream(values.spliterator(), false));
    }

    /**
     * Null-safe version of {@link Stream#of(Object[])} for an IO stream.
     *
     * @param <T> the type of stream elements.
     * @param values the elements of the new stream, may be {@code null}.
     * @return the new stream on {@code values} or {@link Stream#empty()}.
     */
    @SuppressWarnings("resource")
    @SafeVarargs // Creating a stream from an array is safe
    static <T> IOStream<T> of(final T... values) {
        return values == null || values.length == 0 ? empty() : adapt(Arrays.stream(values));
    }

    /**
     * Returns a sequential {@code IOStreamImpl} containing a single element.
     *
     * @param t the single element
     * @param <T> the type of stream elements
     * @return a singleton sequential stream
     */
    static <T> IOStream<T> of(final T t) {
        return adapt(Stream.of(t));
    }

    /**
     * Like {@link Stream#allMatch(Predicate)} but throws {@link IOException}.
     *
     * @param predicate {@link Stream#allMatch(Predicate)}.
     * @return Like {@link Stream#allMatch(Predicate)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default boolean allMatch(final IOPredicate<? super T> predicate) throws IOException {
        return unwrap().allMatch(t -> Erase.test(predicate, t));
    }

    /**
     * Like {@link Stream#anyMatch(Predicate)} but throws {@link IOException}.
     *
     * @param predicate {@link Stream#anyMatch(Predicate)}.
     * @return Like {@link Stream#anyMatch(Predicate)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default boolean anyMatch(final IOPredicate<? super T> predicate) throws IOException {
        return unwrap().anyMatch(t -> Erase.test(predicate, t));
    }

    /**
     * TODO Package-private for now, needs IOCollector?
     *
     * Adding this method now and an IO version later is an issue because call sites would have to type-cast to pick one. It
     * would be ideal to have only one.
     *
     * Like {@link Stream#collect(Collector)}.
     *
     * Package private for now.
     *
     * @param <R> Like {@link Stream#collect(Collector)}.
     * @param <A> Like {@link Stream#collect(Collector)}.
     * @param collector Like {@link Stream#collect(Collector)}.
     * @return Like {@link Stream#collect(Collector)}.
     */
    default <R, A> R collect(final Collector<? super T, A, R> collector) {
        return unwrap().collect(collector);
    }

    /**
     * Like
     * {@link Stream#collect(Supplier, BiConsumer, BiConsumer)}.
     *
     * @param <R> Like
     *        {@link Stream#collect(Supplier, BiConsumer, BiConsumer)}.
     * @param supplier Like
     *        {@link Stream#collect(Supplier, BiConsumer, BiConsumer)}.
     * @param accumulator Like
     *        {@link Stream#collect(Supplier, BiConsumer, BiConsumer)}.
     * @param combiner Like
     *        {@link Stream#collect(Supplier, BiConsumer, BiConsumer)}.
     * @return Like
     *         {@link Stream#collect(Supplier, BiConsumer, BiConsumer)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default <R> R collect(final IOSupplier<R> supplier, final IOBiConsumer<R, ? super T> accumulator, final IOBiConsumer<R, R> combiner) throws IOException {
        return unwrap().collect(() -> Erase.get(supplier), (t, u) -> Erase.accept(accumulator, t, u), (t, u) -> Erase.accept(combiner, t, u));
    }

    /**
     * Like {@link Stream#count()}.
     *
     * @return Like {@link Stream#count()}.
     */
    default long count() {
        return unwrap().count();
    }

    /**
     * Like {@link Stream#distinct()}.
     *
     * @return Like {@link Stream#distinct()}.
     */
    default IOStream<T> distinct() {
        return adapt(unwrap().distinct());
    }

    /**
     * Like {@link Stream#filter(Predicate)}.
     *
     * @param predicate Like {@link Stream#filter(Predicate)}.
     * @return Like {@link Stream#filter(Predicate)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default IOStream<T> filter(final IOPredicate<? super T> predicate) throws IOException {
        return adapt(unwrap().filter(t -> Erase.test(predicate, t)));
    }

    /**
     * Like {@link Stream#findAny()}.
     *
     * @return Like {@link Stream#findAny()}.
     */
    default Optional<T> findAny() {
        return unwrap().findAny();
    }

    /**
     * Like {@link Stream#findFirst()}.
     *
     * @return Like {@link Stream#findFirst()}.
     */
    default Optional<T> findFirst() {
        return unwrap().findFirst();
    }

    /**
     * Like {@link Stream#flatMap(Function)}.
     *
     * @param <R> Like {@link Stream#flatMap(Function)}.
     * @param mapper Like {@link Stream#flatMap(Function)}.
     * @return Like {@link Stream#flatMap(Function)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default <R> IOStream<R> flatMap(final IOFunction<? super T, ? extends IOStream<? extends R>> mapper) throws IOException {
        return adapt(unwrap().flatMap(t -> Erase.apply(mapper, t).unwrap()));
    }

    /**
     * TODO Package-private for now, needs IODoubleStream?
     *
     * Adding this method now and an IO version later is an issue because call sites would have to type-cast to pick one. It
     * would be ideal to have only one.
     *
     * Like {@link Stream#flatMapToDouble(Function)}.
     *
     * @param mapper Like {@link Stream#flatMapToDouble(Function)}.
     * @return Like {@link Stream#flatMapToDouble(Function)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default DoubleStream flatMapToDouble(final IOFunction<? super T, ? extends DoubleStream> mapper) throws IOException {
        return unwrap().flatMapToDouble(t -> Erase.apply(mapper, t));
    }

    /**
     * TODO Package-private for now, needs IOIntStream?
     *
     * Adding this method now and an IO version later is an issue because call sites would have to type-cast to pick one. It
     * would be ideal to have only one.
     *
     * Like {@link Stream#flatMapToInt(Function)}.
     *
     * @param mapper Like {@link Stream#flatMapToInt(Function)}.
     * @return Like {@link Stream#flatMapToInt(Function)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default IntStream flatMapToInt(final IOFunction<? super T, ? extends IntStream> mapper) throws IOException {
        return unwrap().flatMapToInt(t -> Erase.apply(mapper, t));
    }

    /**
     * TODO Package-private for now, needs IOLongStream?
     *
     * Adding this method now and an IO version later is an issue because call sites would have to type-cast to pick one. It
     * would be ideal to have only one.
     *
     * Like {@link Stream#flatMapToLong(Function)}.
     *
     * @param mapper Like {@link Stream#flatMapToLong(Function)}.
     * @return Like {@link Stream#flatMapToLong(Function)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default LongStream flatMapToLong(final IOFunction<? super T, ? extends LongStream> mapper) throws IOException {
        return unwrap().flatMapToLong(t -> Erase.apply(mapper, t));
    }

    /**
     * Performs an action for each element gathering any exceptions.
     *
     * @param action The action to apply to each element.
     * @throws IOExceptionList if any I/O errors occur.
     */
    default void forAll(final IOConsumer<T> action) throws IOExceptionList {
        forAll(action, (i, e) -> e);
    }

    /**
     * Performs an action for each element gathering any exceptions.
     *
     * @param action The action to apply to each element.
     * @param exSupplier The exception supplier.
     * @throws IOExceptionList if any I/O errors occur.
     */
    default void forAll(final IOConsumer<T> action, final BiFunction<Integer, IOException, IOException> exSupplier) throws IOExceptionList {
        final AtomicReference<List<IOException>> causeList = new AtomicReference<>();
        final AtomicInteger index = new AtomicInteger();
        final IOConsumer<T> safeAction = IOStreams.toIOConsumer(action);
        unwrap().forEach(e -> {
            try {
                safeAction.accept(e);
            } catch (final IOException innerEx) {
                if (causeList.get() == null) {
                    // Only allocate if required
                    causeList.set(new ArrayList<>());
                }
                if (exSupplier != null) {
                    causeList.get().add(exSupplier.apply(index.get(), innerEx));
                }
            }
            index.incrementAndGet();
        });
        IOExceptionList.checkEmpty(causeList.get(), null);
    }

    /**
     * Like {@link Stream#forEach(Consumer)} but throws {@link IOException}.
     *
     * @param action Like {@link Stream#forEach(Consumer)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default void forEach(final IOConsumer<? super T> action) throws IOException {
        unwrap().forEach(e -> Erase.accept(action, e));
    }

    /**
     * Like {@link Stream#forEachOrdered(Consumer)}.
     *
     * @param action Like {@link Stream#forEachOrdered(Consumer)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default void forEachOrdered(final IOConsumer<? super T> action) throws IOException {
        unwrap().forEachOrdered(e -> Erase.accept(action, e));
    }

    /**
     * Like {@link Stream#limit(long)}.
     *
     * @param maxSize Like {@link Stream#limit(long)}.
     * @return Like {@link Stream#limit(long)}.
     */
    default IOStream<T> limit(final long maxSize) {
        return adapt(unwrap().limit(maxSize));
    }

    /**
     * Like {@link Stream#map(Function)}.
     *
     * @param <R> Like {@link Stream#map(Function)}.
     * @param mapper Like {@link Stream#map(Function)}.
     * @return Like {@link Stream#map(Function)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default <R> IOStream<R> map(final IOFunction<? super T, ? extends R> mapper) throws IOException {
        return adapt(unwrap().map(t -> Erase.apply(mapper, t)));
    }

    /**
     * TODO Package-private for now, needs IOToDoubleFunction?
     *
     * Adding this method now and an IO version later is an issue because call sites would have to type-cast to pick one. It
     * would be ideal to have only one.
     *
     * Like {@link Stream#mapToDouble(ToDoubleFunction)}.
     *
     * Package private for now.
     *
     * @param mapper Like {@link Stream#mapToDouble(ToDoubleFunction)}.
     * @return Like {@link Stream#mapToDouble(ToDoubleFunction)}.
     */
    default DoubleStream mapToDouble(final ToDoubleFunction<? super T> mapper) {
        return unwrap().mapToDouble(mapper);
    }

    /**
     * TODO Package-private for now, needs IOToIntFunction?
     *
     * Adding this method now and an IO version later is an issue because call sites would have to type-cast to pick one. It
     * would be ideal to have only one.
     *
     * Like {@link Stream#mapToInt(ToIntFunction)}.
     *
     * Package private for now.
     *
     * @param mapper Like {@link Stream#mapToInt(ToIntFunction)}.
     * @return Like {@link Stream#mapToInt(ToIntFunction)}.
     */
    default IntStream mapToInt(final ToIntFunction<? super T> mapper) {
        return unwrap().mapToInt(mapper);
    }

    /**
     * TODO Package-private for now, needs IOToLongFunction?
     *
     * Adding this method now and an IO version later is an issue because call sites would have to type-cast to pick one. It
     * would be ideal to have only one.
     *
     * Like {@link Stream#mapToLong(ToLongFunction)}.
     *
     * Package private for now.
     *
     * @param mapper Like {@link Stream#mapToLong(ToLongFunction)}.
     * @return Like {@link Stream#mapToLong(ToLongFunction)}.
     */
    default LongStream mapToLong(final ToLongFunction<? super T> mapper) {
        return unwrap().mapToLong(mapper);
    }

    /**
     * Like {@link Stream#max(Comparator)}.
     *
     * @param comparator Like {@link Stream#max(Comparator)}.
     * @return Like {@link Stream#max(Comparator)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default Optional<T> max(final IOComparator<? super T> comparator) throws IOException {
        return unwrap().max((t, u) -> Erase.compare(comparator, t, u));
    }

    /**
     * Like {@link Stream#min(Comparator)}.
     *
     * @param comparator Like {@link Stream#min(Comparator)}.
     * @return Like {@link Stream#min(Comparator)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default Optional<T> min(final IOComparator<? super T> comparator) throws IOException {
        return unwrap().min((t, u) -> Erase.compare(comparator, t, u));
    }

    /**
     * Like {@link Stream#noneMatch(Predicate)}.
     *
     * @param predicate Like {@link Stream#noneMatch(Predicate)}.
     * @return Like {@link Stream#noneMatch(Predicate)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default boolean noneMatch(final IOPredicate<? super T> predicate) throws IOException {
        return unwrap().noneMatch(t -> Erase.test(predicate, t));
    }

    /**
     * Like {@link Stream#peek(Consumer)}.
     *
     * @param action Like {@link Stream#peek(Consumer)}.
     * @return Like {@link Stream#peek(Consumer)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default IOStream<T> peek(final IOConsumer<? super T> action) throws IOException {
        return adapt(unwrap().peek(t -> Erase.accept(action, t)));
    }

    /**
     * Like {@link Stream#reduce(BinaryOperator)}.
     *
     * @param accumulator Like {@link Stream#reduce(BinaryOperator)}.
     * @return Like {@link Stream#reduce(BinaryOperator)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default Optional<T> reduce(final IOBinaryOperator<T> accumulator) throws IOException {
        return unwrap().reduce((t, u) -> Erase.apply(accumulator, t, u));
    }

    /**
     * Like {@link Stream#reduce(Object, BinaryOperator)}.
     *
     * @param identity Like {@link Stream#reduce(Object, BinaryOperator)}.
     * @param accumulator Like {@link Stream#reduce(Object, BinaryOperator)}.
     * @return Like {@link Stream#reduce(Object, BinaryOperator)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default T reduce(final T identity, final IOBinaryOperator<T> accumulator) throws IOException {
        return unwrap().reduce(identity, (t, u) -> Erase.apply(accumulator, t, u));
    }

    /**
     * Like {@link Stream#reduce(Object, BiFunction, BinaryOperator)}.
     *
     * @param <U> Like {@link Stream#reduce(Object, BiFunction, BinaryOperator)}.
     * @param identity Like {@link Stream#reduce(Object, BiFunction, BinaryOperator)}.
     * @param accumulator Like {@link Stream#reduce(Object, BiFunction, BinaryOperator)}.
     * @param combiner Like {@link Stream#reduce(Object, BiFunction, BinaryOperator)}.
     * @return Like {@link Stream#reduce(Object, BiFunction, BinaryOperator)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default <U> U reduce(final U identity, final IOBiFunction<U, ? super T, U> accumulator, final IOBinaryOperator<U> combiner) throws IOException {
        return unwrap().reduce(identity, (t, u) -> Erase.apply(accumulator, t, u), (t, u) -> Erase.apply(combiner, t, u));
    }

    /**
     * Like {@link Stream#skip(long)}.
     *
     * @param n Like {@link Stream#skip(long)}.
     * @return Like {@link Stream#skip(long)}.
     */
    default IOStream<T> skip(final long n) {
        return adapt(unwrap().skip(n));
    }

    /**
     * Like {@link Stream#sorted()}.
     *
     * @return Like {@link Stream#sorted()}.
     */
    default IOStream<T> sorted() {
        return adapt(unwrap().sorted());
    }

    /**
     * Like {@link Stream#sorted(Comparator)}.
     *
     * @param comparator Like {@link Stream#sorted(Comparator)}.
     * @return Like {@link Stream#sorted(Comparator)}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unused") // thrown by Erase.
    default IOStream<T> sorted(final IOComparator<? super T> comparator) throws IOException {
        return adapt(unwrap().sorted((t, u) -> Erase.compare(comparator, t, u)));
    }

    /**
     * Like {@link Stream#toArray()}.
     *
     * @return {@link Stream#toArray()}.
     */
    default Object[] toArray() {
        return unwrap().toArray();
    }

    /**
     * TODO Package-private for now, needs IOIntFunction?
     *
     * Adding this method now and an IO version later is an issue because call sites would have to type-cast to pick one. It
     * would be ideal to have only one.
     *
     * Like {@link Stream#toArray(IntFunction)}.
     *
     * Package private for now.
     *
     * @param <A> Like {@link Stream#toArray(IntFunction)}.
     * @param generator Like {@link Stream#toArray(IntFunction)}.
     * @return Like {@link Stream#toArray(IntFunction)}.
     */
    default <A> A[] toArray(final IntFunction<A[]> generator) {
        return unwrap().toArray(generator);
    }

}
