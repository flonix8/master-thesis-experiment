package de.flonix.master;

import de.flonix.master.benchmark.Message;
import org.apache.commons.lang3.RandomStringUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Test {
    public static void main(String[] args) {
        long timestamp = 123456678901234L;
        String id = UUID.randomUUID().toString();
        byte delimiter = ";".getBytes(StandardCharsets.US_ASCII)[0];

        String buffer = RandomStringUtils.randomAlphanumeric(10000000);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(10000000);
        byteBuffer.put(buffer.getBytes(StandardCharsets.US_ASCII)).rewind();
        byte[] byteArrayBuffer = new byte[10000000];
        byteBuffer.get(byteArrayBuffer);


        long startTime = System.nanoTime();
        ByteBuffer byteBuffer3 = ByteBuffer.allocate(1000053);
        byteBuffer3.put(id.getBytes(StandardCharsets.US_ASCII));
        byteBuffer3.put((byte)59);
        byteBuffer3.put(String.valueOf(timestamp).getBytes(StandardCharsets.US_ASCII));
        byteBuffer3.put((byte)59);
        byteBuffer3.put(byteArrayBuffer, 0, 1000000);
        byte[] byteValue3 = byteBuffer3.array();
        System.out.println("Using Bytebuffer.get() to read from pregenerated random string byte buffer: " + (System.nanoTime() - startTime) + "ns");

        new Message("test", 100);

        startTime = System.nanoTime();

        Message msg = new Message("testtest", 1000000);
        msg.setSentTimestamp();
        msg.getMqttPayload();

        System.out.println("Creating a new Message object with payloadSize=1000000: " + (System.nanoTime() - startTime) + "ns");

    }
}
