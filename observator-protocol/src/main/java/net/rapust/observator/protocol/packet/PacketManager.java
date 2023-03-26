package net.rapust.observator.protocol.packet;

import lombok.Getter;
import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.commons.util.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PacketManager {

    @Getter
    private static PacketManager instance;

    private final List<PacketRegistry> packets;

    private final HashMap<Integer, PacketRegistry> packetsById = new HashMap<>();
    private final HashMap<Class<?>, Integer> idByPacket = new HashMap<>();

    private PacketManager() {
        packets = new ArrayList<>();

        try {
            Reflection.loadClasses(PacketManager.class.getClassLoader(), "net.rapust.observator.protocol.packet.impl").forEach(clazz -> {
                PacketRegistry registry = new PacketRegistry(clazz);
                packets.add(registry);

                packetsById.put(registry.getId(), registry);
                idByPacket.put(clazz, registry.getId());
            });
        } catch (Exception e) {
            MasterLogger.error(e);
        }
    }

    public Packet createPacketById(int id) {
        PacketRegistry registry = packetsById.get(id);

        if (registry == null) {
            return null;
        }

        return registry.create();
    }

    public int getIdByPacket(Packet packet) {
        return idByPacket.get(packet.getClass());
    }

    static {
        instance = new PacketManager();
    }

}
