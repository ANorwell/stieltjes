package stieltjes

import org.scalatest.FunSpec
import com.aphyr.riemann.Proto

trait EventTestHelpers {
  def host(e: Event) = e.eventParts.filter { _.isInstanceOf[Host] }.last.value

  def service(e: Event) = e.eventParts.filter { _.isInstanceOf[Service] }.last.value

  def state(e: Event) = e.eventParts.filter { _.isInstanceOf[State] }.last.value

  def tags(e: Event) = e.eventParts filter { _.isInstanceOf[Tag] }

  def metric(e: Event) = e.eventParts.filter { _.isInstanceOf[Metric] }.last.value
}

class EventSpec extends FunSpec with EventTestHelpers {

  describe("Events") {

    it("should serve as templates for other events") {
      val template = Event(Host("hostname"), State("ok"))
      val event = template(Service("test"))
      assert(service(event) === "test")
      assert(host(event) === "hostname")
      assert(state(event) === "ok")
    }

    it("should allow event fields to be over-writeable") {
      val template = Event(Host("hostname"), State("ok"))
      val event = template(Host("test"))
      assert(host(event) === "test")
    }

    describe("serialization") {

      def parseEvent(bytes: Array[Byte]) = {
        val msg = Proto.Msg.parseFrom(bytes)
        assert(msg.getEventsCount === 1)
        msg.getEvents(0)
      }

      it("should serialize metrics with longs to the Sint64 field") {
        val event = Event(Metric(1L))
        val msg = parseEvent(event.toBytes)
        assert(msg.hasMetricSint64)
        assert(msg.getMetricSint64 === 1L)
      }

      it("should serialize metrics with doubles to the double") {
        val event = Event(Metric(1.0D))
        val msg = parseEvent(event.toBytes)
        assert(msg.hasMetricD)
        assert(msg.getMetricD === 1.0D)
      }

      it("should serialize metrics with floats to the float") {
        val event = Event(Metric(1.0F))
        val msg = parseEvent(event.toBytes)
        assert(msg.hasMetricF)
        assert(msg.getMetricF === 1.0F)
      }
    }
  }
}
