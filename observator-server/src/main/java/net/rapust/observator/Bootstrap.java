package net.rapust.observator;

import net.rapust.observator.commons.logger.MasterLogger;
import net.rapust.observator.commons.util.Tray;
import net.rapust.observator.server.ServerAccessor;
import net.rapust.observator.server.gui.MainGUI;

public class Bootstrap {

    public static void main(String[] args) {
        MasterLogger.info("Сервер запускается...");
        Tray.init("server");

        ServerAccessor clientAccessor = new ServerAccessor();
        MainGUI gui = new MainGUI(clientAccessor);

        gui.setVisibility(true);
    }

}
