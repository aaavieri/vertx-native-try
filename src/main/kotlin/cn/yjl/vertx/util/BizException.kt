package cn.yjl.vertx.util

import java.lang.RuntimeException
import cn.yjl.vertx.util.ErrorEnum
import io.vertx.core.json.JsonObject

/**
 * 业务异常类
 */
class BizException(private val code: Int, private val msg: String) : RuntimeException() {
    constructor(errorEnum: ErrorEnum) : this(errorEnum.code, errorEnum.msg) {}

    /**
     * 转换成JSON
     * @return 转换结果
     */
    fun toJson(): JsonObject {
        return JsonObject().put("code", this.code).put("msg", msg)
    }
}
