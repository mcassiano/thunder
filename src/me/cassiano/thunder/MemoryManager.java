package me.cassiano.thunder;

/**
 * Created by mateus on 20/11/16.
 */

public class MemoryManager {

    private static final MemoryManager instance = new MemoryManager();

    private int counter = 0;
    private int tempCounter = 0;

    private MemoryManager() {
        allocStack();
    }

    public static MemoryManager get() {
        return instance;
    }

    public int getCurrentAddress() {
        return counter;
    }

    public int getTemporaryCounter() {
        return tempCounter;
    }

    public void resetCounter() {
        counter = 0;
    }

    public void resetTemporaryCounter() {
        tempCounter = 0;
    }

    private void allocStack() {
        counter += 16384; // 4000h
    }

    private int allocNewByte() {
        int tmp = counter;
        counter++;
        return tmp;
    }

    private int allocNewLogical() {
        int tmp = counter;
        counter++;
        return tmp;
    }

    private int allocNewInteger() {
        int tmp = counter;
        counter += 2;
        return tmp;
    }

    private int allocNewString() {
        int tmp = counter;
        counter += 257;
        return tmp;
    }

    private int allocNewString(int size) {
        int tCounter = counter;
        counter += size;
        return tCounter;
    }

    private int allocTemporaryByte() {
        int tmp = tempCounter;
        tempCounter++;
        return tmp;
    }

    private int allocTemporaryLogical() {
        int tmp = tempCounter;
        tempCounter++;
        return tmp;
    }

    private int allocNewTempString() {
        int tCounter = tempCounter;
        tempCounter += 257;
        return tCounter;
    }

    private int allocNewTempString(int size) {
        int tCounter = tempCounter;
        tempCounter += size;
        return tCounter;
    }

    private int allocTemporaryInteger() {
        int tCounter = tempCounter;
        tempCounter += 2;
        return tCounter;
    }

    public int allocVariable(SymbolType type) {

        int address = 0;

        switch (type) {

            case BYTE:
                address = allocNewByte();
                break;

            case INTEGER:
                address = allocNewInteger();
                break;

            case LOGICAL:
                address = allocNewLogical();
                break;

            case STRING:
                address = allocNewString();
                break;
        }

        return address;
    }

    public int allocVariable(SymbolType type, int extraSize) {

        int address = 0;

        switch (type) {

            case STRING:
                address = allocNewString(extraSize);
                break;

            default:
                address = allocVariable(type);
        }

        return address;
    }

    public int allocNewTemp(SymbolType type) {

        int address = 0;

        switch (type) {

            case BYTE:
                address = allocTemporaryByte();
                break;

            case INTEGER:
                address = allocTemporaryInteger();
                break;

            case LOGICAL:
                address = allocTemporaryLogical();
                break;

            case STRING:
                address = allocNewString();
                break;


        }

        return address;
    }

    public int allocNewTemp(SymbolType type, int extraSize) {

        int address;

        switch (type) {

            case STRING:
                address = allocNewTempString(extraSize);
                break;

            default:
                address = allocNewTempString();
        }

        return address;
    }


}
