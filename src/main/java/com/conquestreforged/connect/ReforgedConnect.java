package com.conquestreforged.connect;

import com.conquestreforged.connect.server.ServerInfoList;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

@Mod("connect")
public class ReforgedConnect {

    public static final Logger logger = LogManager.getLogger(ReforgedConnect.class);

    public ReforgedConnect() {
        logger.info("initialized");
        ServerInfoList.getInstance().setInterval(30, TimeUnit.MINUTES);
    }
}
