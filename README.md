Stieltjes
=========

Stieltjies is a minimal, UDP-only, [Netty](http://netty.io)-based Scala client for [Riemann](http://riemann.io).

    import stieltjes._

    val client = new UdpRiemannClient("127.0.0.1")
    client.write(
      Event(Host("myhost"), Service("server"), State("ok"), Metric(3.0F)))

By default, the client uses client port 5556 and server port 5555.

Events are immutable can be used as templates for other events:

    val defaultEvent = Event(Host("myhost"), Service("server"), State("ok"), Ttl(20))
    ...
    client.write(defaultEvent(State("critical"), Metric(1000L), Description("critical error")))

Caveats
-------

Because Stieltjes uses UDP, event delivery is not guaranteed. This should be used for high-volume stats tracking, rather than error reporting.

Because events are sent as fire-and-forget UDP packets, there is no mechanism for detecting if the Riemann server is down.

About
-----

Author: Arron Norwell
