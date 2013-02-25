package stieltjes

import java.net.InetSocketAddress
import java.util.concurrent.Executors

import org.jboss.netty.bootstrap.ConnectionlessBootstrap
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.channel._
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder

class UdpRiemannClient(hostIp: String, clientPort: Int = 5556, hostPort: Int = 5555) {

  val threadPool = Executors.newCachedThreadPool

  val bootstrap = new ConnectionlessBootstrap(
    new NioDatagramChannelFactory(threadPool))

  bootstrap.setPipeline(Channels.pipeline(EventEncoder, ExceptionHandler))

  val channel = bootstrap.bind(new InetSocketAddress(clientPort))

  val destination = new InetSocketAddress(hostIp, hostPort)

  def write(e: Event) =
    channel.write(e, destination)

  def shutdown = {
    channel.close()
    bootstrap.releaseExternalResources()
  }
}

object EventEncoder extends OneToOneEncoder {

  override protected def encode(ctx: ChannelHandlerContext, channel: Channel, msg: Object): Object = msg match {
    case e: Event ⇒
      ChannelBuffers.wrappedBuffer(e.toBytes)

    case x ⇒ x
  }
}

object ExceptionHandler extends SimpleChannelUpstreamHandler {

  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent) =
    e.getChannel.close()
}
