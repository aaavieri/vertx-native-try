package cn.yjl.vertx

import cn.yjl.vertx.handler.AbstractHandler
import cn.yjl.vertx.handler.DefaultFailureHandler
import cn.yjl.vertx.handler.GetAreaHandler
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.redis.client.RedisAPI

class ApiVerticle(private val mysqlClient: MySQLPool, private val redisClient: RedisAPI) : CoroutineVerticle() {

  override suspend fun start() {
    val server = vertx.createHttpServer()
    val router = Router.router(vertx)
    val failureHandler = DefaultFailureHandler(config, vertx, mysqlClient, redisClient)
    this.postHandler(router, "/api/getArea", GetAreaHandler(config, vertx, mysqlClient, redisClient),
      failureHandler)
    server.requestHandler(router).listen(config.getInteger("port", 8080)).await();
  }

  private fun postHandler(router: Router, path: String, handler: AbstractHandler, failureHandler: DefaultFailureHandler) {
    router.post(path).produces("application/json;charset=UTF-8")
      .handler(BodyHandler.create())
      .handler(handler)
      .failureHandler(failureHandler)
  }
}
