package net.rapust.observator.protocol.buffer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VarType {

    INT(Byte.parseByte("0")),
    LONG(Byte.parseByte("1")),
    DOUBLE(Byte.parseByte("2")),
    STRING(Byte.parseByte("3")),
    BYTE_ARRAY(Byte.parseByte("4"));

    private final byte descriptor;

    public static VarType getByDescriptor(byte descriptor) {
        for (VarType type : values()) {
            if (type.descriptor == descriptor) {
                return type;
            }
        }
        return INT;
    }

}
