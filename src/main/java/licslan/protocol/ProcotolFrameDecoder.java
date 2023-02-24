package licslan.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author licslan
 * 解决半包 粘包  需要记录状态 不可共享
 */
public class ProcotolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProcotolFrameDecoder() {
        this(1024, 12, 4, 0, 0);
    }

    public ProcotolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
