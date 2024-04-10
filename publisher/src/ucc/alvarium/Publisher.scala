package ucc.alvarium

import com.alvarium.annotators.AnnotatorFactory
import com.alvarium.contracts.AnnotationType
import com.alvarium.utils.{ImmutablePropertyBag, PropertyBag}
import com.alvarium.{DefaultSdk, SdkInfo}
import org.apache.logging.log4j.LogManager

import java.net.{InetAddress, InetSocketAddress, SocketAddress}
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util
import scala.util.control.NonFatal

val log = LogManager.getRootLogger

@main
def main(next: String): Unit = {

  val config = os.read(os.pwd / "config.json")
  val sdkInfo = SdkInfo.fromJson(config)
  val annotators = sdkInfo.getAnnotators.map(new AnnotatorFactory().getAnnotator(_, sdkInfo, log))
  val sdk = new DefaultSdk(annotators, sdkInfo, log)

  val dataSocket = SocketChannel.open(InetSocketAddress.createUnresolved(next, 1242))

  while (true) try {
    Thread.sleep(1000)
    val data = generateData
    sdk.create(data)
    dataSocket.write(ByteBuffer.wrap(data))
  } catch
    case NonFatal(e) => e.printStackTrace()

}

def generateData = "Hello Cork".getBytes()

object PropertyBag {
  def apply(properties: (AnnotationType, AnyRef)*): PropertyBag = {
    val map = new util.HashMap[String, AnyRef]()
    for ((k, v) <- properties)
      map.put(k.toString, v)

    new ImmutablePropertyBag(map)
  }
}