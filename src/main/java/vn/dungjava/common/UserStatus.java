package vn.dungjava.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {
    NONE,
    ACTIVE,
    INACTIVE;

    @JsonCreator
    public static UserStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            return UserStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            // ném lỗi để Spring/Jackson trả 400 Bad Request
            throw new IllegalArgumentException(
                    "status must be one of: NONE, ACTIVE, INACTIVE (case-insensitive). Provided: " + value
            );
        }
    }

    @JsonValue
    public String toValue() {
        // Khi trả response ra, luôn trả UPPERCASE, hoặc bạn có thể lowerCase nếu muốn
        return this.name();
    }
}
