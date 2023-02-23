package licslan.nio.c4networkprogramming.study.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import licslan.nio.c2.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {


  //分隔符拆分
  private static void split(ByteBuffer source) {
    source.flip();
    for (int i = 0; i < source.limit(); i++) {
      // 找到一条完整消息
      if (source.get(i) == '\n') {
        int length = i + 1 - source.position();
        // 把这条完整消息存入新的 ByteBuffer
        ByteBuffer target = ByteBuffer.allocate(length);
        // 从 source 读，向 target 写
        for (int j = 0; j < length; j++) {
          target.put(source.get());
        }
        ByteBufferUtil.debugAll(target);
      }
    }
    //写模式
    source.compact();
  }

  public static void main(String[] args) throws IOException {


    //1.创建selector对象 可以管理多个channel
    Selector selector = Selector.open();
    ServerSocketChannel ssc = ServerSocketChannel.open();
    ssc.configureBlocking(false);


    //2.建立selector与channel的联系  注册

    //keys包含一些将来要发送的事件 通过它可以知道是什么事件和发生在什么channel上
    //事件包括  accept[连接请求时] connect[客户端建立] read write
    SelectionKey keys = ssc.register(selector, 0, null);


    //只关注 accept事件
    keys.interestOps(SelectionKey.OP_ACCEPT);
    log.debug("register key is {}", keys);
    ssc.bind(new InetSocketAddress(9999));

    while (true) {
      //3.select 方法 没有事件发生 线程阻塞 有事件线程会恢复  线程该歇息就需要休息
      selector.select();
      //4.处理事件  包含所有的可用的事件
      Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
      while (iterator.hasNext()) {
        SelectionKey key = iterator.next();

        //非常重要 要删除  处理完之后的key 要从selectedKeys中删除 否则下次处理npe
        iterator.remove();
        log.debug("key {}", key);


        //5.区分事件类型


        //可连接事件
        if (key.isAcceptable()) {

          //连接事件 服务端的事件 ServerSocketChannel
          ServerSocketChannel channel = (ServerSocketChannel) key.channel();
          SocketChannel sc = channel.accept();
          //非阻塞
          sc.configureBlocking(false);
          ByteBuffer buffer = ByteBuffer.allocate(16);
          //设置附件 属于每个channel自己独有的 将bytebuffer作为附件关联到seleckey上
          SelectionKey scKey = sc.register(selector, 0, buffer);
          //对可读事件感兴趣
          scKey.interestOps(key.OP_READ);
          log.debug("sc {}", sc);
        }
        //可读事件
        else if (key.isReadable()) {
          //客户端断开处理问题
          try {
            //读事件 客户端的事件 SocketChannel
            SocketChannel channel = (SocketChannel) key.channel();

            //注意这里需要处理消息的边界  bytebuffer与传递的数据长度不一样  可能产生半包 粘包等问题
            ByteBuffer buffer = (ByteBuffer) key.attachment();
            int read = channel.read(buffer);
            //正常断开 read == -1
            if (read == -1) {
              key.cancel();
            } else {
              //思路 1.固定消息长度 浪费带宽 2.分隔符拆分 效率低  3.Http1.1 TLV Http2.0 LTV
//              buffer.flip();
//              debugRead(buffer);
              split(buffer);
              //扩容  netty这里做的更加好 可以自动扩缩容 bytebuffer 参考p36 思路
              //优点消息连续容易处理  缺点数据拷贝消耗性能  netty更好  服务器有哪些思路可以来设计呢  高级程序员需要思考的问题
              if (buffer.position() == buffer.limit()) {
                ByteBuffer newbuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                //切换读模式
                buffer.flip();
                newbuffer.put(buffer);
                //将扩容后的大小放回去
                key.attach(newbuffer);
              }

            }

          } catch (Exception e) {
            e.printStackTrace();
            //因为客户端异常断开了  所以需要移除key 真正删除
            key.cancel();
          }

        }

      }
    }
  }


}

