package nure.ua.clothesstore.entity.enums;

public enum Size {
    XS("XS"),
    S("S"),
    M("M"),
    L("L"),
    XL("XL"),
    XXL("XXL");
    String size;
    Size(String size) {
        this.size=size;
    }
    public String getSize() {
        return size;
    }
}
