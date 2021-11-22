package cn.yjl.vertx.handler

import cn.yjl.vertx.util.AppUtil
import cn.yjl.vertx.util.BizException
import cn.yjl.vertx.util.ErrorEnum
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.redis.client.RedisAPI
import io.vertx.redis.client.Response
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.SqlConnection
import io.vertx.sqlclient.Tuple

class GetAreaHandler(config: JsonObject, vertx: Vertx, mysqlClient: MySQLPool, redisClient: RedisAPI) :
  AbstractHandler(config, vertx, mysqlClient, redisClient) {

  override suspend fun getData(context: RoutingContext): JsonObject {
    val body = getJsonBody(context.body)
    logger.info(body.toString())
    // 检查入参
    checkItem(body, "code")
    val cacheKey = "Area" + "-" + body.getString("code")
    val response: Response? = redisClient.get(cacheKey).await()
    if (response == null) {
      val sqlConnection: SqlConnection = mysqlClient.connection.await()
      val rows: RowSet<Row> = sqlConnection.preparedQuery("""
          select id, code, name from t_area
          where code = ? limit 1""".trimIndent())
        .execute(Tuple.of(body.getString("code"))).await()
      if (rows.size() > 0) {
        val data = rows.iterator().next().toJson()
        redisClient.set(listOf(cacheKey, data.toString()))
        return this.convertData(data)
      } else {
        throw BizException(ErrorEnum.NoData)
      }
    } else {
      return this.convertData(response.toBuffer().toJsonObject())
    }
  }

  private fun convertData(data: JsonObject): JsonObject {
    return jsonObjectOf("areaId" to data.getInteger("id"),
      "areaCode" to data.getString("code"), "areaName" to data.getString("name"))
  }
}
