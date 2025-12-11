package vn.dungjava.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    PROCESSING, PENDING, SHIPPED, DELIVERED, CANCELLED;

    @JsonCreator
    public static OrderStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            return OrderStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            // ném lỗi để Spring/Jackson trả 400 Bad Request
            throw new IllegalArgumentException(
                    "status must be one of:  PROCESSING, PENDING, SHIPPED, DELIVERED, CANCELLED (case-insensitive). Provided: " + value
            );
        }
    }

    @JsonValue
    public String toValue() {
        // Khi trả response ra, luôn trả UPPERCASE, hoặc bạn có thể lowerCase nếu muốn
        return this.name();
    }
}
