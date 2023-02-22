package licslan.nio.c4networkprogramming.study.nonblock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import licslan.nio.c2.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {


  //使用nio理解阻塞模式 单线程

  public static void main(String[] args) throws IOException {

    //0.bytebuffer
    ByteBuffer buffer = ByteBuffer.allocate(16);

    //1.创建服务器
    ServerSocketChannel ssc = ServerSocketChannel.open();
    //设置非阻塞 默认阻塞的   ssc.accept();非阻塞  线程还会继续运行  如果没有建立连接  sc=null
    ssc.configureBlocking(false);

    //2.绑定监听端口
    ssc.bind(new InetSocketAddress(9999));

    //3.连接集合
    List<SocketChannel> channelList = new ArrayList<>();

    while (true) {
      //4.accept 建立与客户端连接  SocketChannel用来与客户端通信
      //log.debug("====connecting....");
      //阻塞 线程停止运行  没有客户端连接时就会阻塞
      SocketChannel accept = ssc.accept();
      if (accept != null) {
        //log.debug("====connected....{}", accept);
        //客户端连接也可以设置为非阻塞   channel.read(byteBuffer); 也会变成非阻塞的了 如果没有读到数据 返回0
        ssc.configureBlocking(false);
        channelList.add(accept);
      }

      for (SocketChannel channel : channelList) {
        //5.接收客户端发送的数据
        //log.debug("====read before....{}", channel);
        //阻塞 线程停止运行 等待客户端发了数据 没有读到数据时就会阻塞住
        int read = channel.read(buffer);
        if (read > 0) {
          //切换到可读模式
          buffer.flip();
          ByteBufferUtil.debugRead(buffer);
          //切换到可写模式
          buffer.clear();
          log.debug("====after before....{}", channel);
        }
      }
    }
  }



}

