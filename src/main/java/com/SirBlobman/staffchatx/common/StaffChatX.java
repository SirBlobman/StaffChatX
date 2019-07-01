package com.SirBlobman.staffchatx.common;

import java.io.File;
import java.util.logging.Logger;

public interface StaffChatX {
    public abstract Logger getLogger();
    public abstract void onEnable();
    public abstract File getDataFolder();
    public abstract ChatHandler getChatHandler();
}