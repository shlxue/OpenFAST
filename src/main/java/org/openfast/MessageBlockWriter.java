package org.openfast;

import java.io.OutputStream;

public interface MessageBlockWriter {

    MessageBlockWriter NULL = new MessageBlockWriter() {
        public void writeBlockLength(OutputStream out, byte[] data) {
        }};

    void writeBlockLength(OutputStream out, byte[] data);
}
