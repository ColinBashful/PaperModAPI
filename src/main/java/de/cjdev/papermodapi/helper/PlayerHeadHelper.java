package de.cjdev.papermodapi.helper;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class PlayerHeadHelper {
    public static ItemStack getSkull(String texture) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);

        //skull.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile().uuid(Bukkit.getOfflinePlayer("CJDev").getUniqueId()).name("CJDev").build()); //.addProperty(new ProfileProperty("textures", texture)).build());

        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();

        try {
            textures.setSkin(new URL(texture));
            profile.setTextures(textures);
        } catch (MalformedURLException ignored) {
        }

        skull.editMeta(itemMeta -> {
            SkullMeta skullMeta = (SkullMeta) itemMeta;

            skullMeta.setPlayerProfile(profile);
        });

        return skull;
    }
}
