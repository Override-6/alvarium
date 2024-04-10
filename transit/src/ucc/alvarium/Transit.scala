package ucc.alvarium

import com.alvarium.annotators.AnnotatorFactory
import com.alvarium.{DefaultSdk, SdkInfo}
import org.apache.logging.log4j.{Level, LogManager}
import org.apache.logging.log4j.core.config.Configurator

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.{ServerSocketChannel, SocketChannel}
import scala.util.CommandLineParser

val log = {
  Configurator.setRootLevel(Level.DEBUG)
  LogManager.getRootLogger
}

@main def main(args: String*): Unit = {
  val next = args.headOption
  val config = os.read(os.pwd / "config.json")
  val sdkInfo = SdkInfo.fromJson(config)
  val annotators = sdkInfo.getAnnotators.map(new AnnotatorFactory().getAnnotator(_, sdkInfo, log))
  val sdk = new DefaultSdk(annotators, sdkInfo, log)

  val inputSocket = ServerSocketChannel.open().bind(new InetSocketAddress("0.0.0.0", 1242))
  val buffer = ByteBuffer.allocate(1024)

  while (inputSocket.isOpen) {
    buffer.position(0)
    buffer.limit(buffer.capacity())

    log.info("Accepting next connection...")
    val client = inputSocket.accept()
    val messageLen = client.read(buffer)
    val bytes = new Array[Byte](messageLen)
    buffer.get(0, bytes, 0, messageLen)
    val message = new String(bytes)
    log.info(s"Transit message : $message")

    sdk.transit(bytes)

    buffer.position(0)
    buffer.put(bytes)
    buffer.flip()

    val nextSocket = next.map(str => SocketChannel.open(new InetSocketAddress(str, 1242)))
    nextSocket match
      case Some(socket) => socket.write(buffer)
      case None => log.info(s"Transit ends : $message")
      
    client.close()
  }
}