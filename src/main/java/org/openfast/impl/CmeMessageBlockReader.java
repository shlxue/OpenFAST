package org.openfast.impl;

import java.io.IOException;
import java.io.InputStream;
import org.openfast.Message;
import org.openfast.MessageBlockReader;

public class CmeMessageBlockReader implements MessageBlockReader {
    public void messageRead(InputStream in, Message message) {
    }

    public boolean readBlock(InputStream in) {
        try {
            in.read();
            in.read();
            in.read();
            in.read();
            in.read();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
