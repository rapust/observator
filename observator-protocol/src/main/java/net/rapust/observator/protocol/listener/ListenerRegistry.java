package net.rapust.observator.protocol.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.protocol.connection.impl.Client;
import net.rapust.observator.protocol.connection.impl.Server;
import net.rapust.observator.protocol.packet.Packet;

import java.lang.reflect.Method;

@Getter
@AllArgsConstructor
public class ListenerRegistry {

    private final Class<?> packet;
    private final Listener listener;
    private final Method method;

    public void invoke(Packet packet, Object target) {
        try {
            method.invoke(listener, packet, target);
        } catch (Exception e) {
            MasterLogger.error("Ошибка в листенере", e);
        }
    }

}
