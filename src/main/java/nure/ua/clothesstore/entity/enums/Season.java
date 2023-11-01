package nure.ua.clothesstore.entity.enums;

public enum Season {
    SUMMER("Summer"),
    FALL("Fall"),
    WINTER("Winter"),
    SPRING("Spring");
    String season;

    Season(String season) {
        this.season=season;
    }

    public String getSeason() {
        return season;
    }
}
