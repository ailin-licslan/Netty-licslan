package licslan.nio.c4networkprogramming.study.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WriteServer {


  //多路复用仅仅针对网络io

  public static void main(String[] args) throws IOException {
    ServerSocketChannel ssc = ServerSocketChannel.open();
    ssc.configureBlocking(false);
    Selector selector = Selector.open();
    ssc.register(selector, SelectionKey.OP_ACCEPT);
    ssc.bind(new InetSocketAddress(9999));

    while (true) {
      selector.select();
      Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
      while (iterator.hasNext()) {
        SelectionKey next = iterator.next();
        iterator.remove();

        //可连接事件
        if (next.isAcceptable()) {
//          ServerSocketChannel channel = (ServerSocketChannel) next.channel();
//          SocketChannel sc = channel.accept();

          SocketChannel sc = ssc.accept();
          sc.configureBlocking(false);

          SelectionKey scKey = sc.register(selector, 0, null);
          scKey.interestOps(SelectionKey.OP_READ);

          sc.configureBlocking(false);

          //1.向客户端发送大量数据
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < 30000000; i++) {
            sb.append("h");
          }

          ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());

          //写大量数据时  缓存区不够了  不符合非阻塞思想
          //当发送缓冲区满了 写不了的时候  可以去读 做其他的事情 等你写缓冲区好了 可以写的时候  再回来写
          /*while (buffer.hasRemaining()) {
            int write = sc.write(buffer);
            System.out.println("====> " + write);
          }*/

          //上面注释代码改造一下


          //2.返回值表示实际写入了多少字节数
          int write = sc.write(buffer);
          System.out.println("====> " + write);


          //3.判断是否有剩余内容
          if (buffer.hasRemaining()) {
            //scKey.interestOps()+SelectionKey.OP_WRITE 这样写不会覆盖上面的关注的读事件
            //4.关注可写事件
            scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);

            //5.把没有写完的数据挂载到sckey上下次有可写事件发生时接着写
            scKey.attach(buffer);
          }


        } else if (next.isWritable()) {
          //将上面的while循环改造成了 去多次监听写事件再去写了
          ByteBuffer buffer = (ByteBuffer) next.attachment();
          SocketChannel sc = (SocketChannel) next.channel();
          int write = sc.write(buffer);
          System.out.println(write);
          //6.清理数据
          if(!buffer.hasRemaining()){
            //需要清除buffer
            next.attach(null);
            //不再关注可写事情处理完了之后
            next.interestOps(next.interestOps()-SelectionKey.OP_WRITE);
          }
        }

      }
    }


  }


}

