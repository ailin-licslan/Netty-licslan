package licslan.nio.c2;


import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static licslan.nio.c2.ByteBufferUtil.debugAll;

public class TestByteBufferString11 {
    public static void main(String[] args) {
        // 1. 字符串转为 ByteBuffer
        ByteBuffer buffer1 = ByteBuffer.allocate(16);
        buffer1.put("hello".getBytes());
        debugAll(buffer1);

        // 2. Charset 自动切换到读模式了
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer2);

        // 3. wrap  自动切换到读模式了
        ByteBuffer buffer3 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer3);

        // 4. 转为字符串
        String str1 = StandardCharsets.UTF_8.decode(buffer2).toString();
        System.out.println(str1);

        //切换为读模式
        buffer1.flip();
        String str2 = StandardCharsets.UTF_8.decode(buffer1).toString();
        System.out.println(str2);

    }
}
