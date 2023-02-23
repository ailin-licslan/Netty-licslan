package licslan.nio.c4networkprogramming.study.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
  public static void main(String[] args) throws IOException {
    SocketChannel sc = SocketChannel.open();

    // 指定要连接的 服务器 和 端口号
    sc.connect(new InetSocketAddress("localhost", 9999));
    sc.write(Charset.defaultCharset().encode("hiWEILINfaddfasfsfdsfsdfsdfsf\ns "));
    // 为了让 代码 不结束 【这里要打 断点】
    System.out.println("waiting ... ");
    System.in.read();
  }
}
