package net.rapust.observator.protocol.connection;

import lombok.Data;
import net.rapust.observator.commons.crypt.RSAKeyPair;
import net.rapust.observator.commons.util.Async;
import net.rapust.observator.protocol.connection.impl.Client;
import net.rapust.observator.protocol.connection.impl.Server;
import net.rapust.observator.protocol.listener.Listen;
import net.rapust.observator.protocol.listener.Listener;
import net.rapust.observator.protocol.listener.ListenerRegistry;
import net.rapust.observator.protocol.packet.Packet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Data
public class Connector {

    protected RSAKeyPair keyPair = new RSAKeyPair();
    protected final List<ListenerRegistry> listeners = new ArrayList<>();

    public void registerListeners(Listener... listeners) {
        for (Listener l : listeners) {
            Method[] methods = l.getClass().getDeclaredMethods();
            for (Method m : methods) {
                m.setAccessible(true);
                Class<?>[] params = m.getParameterTypes();
                if (m.isAnnotationPresent(Listen.class) && params.length == 2 &&
                        (params[1] == Server.ClientHandler.class || params[1] == Client.class)) {
                    this.listeners.add(new ListenerRegistry(
                            params[0],
                            l,
                            m
                    ));
                }
            }
        }
    }

    protected void runListeners(Packet packet, Object target) {
        Async.run(() -> {
            for (ListenerRegistry registry : listeners) {
                if (registry.getPacket().isInstance(packet)) {
                    registry.invoke(packet, target);
                }
            }
        });
    }

}
