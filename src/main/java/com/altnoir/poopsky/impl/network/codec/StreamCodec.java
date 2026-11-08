package com.altnoir.poopsky.impl.network.codec;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/** 保留原模组的消息字段顺序，底层交由 Forge 1.20.1 的缓冲区传输。 */
public interface StreamCodec<B, V> {
    V decode(B buffer);
    void encode(B buffer, V value);

    static <B, V> StreamCodec<B, V> of(BiConsumer<B, V> writer, Function<B, V> reader) {
        return new StreamCodec<>() {
            public V decode(B buffer) { return reader.apply(buffer); }
            public void encode(B buffer, V value) { writer.accept(buffer, value); }
        };
    }

    static <B, V> StreamCodec<B, V> unit(V value) {
        return of((buffer, ignored) -> {}, buffer -> value);
    }

    default <T> StreamCodec<B, T> map(Function<V, T> reader, Function<T, V> writer) {
        return of((buffer, value) -> encode(buffer, writer.apply(value)), buffer -> reader.apply(decode(buffer)));
    }

    default <T> StreamCodec<B, T> apply(Function<StreamCodec<B, V>, StreamCodec<B, T>> operation) {
        return operation.apply(this);
    }

    static <B, A, V> StreamCodec<B, V> composite(StreamCodec<? super B, A> a, Function<V, A> ga, Function<A, V> factory) {
        return of((buf, v) -> a.encode(buf, ga.apply(v)), buf -> factory.apply(a.decode(buf)));
    }

    static <B, A, C, V> StreamCodec<B, V> composite(StreamCodec<? super B, A> a, Function<V, A> ga,
            StreamCodec<? super B, C> c, Function<V, C> gc, BiFunction<A, C, V> factory) {
        return of((buf, v) -> { a.encode(buf, ga.apply(v)); c.encode(buf, gc.apply(v)); },
                buf -> factory.apply(a.decode(buf), c.decode(buf)));
    }

    static <B, A, C, D, V> StreamCodec<B, V> composite(StreamCodec<? super B, A> a, Function<V, A> ga,
            StreamCodec<? super B, C> c, Function<V, C> gc, StreamCodec<? super B, D> d, Function<V, D> gd,
            Function3<A, C, D, V> factory) {
        return of((buf, v) -> { a.encode(buf, ga.apply(v)); c.encode(buf, gc.apply(v)); d.encode(buf, gd.apply(v)); },
                buf -> factory.apply(a.decode(buf), c.decode(buf), d.decode(buf)));
    }

    static <B, A, C, D, E, V> StreamCodec<B, V> composite(StreamCodec<? super B, A> a, Function<V, A> ga,
            StreamCodec<? super B, C> c, Function<V, C> gc, StreamCodec<? super B, D> d, Function<V, D> gd,
            StreamCodec<? super B, E> e, Function<V, E> ge, Function4<A, C, D, E, V> factory) {
        return of((buf, v) -> { a.encode(buf, ga.apply(v)); c.encode(buf, gc.apply(v)); d.encode(buf, gd.apply(v)); e.encode(buf, ge.apply(v)); },
                buf -> factory.apply(a.decode(buf), c.decode(buf), d.decode(buf), e.decode(buf)));
    }

    interface Function3<A, B, C, R> { R apply(A a, B b, C c); }
    interface Function4<A, B, C, D, R> { R apply(A a, B b, C c, D d); }
}
