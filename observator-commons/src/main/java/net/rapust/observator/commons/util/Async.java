package net.rapust.observator.commons.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Async {

    public void run(Runnable runnable) {
        new Thread(runnable).start();
    }

}
