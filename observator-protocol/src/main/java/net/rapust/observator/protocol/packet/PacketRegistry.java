package net.rapust.observator.protocol.packet;

import lombok.Data;
import lombok.Getter;
import net.rapust.observator.commons.logger.MasterLogger;

import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class PacketRegistry {

    @Getter
    private static final AtomicInteger lastId = new AtomicInteger(-129);

    private final int id;
    private final Class<?> clazz;

    private Constructor<?> constructor;

    public PacketRegistry(Class<?> clazz) {
        this.id = lastId.incrementAndGet();
        this.clazz = clazz;

        try {
            this.constructor = clazz.getConstructor();
        } catch (Exception e) {
            MasterLogger.error(e);
            this.constructor = null;
        }
    }

    public Packet create() {
        try {
            return (Packet) constructor.newInstance();
        } catch (Exception e) {
            MasterLogger.error(e);
            return null;
        }
    }

}
