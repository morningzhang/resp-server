package tonivade.redis.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RequestEncoder extends MessageToByteEncoder<RedisToken> {

    private static final byte ARRAY = '*';
    private static final byte ERROR = '-';
    private static final byte INTEGER = ':';
    private static final byte SIMPLE_STRING = '+';
    private static final byte BULK_STRING = '$';

    private static final byte[] DELIMITER = new byte[] { '\r', '\n' };
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    @Override
    protected void encode(ChannelHandlerContext ctx, RedisToken msg, ByteBuf out) throws Exception {
        out.writeBytes(new ResponseBuilder().encodeToken(msg));
    }

    private static class ResponseBuilder {
        private ByteBufferBuilder builder = new ByteBufferBuilder();

        private byte[] encodeToken(RedisToken msg) throws IOException {
            switch (msg.getType()) {
            case STRING:
                addBulkStr(msg.<SafeString> getValue());
                break;
            case STATUS:
                addSimpleStr(msg.<String> getValue());
                break;
            case INTEGER:
                addInt(msg.<Integer> getValue());
                break;
            case ERROR:
                addError(msg.<String> getValue());
                break;
            case ARRAY:
                addArray(msg.<List<RedisToken>> getValue());
                break;
            case UNKNOWN:
                break;
            }
            return builder.build();
        }

        private void addBulkStr(SafeString str) throws IOException {
            if (str != null) {
                builder.append(BULK_STRING).append(str.length()).append(DELIMITER).append(str);
            } else {
                builder.append(BULK_STRING).append(-1);
            }
            builder.append(DELIMITER);
        }

        private void addSimpleStr(String str) throws IOException {
            builder.append(SIMPLE_STRING).append(str.getBytes(DEFAULT_CHARSET)).append(DELIMITER);
        }

        private void addInt(int value) throws IOException {
            builder.append(INTEGER).append(value).append(DELIMITER);
        }

        private void addError(String str) throws IOException {
            builder.append(ERROR).append(str).append(DELIMITER);
        }

        private void addArray(Collection<RedisToken> array) throws IOException {
            if (array != null) {
                builder.append(ARRAY).append(array.size()).append(DELIMITER);
                for (RedisToken token : array) {
                    builder.append(new ResponseBuilder().encodeToken(token));
                }
            } else {
                builder.append(ARRAY).append(0).append(DELIMITER);
            }
        }
    }

    private static class ByteBufferBuilder {
        private static final int INITIAL_CAPACITY = 1024;

        private ByteBuffer buffer = ByteBuffer.allocate(INITIAL_CAPACITY);

        private ByteBufferBuilder append(int i) {
            append(String.valueOf(i));
            return this;
        }

        private ByteBufferBuilder append(String str) {
            append(str.getBytes(DEFAULT_CHARSET));
            return this;
        }

        private ByteBufferBuilder append(SafeString str) {
            append(str.getBytes());
            return this;
        }

        private ByteBufferBuilder append(byte[] buf) {
            ensureCapacity(buf.length);
            buffer.put(buf);
            return this;
        }

        public ByteBufferBuilder append(byte b) {
            ensureCapacity(1);
            buffer.put(b);
            return this;
        }

        private void ensureCapacity(int len) {
            if (buffer.remaining() < len) {
                growBuffer(len);
            }
        }

        private void growBuffer(int len) {
            int capacity = buffer.capacity() + Math.max(len, INITIAL_CAPACITY);
            buffer = ByteBuffer.allocate(capacity).put(build());
        }

        public byte[] build() {
            byte[] array = new byte[buffer.position()];
            buffer.rewind();
            buffer.get(array);
            return array;
        }
    }

}