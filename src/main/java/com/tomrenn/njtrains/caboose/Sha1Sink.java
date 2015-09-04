package com.tomrenn.njtrains.caboose;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import okio.Buffer;
import okio.Sink;
import okio.Timeout;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public class Sha1Sink implements Sink {
    Sink sink;
    Buffer tmpBuffer;
    Hasher sha1;

    public Sha1Sink(Sink sink) throws NoSuchAlgorithmException{
        this.sink = sink;
        tmpBuffer = new Buffer();
        sha1 = Hashing.sha1().newHasher();
    }

    public String hashString() {
        return sha1.hash().toString();
    }

    private void updateHash(byte[] bytes){
        sha1.putBytes(bytes);
    }

    @Override
    public void write(Buffer source, long byteCount) throws IOException {
        byte[] bytes = source.readByteArray(byteCount);
        updateHash(bytes);
        sink.write(tmpBuffer.write(bytes), byteCount);
    }

    @Override
    public void flush() throws IOException {
        sink.flush();
    }

    @Override
    public Timeout timeout() {
        return sink.timeout();
    }

    @Override
    public void close() throws IOException {
        sink.close();
    }
}
