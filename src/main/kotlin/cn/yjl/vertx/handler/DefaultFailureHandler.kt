package cn.yjl.vertx.handler

import cn.yjl.vertx.util.AppUtil.autoCast
import cn.yjl.vertx.util.BizException
import cn.yjl.vertx.util.ErrorEnum
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.mysqlclient.MySQLPool
import io.vertx.redis.client.RedisAPI
import kotlinx.coroutines.DelicateCoroutinesApi

class DefaultFailureHandler(config: JsonObject, vertx: Vertx, mysqlClient: MySQLPool, redisClient: RedisAPI) :
  AbstractHandler(config, vertx, mysqlClient, redisClient) {

    @OptIn(DelicateCoroutinesApi::class)
    override fun handle(context: RoutingContext) {
      when (val t = context.failure()) {
        is BizException -> context.response().end(t.toJson().toBuffer())
        else -> context.response().end(ErrorEnum.InnerException.json.toBuffer())
      }
    }

  override suspend fun getData(context: RoutingContext): JsonObject {
    TODO("Not yet implemented")
  }
}
