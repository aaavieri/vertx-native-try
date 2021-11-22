package cn.yjl.vertx.util

import java.lang.RuntimeException
import java.util.Objects
import java.util.function.Consumer

/**
 * 断言共通类
 */
object AssertUtil {
    @JvmField
    val DEFAULT = Checker { ex: RuntimeException? -> throw ex!! }
    fun getChecker(errorConsumer: Consumer<RuntimeException>): Checker {
        return Checker(errorConsumer)
    }

    class Checker(private val errorConsumer: Consumer<RuntimeException>) {

        /**
         * 断言是否为Null，不为Null则抛出异常
         * @param object 待判断的对象
         * @param ex 待抛出异常
         */
        fun isNull(`object`: Any?, ex: RuntimeException?) {
            meet(Objects.isNull(`object`), ex)
        }

        /**
         * 断言是否不为Null，为Null则抛出异常
         * @param object 待判断的对象
         * @param ex 待抛出异常
         */
        fun notNull(`object`: Any?, ex: RuntimeException?) {
            meet(Objects.nonNull(`object`), ex)
        }

        /**
         * 断言两个对象是否相等，不相等就抛出异常
         * @param data1 对象1
         * @param data2 对象2
         * @param ex 待抛出异常
         * @param <T> 泛型
        </T> */
        fun <T> eqaul(data1: T, data2: T, ex: RuntimeException?) {
            meet(data1 == data2, ex)
        }

        /**
         * 断言两个对象是否不相等，相等就抛出异常
         * @param data1 对象1
         * @param data2 对象2
         * @param ex 待抛出异常
         * @param <T> 泛型
        </T> */
        fun <T> notEqaul(data1: T, data2: T, ex: RuntimeException?) {
            meet(data1 != data2, ex)
        }

        /**
         * 断言是否不满足条件，如果条件为真则抛出异常
         * @param condition 条件
         * @param ex 待抛出异常
         */
        fun notMeet(condition: Boolean, ex: RuntimeException?) {
            meet(!condition, ex)
        }

        /**
         * 断言是否满足条件，如果条件为假则抛出异常
         * @param condition 条件
         * @param ex 待抛出异常
         */
        fun meet(condition: Boolean, ex: RuntimeException?) {
            if (!condition && ex != null) {
              errorConsumer.accept(ex)
            }
        }
    }
}
