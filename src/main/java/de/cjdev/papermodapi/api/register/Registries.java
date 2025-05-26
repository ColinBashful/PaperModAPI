package de.cjdev.papermodapi.api.register;

import de.cjdev.papermodapi.PaperModAPI;
import de.cjdev.papermodapi.api.item.CustomItem;

public class Registries {
    public static final Registry<Registry<?>> REGISTERS;
    public static final Registry<CustomItem> ITEM;

    static {
        REGISTERS = new NamedRegister<>("Register");
        ITEM = Registry.register(REGISTERS, PaperModAPI.key("item"), new NamedRegister<>("Item"));
    }
}
