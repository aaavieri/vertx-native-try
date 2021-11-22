package cn.yjl.vertx

import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.mysqlclient.MySQLConnectOptions
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.PoolOptions
import java.lang.RuntimeException

class MysqlVerticle : CoroutineVerticle() {

  private var client: MySQLPool? = null

  override suspend fun start() {
    println(config.toString())
    val connectOptions = MySQLConnectOptions()
      .setPort(config.getInteger("port", 3306))
      .setHost(config.getString("host", "127.0.0.1"))
      .setDatabase(config.getString("db"))
      .setUser(config.getString("user"))
      .setCharacterEncoding(config.getString("charset", "utf8"))
      .setCollation(config.getString("collation", "utf8_general_ci"))
      .setPassword(config.getString("password"))

    // 连接池
    val poolOptions = PoolOptions()
      .setMaxSize(config.getInteger("poolSize", 5))

    // 创建mysql连接池
    client = MySQLPool.pool(vertx, connectOptions, poolOptions)
  }

  override suspend fun stop() {
    client?.close()
  }

  fun getMysqlClient() = client
}
