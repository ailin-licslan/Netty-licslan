package licslan.nio.c4networkprogramming.study.block;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import licslan.nio.c2.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * https://gitee.com/fmw246/black-horse-netty/blob/master/netty-1-nio/src/test/java/com/asm/netty/c4_nio/a_block/Client.java
 * */
@Slf4j
public class Server {


  public static void main(String[] args) throws IOException {

    //0.bytebuffer
    ByteBuffer buffer = ByteBuffer.allocate(16);

    //1.创建服务器
    ServerSocketChannel ssc = ServerSocketChannel.open();

    //2.绑定监听端口
    ssc.bind(new InetSocketAddress(8080));

    //3.连接集合
    List<SocketChannel> channelList = new ArrayList<>();

    while (true) {
      //4.accept 建立与客户端连接  SocketChannel用来与客户端通信
      log.debug("====connecting....");
      //阻塞 线程停止运行  没有客户端连接时就会阻塞
      SocketChannel accept = ssc.accept();
      log.debug("====connected....{}", accept);
      channelList.add(accept);
      for (SocketChannel channel : channelList) {
        //5.接收客户端发送的数据
        log.debug("====read before....{}", channel);
        //阻塞 线程停止运行 等待客户端发了数据 没有读到数据时就会阻塞住
        channel.read(buffer);
        //切换到可读模式
        buffer.flip();
        ByteBufferUtil.debugRead(buffer);
        //切换到可写模式
        buffer.clear();
        log.debug("====after before....{}", channel);
      }
    }
  }



//  public static void main(String[] args) throws IOException {
//
//    // 0. 创建全局的 ByteBuffer
//    ByteBuffer buffer = ByteBuffer.allocate(1024);
//
//    // 1. 创建一个 服务器 对象
//    ServerSocketChannel ssc = ServerSocketChannel.open();
//
//    // 2. 绑定一个 监听端口
//    ssc.bind(new InetSocketAddress(8080));
//
//    // 3. 连接 集合
//    ArrayList<SocketChannel> channels = new ArrayList<>();
//
//    while (true)  // accept 能 多次调用 使用 wile循环
//    {
//      log.debug("connecting    "
//          +
//          "#########################################################################################################################################################################################");
//
//      // 4. 建立 客户端连接,通过 TCP三次握手， accept
//      // 返回一个 SocketChannel 读写通道，方便与客户端通信 进行数据读写
//      SocketChannel sc = ssc.accept(); // accept 是阻塞的方法，让线程暂停 【等待客户端建立连接】
//
//      log.debug("已连接... {}", sc);
//
//      channels.add(sc);
//
//
//      // 5. 接收 客户端的 数据， 进行遍历处理
//      // 这里有个 设计缺陷，按 连接通道 顺序遍历，如果 第一个没数据一直阻塞，第二个有数据 也 读不到
//      for (SocketChannel channel : channels) {
//        log.debug("before read ----------------------------------------  {}", channel);
//
//        int len = channel.read(buffer); // read 是阻塞的方法，让线程暂停 【等待客户端发送数据】
//
//        buffer.flip(); //  读模式 (position下标 = 0) 进行 从头get
//
////                debugAll(buffer);
//        final CharBuffer string = Charset.defaultCharset().decode(buffer);
//        System.out.println("读取内容： " + string.toString());
//
//
//        buffer.clear(); //  写模式(position下标 = 容量)，继续 循环写
//
//        log.debug("after read ----------------------------------------  {}", channel);
//        /**
//         * 当执行 这行以后 ，将进入下一个循环
//         * 打印： 20:06:38 [DEBUG] [main] c.a.n.c.Server - connecting...
//         * 进行 阻塞中，线程暂停
//         */
//      }
//      log.debug("connecting end "
//          +
//          "#########################################################################################################################################################################################");
//    }
//
//  }


}
