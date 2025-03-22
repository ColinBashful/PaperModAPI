# PaperModAPI
PaperModAPI is a library making Creation of Custom Features like Custom Items a breeze.\

Join our [Discord Server](https://discord.gg/Nq6bMazJe9) to get help developing your Plugin utilizing this library.\
There you can also open a feature request, or give feedback. ^^


Gradle (Groovy DSL):
```groovy
repositories {
    maven { url "https://api.modrinth.com/maven" }
}

dependencies {
    compileOnly "maven.modrinth:papermodapi:<version-number>+<minecraft-version>"
}
```
Gradle (Kotlin DSL):
```kotlin
repositories {
    maven(url = "https://api.modrinth.com/maven")
}

dependencies {
    compileOnly("maven.modrinth:papermodapi:<version-number>+<minecraft-version>")
}
```
## Usage
Look into following projects for better examples:
- [Example]()
```java
class Plugin extends JavaPlugin {
    public static class SuicideBombItem extends CustomItem {
        public SuicideBombItem(Settings settings) {
            super(settings.modelId(NamespacedKey.minecraft("tnt"))
                    .component(DataComponentTypes.CONSUMABLE,
                            Consumable.consumable()
                                    .hasConsumeParticles(false)
                                    .consumeSeconds(1)
                                    .animation(ItemUseAnimation.BOW)
                                    .sound(SoundEventKeys.ENTITY_CREEPER_PRIMED)
                                    .build()));
        }

        @Override
        public Component getName(ItemStack stack) {
            return Component.text("Suicide Bomb");
        }

        @Override
        public void onConsumed(PlayerItemConsumeEvent event) {
            Player player = event.getPlayer();
            player.getWorld().createExplosion(player, player.getLocation(), 100, true, true, false);
        }
    }

    @Override
    public void onEnable() {
        CustomItem SUICIDE_BOMB = CustomItems.register(key("suicide_bomb"), SuicideBombItem::new);
        CustomItemGroups.register(key("itemgroup"), CustomItemGroup.builder()
                .displayName(Component.text("Example ItemGroup"))
                .icon(() -> SUI.getDisplayStack())
                .entries((hasOp, entries) -> {
                    entries.add(SUICIDE_BOMB);
                })
                .build());
    }

    public NamespacedKey key(String id) {
        return NamespacedKey.fromString(id, this);
    }
}
```
## TODO
- [X] Custom Items
  - [X] Custom Classes
  - [X] Custom Item Components
- [ ] Custom Blocks
- [ ] Custom GUI
## Libraries for other Features
- Animations | [UntitledModelEngine]()
- Recipes | [RecipeAPI]()
