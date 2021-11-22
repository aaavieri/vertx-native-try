package cn.yjl.vertx.handler

import cn.yjl.vertx.util.AssertUtil
import cn.yjl.vertx.util.BizException
import cn.yjl.vertx.util.ErrorEnum
import cn.yjl.vertx.util.LoggerIf
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.mysqlclient.MySQLPool
import io.vertx.redis.client.RedisAPI
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class AbstractHandler(protected val config: JsonObject,
                               protected val vertx: Vertx,
                               protected val mysqlClient: MySQLPool,
                               protected val redisClient: RedisAPI) : Handler<RoutingContext>, LoggerIf {

  @DelicateCoroutinesApi
  override fun handle(context: RoutingContext) {

    GlobalScope.launch {
      try {
        val data = getData(context)
        val result = ErrorEnum.Success.json.put("data", data)
        context.end(result.toBuffer())
      } catch (t: Throwable) {
        context.fail(t)
      }
    }
  }

  protected abstract suspend fun getData(context: RoutingContext): JsonObject;

  protected fun getJsonBody(buffer: Buffer?): JsonObject {
    return if (buffer == null || buffer.length() == 0) {
      JsonObject()
    } else {
      buffer.toJsonObject()
    }
  }

  /**
   * 检查字段是否存在
   * @param data 请求的JSON数据
   * @param key 检查的KEY
   */
  protected fun checkItem(data: JsonObject, key: String) {
    AssertUtil.DEFAULT.meet(data.containsKey(key), BizException(ErrorEnum.Valid))
  }
}
