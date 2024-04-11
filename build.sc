import mill._
import mill.scalalib._

trait AlvariumModule extends ScalaModule {
  def scalaVersion = "3.4.0"
  override def unmanagedClasspath = T {
    Agg.from(
      os.list(millSourcePath / os.up / "lib").map(PathRef(_))
    )
  }
  override def ivyDeps = Agg(
    ivy"com.lihaoyi::os-lib:0.9.3",
    ivy"org.apache.logging.log4j:log4j-core:2.21.0",
    ivy"com.google.code.findbugs:jsr305:2.0.2"
  )
}

object publisher extends AlvariumModule
object transit extends AlvariumModule
object `mosquitto-client` extends ScalaModule {
  def scalaVersion = "3.4.0"
  override def ivyDeps = Agg(
    ivy"net.sigusr::fs2-mqtt:1.0.1",
    ivy"org.typelevel::cats-effect:3.6-0142603",
  )
}