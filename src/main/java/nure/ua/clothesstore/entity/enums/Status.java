package nure.ua.clothesstore.entity.enums;

public enum Status {
    PROCESSING("Processing"),
    ACCEPTED("Accepted"),
    SENT("Sent"),
    DELIVERED("Delivered");
    String status;

    Status(String status) {
        this.status = status;
    }
    public String getStatus(){
        return status;
    }
}
