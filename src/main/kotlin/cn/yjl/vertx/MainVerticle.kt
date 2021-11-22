package cn.yjl.vertx

import io.vertx.core.DeploymentOptions
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await


class MainVerticle : CoroutineVerticle() {

  override suspend fun start() {
    val mysqlVerticle = MysqlVerticle()
    val redisVerticle = RedisVerticle()
    vertx.deployVerticle(mysqlVerticle, this.getDeploymentOptions("mysql")).await();
    vertx.deployVerticle(redisVerticle, this.getDeploymentOptions("redis")).await();
    val apiVerticle = ApiVerticle(mysqlVerticle.getMysqlClient()!!, redisVerticle.getRedisClient()!!)
    vertx.deployVerticle(apiVerticle, this.getDeploymentOptions("api")).await();
  }

  private fun getDeploymentOptions(name: String): DeploymentOptions {
    return DeploymentOptions().setConfig(config.getJsonObject(name, JsonObject()))
      .setWorkerPoolName(name)
  }
}
