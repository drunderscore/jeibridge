package xyz.jame.jeibridge;

public class PacketId
{
    private PacketId()
    {
    }

    public enum ServerBound
    {
        RECIPE_TRANSFER,
        DELETE_ITEM,
        GIVE_ITEM,
        SET_HOTBAR_ITEM,
        CHEAT_PERMISSION_REQUEST;

        static final ServerBound[] VALUES = values();
    }

    public enum ClientBound
    {
        CHEAT_PERMISSION
    }
}
