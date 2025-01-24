package de.cjdev.papermodapi.api.component;

import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.keys.MobEffectKeys;
import io.papermc.paper.registry.keys.SoundEventKeys;
import io.papermc.paper.registry.set.RegistrySet;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Consumables {
    public static final Consumable DEFAULT_FOOD = defaultFood().build();
    public static final Consumable DEFAULT_DRINK = defaultDrink().build();
    public static final Consumable HONEY_BOTTLE;
    public static final Consumable OMINOUS_BOTTLE;
    public static final Consumable DRIED_KELP;
    public static final Consumable CHICKEN;
    public static final Consumable ENCHANTED_GOLDEN_APPLE;
    public static final Consumable GOLDEN_APPLE;
    public static final Consumable POISONOUS_POTATO;
    public static final Consumable PUFFERFISH;
    public static final Consumable ROTTEN_FLESH;
    public static final Consumable SPIDER_EYE;
    public static final Consumable MILK_BUCKET;
    public static final Consumable CHORUS_FRUIT;

    private Consumables() {}

    public static Consumable.Builder defaultFood() {
        return Consumable.consumable().consumeSeconds(1.6F).animation(ItemUseAnimation.EAT).sound(SoundEventKeys.ENTITY_GENERIC_EAT).hasConsumeParticles(true);
    }

    public static Consumable.Builder defaultDrink() {
        return Consumable.consumable().consumeSeconds(1.6F).animation(ItemUseAnimation.DRINK).sound(SoundEventKeys.ENTITY_GENERIC_DRINK).hasConsumeParticles(false);
    }

    static {
        HONEY_BOTTLE = defaultDrink().consumeSeconds(2.0F).sound(SoundEventKeys.ITEM_HONEY_BOTTLE_DRINK).addEffect(ConsumeEffect.removeEffects(RegistrySet.keySet(MobEffectKeys.POISON.registryKey()))).build();
        OMINOUS_BOTTLE = defaultDrink().addEffect(ConsumeEffect.playSoundConsumeEffect(SoundEventKeys.ITEM_OMINOUS_BOTTLE_DISPOSE)).build();
        DRIED_KELP = defaultFood().consumeSeconds(0.8F).build();
        CHICKEN = defaultFood().addEffect(ConsumeEffect.applyStatusEffects(List.of(PotionEffectType.HUNGER.createEffect(600, 0)), 0.3F)).build();
        ENCHANTED_GOLDEN_APPLE = defaultFood().addEffect(ConsumeEffect.applyStatusEffects(List.of(PotionEffectType.REGENERATION.createEffect(400, 1), PotionEffectType.RESISTANCE.createEffect(6000, 0), PotionEffectType.FIRE_RESISTANCE.createEffect(6000, 0), PotionEffectType.ABSORPTION.createEffect(2400, 3)), 1f)).build();
        GOLDEN_APPLE = defaultFood().addEffect(ConsumeEffect.applyStatusEffects(List.of(PotionEffectType.REGENERATION.createEffect(100, 1), PotionEffectType.ABSORPTION.createEffect(2400, 0)), 1f)).build();
        POISONOUS_POTATO = defaultFood().addEffect(ConsumeEffect.applyStatusEffects(List.of(PotionEffectType.POISON.createEffect(100, 0)), 0.6F)).build();
        PUFFERFISH = defaultFood().addEffect(ConsumeEffect.applyStatusEffects(List.of(PotionEffectType.POISON.createEffect(1200, 1), PotionEffectType.HUNGER.createEffect(300, 2), PotionEffectType.NAUSEA.createEffect(300, 0)), 1f)).build();
        ROTTEN_FLESH = defaultFood().addEffect(ConsumeEffect.applyStatusEffects(List.of(PotionEffectType.HUNGER.createEffect(600, 0)), 0.8f)).build();
        SPIDER_EYE = defaultFood().addEffect(ConsumeEffect.applyStatusEffects(List.of(PotionEffectType.POISON.createEffect(100, 0)), 1f)).build();
        MILK_BUCKET = defaultDrink().addEffect(ConsumeEffect.clearAllStatusEffects()).build();
        CHORUS_FRUIT = defaultFood().addEffect(ConsumeEffect.teleportRandomlyEffect(16)).build();
    }
}
