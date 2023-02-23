package licslan.nio.c4networkprogramming.study.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WriteClinet {


  public static void main(String[] args) throws IOException {
    SocketChannel sc = SocketChannel.open();
    sc.connect(new InetSocketAddress("localhost", 9999));

    //接收服务端写回的数据
    int count = 0;
    while (true) {
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      count += sc.read(buffer);
      System.out.println(count);
      buffer.clear();
    }
  }


}

