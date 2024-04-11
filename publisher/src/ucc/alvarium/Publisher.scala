package ucc.alvarium

import com.alvarium.annotators.AnnotatorFactory
import com.alvarium.contracts.AnnotationType
import com.alvarium.utils.{ImmutablePropertyBag, PropertyBag}
import com.alvarium.{DefaultSdk, SdkInfo}
import org.apache.logging.log4j.{Level, LogManager}
import org.apache.logging.log4j.core.config.Configurator
import sun.jvm.hotspot.HelloWorld.e

import java.net.{InetAddress, InetSocketAddress, SocketAddress}
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util
import scala.util.control.NonFatal

val log = {
  Configurator.setRootLevel(Level.DEBUG)
  LogManager.getRootLogger
}

@main
def main(next: String): Unit = {

  val config = os.read(os.pwd / "config.json")
  val sdkInfo = SdkInfo.fromJson(config)
  val annotators = sdkInfo.getAnnotators.map(new AnnotatorFactory().getAnnotator(_, sdkInfo, log))
  val sdk = new DefaultSdk(annotators, sdkInfo, log)


  while (true) try {
    Thread.sleep(3000)
    val dataSocket = SocketChannel.open(new InetSocketAddress(next, 1242))

    val data = generateData
    log.info(s"Publishing : ${new String(data)}")
    sdk.create(data)
    dataSocket.write(ByteBuffer.wrap(data))

    dataSocket.close()
  } catch
    case NonFatal(e) => e.printStackTrace()

}

def generateData = "Hello Cork and UCC !".getBytes()

object PropertyBag {
  def apply(properties: (AnnotationType, AnyRef)*): PropertyBag = {
    val map = new util.HashMap[String, AnyRef]()
    for ((k, v) <- properties)
      map.put(k.toString, v)

    new ImmutablePropertyBag(map)
  }
}