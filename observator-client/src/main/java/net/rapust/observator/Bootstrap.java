package net.rapust.observator;

import net.rapust.observator.client.ClientAccessor;
import net.rapust.observator.client.gui.MainGUI;
import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.commons.util.Tray;

public class Bootstrap {

    public static void main(String[] args) {
        MasterLogger.info("Клиент запускается...");
        Tray.init("client");

        ClientAccessor clientAccessor = new ClientAccessor();
        MainGUI gui = new MainGUI(clientAccessor);

        gui.setVisibility(true);
    }

}
