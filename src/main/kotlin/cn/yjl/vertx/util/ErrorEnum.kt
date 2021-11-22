package cn.yjl.vertx.util

import io.vertx.core.json.JsonObject

/**
 * 异常定义枚举
 */
enum class ErrorEnum(val code: Int, val msg: String) {
  Success(0, "ok"),
  NoData(1, "没有数据"),
  Valid(10, "校验不通过"),
  InnerException(99, "程序异常");

  val json: JsonObject
    get() = JsonObject().put("code", this.code).put("msg", msg)
}
