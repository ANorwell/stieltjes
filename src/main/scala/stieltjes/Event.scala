package stieltjes

import com.aphyr.riemann.Proto
import scala.collection.JavaConversions._

trait EventPart {
  def value: Any
}

case class Host(value: String) extends EventPart
case class Service(value: String) extends EventPart
case class State(value: String) extends EventPart
case class Tag(value: String) extends EventPart
case class Description(value: String) extends EventPart
case class Ttl(value: Float) extends EventPart

object Metric {
  def apply(value: Long) = new Metric(value)
  def apply(value: Double) = new Metric(value)
  def apply(value: Float) = new Metric(value)
}

private[stieltjes] case class Metric(value: AnyVal) extends EventPart

case class Event(eventParts: EventPart*) {

  def apply(eventPartsToAdd: EventPart*) =
    new Event(eventParts ++ eventPartsToAdd: _*)

  def toBytes = {

    val b = Proto.Event.newBuilder

    eventParts foreach { part ⇒
      part match {
        case Host(h)    ⇒ b.setHost(h)
        case Service(s) ⇒ b.setService(s)
        case State(s)   ⇒ b.setState(s)
        case Tag(t)     ⇒ b.addTags(t)
        case Metric(m) ⇒ m match {
          case l: Long   ⇒ b.setMetricSint64(l)
          case d: Double ⇒ b.setMetricD(d)
          case f: Float  ⇒ b.setMetricF(f)
        }
      }
    }

    b.setTime(time)

    Proto.Msg.newBuilder.addEvents(b.build).build.toByteArray
  }

  val time = System.currentTimeMillis / 1000
}
