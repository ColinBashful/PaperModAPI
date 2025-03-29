package de.cjdev.papermodapi.api.item;

public class CustomBlockItem extends CustomItem {
    public CustomBlockItem(Settings settings) {
        super(settings.useBlockTranslationPrefix());
    }
}
