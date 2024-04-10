package ucc.alvarium

import com.alvarium.annotators.AnnotatorFactory
import com.alvarium.{DefaultSdk, SdkInfo}
import org.apache.logging.log4j.LogManager

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.{ServerSocketChannel, SocketChannel}

val log = LogManager.getRootLogger

@main def main(next: Option[String]): Unit = {
  val config = os.read(os.pwd / "config.json")
  val sdkInfo = SdkInfo.fromJson(config)
  val annotators = sdkInfo.getAnnotators.map(new AnnotatorFactory().getAnnotator(_, sdkInfo, log))
  val sdk = new DefaultSdk(annotators, sdkInfo, log)

  val inputSocket = ServerSocketChannel.open()
  val nextSocket = next.map(str => SocketChannel.open(new InetSocketAddress(str, 1242)))
  val buffer = ByteBuffer.allocate(1024)

  while (inputSocket.isOpen) {
    val client = inputSocket.accept()
    val messageLen = client.read(buffer)
    val bytes = new Array[Byte](messageLen)
    buffer.get(0, bytes, 0, messageLen)
    val message = new String(bytes)
    log.info(s"Transit message : $message")

    sdk.transit(bytes)

    nextSocket.tapEach(_.write(buffer)).
    buffer.reset()
    buffer.flip()
  }
}