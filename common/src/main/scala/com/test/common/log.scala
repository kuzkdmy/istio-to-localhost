package com.test.common

import com.test.common.types.Ctx
import org.slf4j.{Logger, MDC}
import zio.{Trace, UIO, ZIO}

// still don't like zio standard logs and zio logging library
object log {
  private def ctxLog(logFn: => Unit)(implicit ctx: Ctx, trace: Trace): Unit = {
    import scala.jdk.CollectionConverters._
    try {
      MDC.setContextMap(
        (List(
          Some("tenant-id"    -> ctx.tenantId.value.toString),
          Some("user-id"      -> ctx.userId.value.toString),
          Some("x-request-id" -> ctx.requestId.value),
          ctx.payloadCtx.map(c => "payload-ctx" -> c)
        ) ++ (trace match {
          case Trace(location, file, line) =>
            List(
              Some("trace-location" -> location),
              Some("trace-file"     -> file),
              Some("trace-line"     -> line.toString)
            )
          case _ => List.empty
        })).flatten.toMap.asJava
      )
      logFn
    } finally {
      MDC.clear()
    }
  }

  def error(msg: => String)(implicit ctx: Ctx, l: Logger, trace: Trace): UIO[Unit]               = ZIO.succeed(ctxLog(l.error(msg)))
  def warn(msg: => String)(implicit ctx: Ctx, l: Logger, trace: Trace): UIO[Unit]                = ZIO.succeed(ctxLog(l.warn(msg)))
  def info(msg: => String)(implicit ctx: Ctx, l: Logger, trace: Trace): UIO[Unit]                = ZIO.succeed(ctxLog(l.info(msg)))
  def debug(msg: => String)(implicit ctx: Ctx, l: Logger, trace: Trace): UIO[Unit]               = ZIO.succeed(ctxLog(l.debug(msg)))
  def trace(msg: => String)(implicit ctx: Ctx, l: Logger, trace: Trace): UIO[Unit]               = ZIO.succeed(ctxLog(l.trace(msg)))
  def error(msg: => String, t: Throwable)(implicit ctx: Ctx, l: Logger, trace: Trace): UIO[Unit] = ZIO.succeed(ctxLog(l.error(msg, t)))
  def warn(msg: => String, t: Throwable)(implicit ctx: Ctx, l: Logger, trace: Trace): UIO[Unit]  = ZIO.succeed(ctxLog(l.warn(msg, t)))
  def info(msg: => String, t: Throwable)(implicit ctx: Ctx, l: Logger, trace: Trace): UIO[Unit]  = ZIO.succeed(ctxLog(l.info(msg, t)))
  def debug(msg: => String, t: Throwable)(implicit ctx: Ctx, l: Logger, trace: Trace): UIO[Unit] = ZIO.succeed(ctxLog(l.debug(msg, t)))
  def trace(msg: => String, t: Throwable)(implicit ctx: Ctx, l: Logger, trace: Trace): UIO[Unit] = ZIO.succeed(ctxLog(l.trace(msg, t)))

}
