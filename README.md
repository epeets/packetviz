What is PacketViz?
----

PacketViz is a general packet or interaction graphing tool that can be used in a variety of applications including:

- Cache coherency "protocol flow diagrams"
- Networking packet diagrams
- Dynamic software interaction diagrams

What is the status of PacketViz?
----

PacketViz is relatively stable having been used countless times at Newisys to analyze cache coherency simulations.

Example Screenshots
----

### Cache Coherence

![PacketViz Screenshot](docs/screenshot-cache-coherence.png)

### News Feeder Service

![PacketViz Screenshot](docs/screenshot-news-subscriber.png)

How do I run PacketViz?
----

PacketViz can be run with as an executable jar archive.

    % jar -jar pktviz-dist-x.y.z.jar 

If you'd like to get a quick example to try out, download the source distribution and use an example ".pkt" file from the examples directory.

    % jar -jar pktviz-dist-x.y.z.jar examples/example-abc.pkt

What platforms does PacketViz support?
----

PacketViz is written in the Java 5.0 language 100% Java, and should support any OS that can run a JVM version 5.0 or better.  It has been most recently tested on JRE 1.6.0.
