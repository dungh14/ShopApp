package vn.dungjava.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserType {
    OWNER,
    ADMIN,
    USER;

    @JsonCreator
    public static UserType fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            return UserType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            // ném lỗi để Spring/Jackson trả 400 Bad Request
            throw new IllegalArgumentException(
                    "type must be one of: OWNER, ADMIN, USER (case-insensitive). Provided: " + value
            );
        }
    }

    @JsonValue
    public String toValue() {
        // Khi trả response ra, luôn trả UPPERCASE, hoặc bạn có thể lowerCase nếu muốn
        return this.name();
    }
}
