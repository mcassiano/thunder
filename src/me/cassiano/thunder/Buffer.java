package me.cassiano.thunder;

/**
 * Created by mateus on 20/11/16.
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Buffer {

    private BufferedWriter fileDescriptor;
    private List<String> buffer = new ArrayList<>();

    public Buffer(String outputFile) throws IOException {
        buffer = new ArrayList<>();
        fileDescriptor = new BufferedWriter(new FileWriter(outputFile));
    }

    public List<String> getBuffer() {
        return buffer;
    }

    public void dump() throws IOException {

        for (String line : buffer) {
            fileDescriptor.write(line);
            fileDescriptor.newLine();
        }
        fileDescriptor.close();
    }
}