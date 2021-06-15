package com.github.sirblobman.staff.chat.common;

import java.io.File;
import java.util.logging.Logger;

public interface StaffChatX {
    Logger getLogger();
    void onEnable();
    File getDataFolder();
    ChatHandler getChatHandler();
}
