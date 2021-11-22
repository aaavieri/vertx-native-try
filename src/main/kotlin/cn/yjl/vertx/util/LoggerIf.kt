package cn.yjl.vertx.util

import java.util.logging.Logger

interface LoggerIf {
    val logger: Logger
        get() = Logger.getLogger(this.javaClass.name)
}
