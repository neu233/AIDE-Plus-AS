//
// Decompiled by Jadx - 966ms
//
package com.sdklite.sed;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class StreamEditor implements Closeable {
    private final ByteOrder byteOrder;
    private final RandomAccessFile raf;

    public StreamEditor(File file) throws FileNotFoundException {
        this(file, ByteOrder.LITTLE_ENDIAN);
    }

    public StreamEditor(File file, ByteOrder byteOrder) throws FileNotFoundException {
        this.raf = new RandomAccessFile(file, "rw");
        this.byteOrder = byteOrder;
    }

    public boolean hasRemaining() throws IOException {
        return this.raf.getFilePointer() < this.raf.length();
    }

    public long remaining() throws IOException {
        return this.raf.length() - this.raf.getFilePointer();
    }

    public long tell() throws IOException {
        return this.raf.getFilePointer();
    }

    public void seek(long pos) throws IOException {
        this.raf.seek(pos);
    }

    public void skip(int n) throws IOException {
        this.raf.skipBytes(n);
    }

    public int peek() throws IOException {
        long p = tell();
        try {
            return read();
        } finally {
            seek(p);
        }
    }

    public byte peekByte() throws IOException {
        long p = tell();
        try {
            return readByte();
        } finally {
            seek(p);
        }
    }

    public int peekUnsignedByte() throws IOException {
        long p = tell();
        try {
            return read();
        } finally {
            seek(p);
        }
    }

    public char peekChar() throws IOException {
        long p = tell();
        try {
            return readChar();
        } finally {
            seek(p);
        }
    }

    public short peekShort() throws IOException {
        long p = tell();
        try {
            return readShort();
        } finally {
            seek(p);
        }
    }

    public int peekUnsignedShort() throws IOException {
        long p = tell();
        try {
            return readUnsignedShort();
        } finally {
            seek(p);
        }
    }

    public int peekInt() throws IOException {
        long p = tell();
        try {
            return readInt();
        } finally {
            seek(p);
        }
    }

    public float peekFloat() throws IOException {
        long p = tell();
        try {
            return Float.intBitsToFloat(readInt());
        } finally {
            seek(p);
        }
    }

    public long peekLong() throws IOException {
        long p = tell();
        try {
            return readLong();
        } finally {
            seek(p);
        }
    }

    public double peekDouble() throws IOException {
        long p = tell();
        try {
            return Double.longBitsToDouble(readLong());
        } finally {
            seek(p);
        }
    }

    public int read() throws IOException {
        int b = this.raf.read();
        if (-1 == b) {
            throw new EOFException();
        }
        return b;
    }

    public int read(ByteBuffer buffer) throws IOException {
        int nbytes = this.raf.read(buffer.array());
        if (-1 == nbytes) {
            throw new EOFException();
        }
        return nbytes;
    }

    public int read(byte[] buffer) throws IOException {
        int nbytes = this.raf.read(buffer);
        if (-1 == nbytes) {
            throw new EOFException();
        }
        return nbytes;
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        int nbytes = this.raf.read(buf, off, len);
        if (-1 == nbytes) {
            throw new EOFException();
        }
        return nbytes;
    }

    public byte readByte() throws IOException {
        int b = read();
        if (-1 == b) {
            throw new EOFException();
        }
        return (byte) b;
    }

    public int readUnsignedByte() throws IOException {
        int b = read();
        if (-1 == b) {
            throw new EOFException();
        }
        return b;
    }

    public char readChar() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2).order(this.byteOrder);
        if (-1 == this.raf.read(buffer.array())) {
            throw new EOFException();
        }
        buffer.rewind();
        return buffer.getChar();
    }

    public short readShort() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2).order(this.byteOrder);
        if (-1 == this.raf.read(buffer.array())) {
            throw new EOFException();
        }
        buffer.rewind();
        return buffer.getShort();
    }

    public int readUnsignedShort() throws IOException {
        int b1 = read();
        int b2 = read();
        return this.byteOrder == ByteOrder.BIG_ENDIAN ? (b1 << 8) | b2 : (b2 << 8) | b1;
    }

    public int readInt() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4).order(this.byteOrder);
        if (-1 == this.raf.read(buffer.array())) {
            throw new EOFException();
        }
        buffer.rewind();
        return buffer.getInt();
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public long readLong() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8).order(this.byteOrder);
        if (-1 == this.raf.read(buffer.array())) {
            throw new EOFException();
        }
        buffer.rewind();
        return buffer.getLong();
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public void write(byte b) throws IOException {
        this.raf.write(b);
    }

    public void write(byte[] b) throws IOException {
        this.raf.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        this.raf.write(b, off, len);
    }

    public void writeShort(short v) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2).order(this.byteOrder);
        this.raf.write(buffer.putShort(v).array());
    }

    public void writeInt(int v) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4).order(this.byteOrder);
        this.raf.write(buffer.putInt(v).array());
    }

    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    public void writeLong(long v) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8).order(this.byteOrder);
        this.raf.write(buffer.putLong(v).array());
    }

    @Override
    public void close() throws IOException {
        this.raf.close();
    }
}

