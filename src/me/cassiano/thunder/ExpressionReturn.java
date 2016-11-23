package me.cassiano.thunder;


public class ExpressionReturn {

    private int address;
    private SymbolType type;
    private String value;

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public SymbolType getType() {
        return type;
    }

    public void setType(SymbolType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return type == null ? "" : type.toString();
    }
}
