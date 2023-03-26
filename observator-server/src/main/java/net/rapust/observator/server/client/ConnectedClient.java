package net.rapust.observator.server.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.rapust.observator.commons.crypt.AESKey;
import net.rapust.observator.commons.crypt.RSAPublicKey;
import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.commons.util.Image;
import net.rapust.observator.protocol.connection.Server;
import net.rapust.observator.protocol.packet.Packet;
import net.rapust.observator.protocol.packet.impl.HelloPacket;
import net.rapust.observator.protocol.packet.impl.ScreenChangePacket;
import net.rapust.observator.protocol.packet.impl.ScreenPacket;
import net.rapust.observator.protocol.packet.impl.StopSharingPacket;
import net.rapust.observator.server.gui.ClientViewerGUI;
import net.rapust.observator.server.gui.PasswordGUI;

@Getter
@ToString
public class ConnectedClient {

    @ToString.Exclude
    private final Server.ClientHandler handler;
    
    private final String name;
    private final String IP;
    private final String HWID;

    private final int screenWidth;
    private final int screenHeight;

    @Setter
    private String savedPassword = null;

    @Setter
    private RSAPublicKey rsaPublicKey = null;
    @Setter
    private AESKey aesKey = null;

    @Setter
    private ClientViewerGUI GUI = null;
    @Setter
    private PasswordGUI passwordGUI = null;
    @Setter
    private boolean waiting = false;
    @Setter
    private boolean usedSaved = false;

    public ConnectedClient(Server.ClientHandler handler, HelloPacket helloPacket) {
        this.handler = handler;
        this.name = helloPacket.getName();
        this.IP = handler.getIP();
        this.HWID = helloPacket.getHWID();
        this.screenWidth = helloPacket.getScreenWidth();
        this.screenHeight = helloPacket.getScreenHeight();
    }

    public void sendPacket(Packet packet) {
        try {
            handler.write(packet);
        } catch (Exception e) {
            MasterLogger.error("Ошибка при отправке пакета клиенту " + IP, e);
        }
    }

    public void onPacket(Packet packet) {
        if (packet instanceof ScreenPacket) {
            if (GUI != null && GUI.isVisible()) {
                if (waiting) {
                    waiting = false;
                }
            } else {
                if (waiting) {
                    GUI = new ClientViewerGUI(this);
                    GUI.setVisible(true);

                    if (passwordGUI != null) {
                        savedPassword = passwordGUI.getLastPassword();
                    }
                } else {
                    sendPacket(new StopSharingPacket());
                    return;
                }
            }

            ScreenPacket screenPacket = (ScreenPacket) packet;
            if (GUI == null) {
                return;
            }

            try {
                GUI.applyScreenShot(Image.toBufferedImage(aesKey.decrypt(screenPacket.getBytes())));
            } catch (Exception e) {
                MasterLogger.error(e);
            }
        } else if (packet instanceof ScreenChangePacket) {
            ScreenChangePacket screenChangePacket = (ScreenChangePacket) packet;

            byte[] changes = aesKey.decrypt(screenChangePacket.getChanges());

            if (GUI != null && GUI.isVisible()) {
                GUI.applyChanges(changes);
            }
        }
    }

}
