import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.{Host, Port}
import fs2.Stream
import net.sigusr.mqtt.api.RetryConfig.Custom
import net.sigusr.mqtt.api.{Message, Session, SessionConfig, TransportConfig}
import retry.RetryPolicies

import scala.concurrent.duration.{FiniteDuration, SECONDS}

object Mosquitto extends IOApp {


  override def run(args: List[String]): IO[ExitCode] = {
    val retryConfig = Custom[IO](
      RetryPolicies
        .limitRetries[IO](5)
        .join(RetryPolicies.fullJitter(FiniteDuration(2, SECONDS)))
    )
    val transportConfig =
      TransportConfig[IO](
        Host.fromString("mosquitto-server").get,
        Port.fromInt(1883).get,
        retryConfig = retryConfig
      )
    val sessionConfig =
      SessionConfig(
        "mqtt-client",
        cleanSession = false,
        keepAlive = 5
      )


    Session[IO](transportConfig, sessionConfig).use { session =>
      session
        .messages
        .flatMap {
          case Message(topic, payload) =>
            Stream.eval(
              IO(println(
                s"""
                  |annotation received : ${payload}
                  |""".stripMargin))
            )
        }
        .compile
        .drain
        .map(_ => ExitCode.Success)
    }
  }

}