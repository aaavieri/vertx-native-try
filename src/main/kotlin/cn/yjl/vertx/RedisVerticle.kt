package cn.yjl.vertx

import cn.yjl.vertx.util.Const
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.redis.client.Redis
import io.vertx.redis.client.RedisAPI
import io.vertx.redis.client.RedisOptions

class RedisVerticle : CoroutineVerticle() {

  private var redisClient: RedisAPI? = null

  override suspend fun start() {
    val redisType = config.getInteger("redisType", Const.REDIS_TYPE_SINGLE)
    var redis: Redis? = null
    if (redisType == Const.REDIS_TYPE_SINGLE) {
      redis = Redis.createClient(vertx, config.getString("redisUrl"))
    } else if (redisType == Const.REDIS_TYPE_CLUSTER) {
      val options = RedisOptions().setPassword(config.getString("password"))
      config.getJsonArray("redisUrl").forEach { url: Any -> options.addConnectionString(url.toString()) }
      redis = Redis.createClient(vertx, options)
    }
    this.redisClient = RedisAPI.api(redis)
  }

  override suspend fun stop() {
    this.redisClient?.close()
  }

  fun getRedisClient() = redisClient
}
