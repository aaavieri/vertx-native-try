package cn.yjl.vertx.util

import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import io.vertx.core.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

object AppUtil {
    private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault())

    fun <T> autoCast(o: Any): T {
        return o as T
    }

    val nowInstant: Instant
        get() = LocalDateTime.now().atZone(ZoneOffset.systemDefault()).toInstant()
    val nowString: String
        get() = LocalDateTime.now().toString()

    fun formatInstant(instant: Instant?): String {
        return DATE_TIME_FORMATTER.format(instant)
    }

    fun formatDbDate(dbDate: String?): String? {
        return if (dbDate == null || dbDate.trim { it <= ' ' }.length == 0) {
            null
        } else {
            val localDateTime = LocalDateTime.parse(dbDate)
            localDateTime.format(DATE_TIME_FORMATTER)
        }
    }

    fun <T> delay(vertx: Vertx, c: Consumer<T>): Consumer<T> {
        return Consumer { t: T -> vertx.setTimer(50L) { l: Long? -> c.accept(t) } }
    }

    fun <T> block2Future(vertx: Vertx, supplier: Supplier<T>): Future<T> {
        return block2Promise(vertx, supplier).future()
    }

    fun <T> block2Promise(vertx: Vertx, supplier: Supplier<T>): Promise<T> {
        val returnPromise = Promise.promise<T>()
        vertx.executeBlocking({ promise: Promise<T> -> promise.complete(supplier.get()) }, returnPromise)
        return returnPromise
    }

    fun <T, R> convert(promise: Promise<T>, converter: Function<T, R>): Promise<R> {
        return consumer2Promise { p: Promise<R> -> promise.future().onSuccess { t: T -> p.complete(converter.apply(t)) }.onFailure { cause: Throwable? -> p.fail(cause) } }
    }

    fun <T, R> convert(future: Future<T>, converter: Function<T, R>): Future<R> {
        return consumer2Promise { p: Promise<R> -> future.onSuccess { t: T -> p.complete(converter.apply(t)) }.onFailure { cause: Throwable? -> p.fail(cause) } }.future()
    }

    fun <T> successPromise(): Promise<T> {
        return future2Promise(Future.succeededFuture())
    }

    fun <T> successPromise(t: T): Promise<T> {
        return future2Promise(Future.succeededFuture(t))
    }

    fun <T> future2Promise(future: Future<T>): Promise<T> {
        return consumer2Promise { handler: Promise<T>? -> future.onComplete(handler) }
    }

    fun <T, R> future2Promise(future: Future<T>, converter: Function<T, R>): Promise<R> {
        return future2Promise(future.compose { t: T -> Future.succeededFuture(converter.apply(t)) })
    }

    fun <T> consumer2Promise(consumer: Consumer<Promise<T>>): Promise<T> {
        val promise = Promise.promise<T>()
        consumer.accept(promise)
        return promise
    }

    fun <T> consumer2Future(consumer: Consumer<Future<T>?>): Future<T> {
        val future = Promise.promise<T>().future()
        consumer.accept(future)
        return future
    }

    fun <T, E> compose(func: Function<T, E>, consumer: Consumer<E>): Consumer<T> {
        return Consumer { t: T -> consumer.accept(func.apply(t)) }
    }

    @JvmStatic
    fun <T, R> convert(result: AsyncResult<T>, converter: Function<T, R>): AsyncResult<R> {
        return object : AsyncResult<R> {
            override fun result(): R? {
                return if (result.succeeded()) converter.apply(result.result()) else null
            }

            override fun cause(): Throwable {
                return result.cause()
            }

            override fun succeeded(): Boolean {
                return result.succeeded()
            }

            override fun failed(): Boolean {
                return result.failed()
            }
        }
    }

    fun <T> getPromise(handler: Handler<T>?, exHandler: Handler<Throwable?>?): Promise<T> {
        val promise = Promise.promise<T>()
        promise.future().onSuccess(handler).onFailure(exHandler)
        return promise
    }
}
