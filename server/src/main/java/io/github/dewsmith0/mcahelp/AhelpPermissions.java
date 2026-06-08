package io.github.dewsmith0.mcahelp;

import org.bukkit.permissions.Permission;

public class AhelpPermissions {
    public static final Permission RECEIVE_ADMIN = new Permission("mcahelp.adminreceive"); // receive ahelps from players as admin?
    public static final Permission SEND_ADMIN = new Permission("mcahelp.adminsend"); // /ahelp asend
    public static final Permission OPEN_OTHER = new Permission("mcahelp.openother"); // /ahelp open <player>
    public static final Permission RELOAD_CONFIG = new Permission("mcahelp.reloadconfig"); // /mc-ahelp reload-config
}
