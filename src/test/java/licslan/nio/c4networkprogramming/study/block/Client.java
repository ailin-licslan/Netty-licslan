package licslan.nio.c4networkprogramming.study.block;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
  public static void main(String[] args) throws IOException {
    SocketChannel sc2 = SocketChannel.open();
    sc2.connect(new InetSocketAddress("localhost", 8080));
    sc2.write(Charset.defaultCharset().encode("xxx-hi"));
    System.out.println("wait....");
  }
}
