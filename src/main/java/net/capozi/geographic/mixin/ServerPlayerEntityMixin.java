package net.capozi.geographic.mixin;

import net.capozi.geographic.common.datagen.AdvancementProvider;
import net.capozi.geographic.foundation.ItemInit;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
   @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
       Identifier CAVES_AND_CLIFFS = Identifier.ofVanilla( "adventure/fall_from_world_height");
       Identifier ADVENTURING_TIME  = Identifier.ofVanilla("adventure/adventuring_time");
       Identifier HOT_TOURIST_DESTINATION = Identifier.ofVanilla( "nether/explore_nether");
       Identifier SUBSPACE_BUBBLE  = Identifier.ofVanilla("nether/fast_travel");
       Identifier WORLDWALKER = Identifier.of("geographic:worldwalker");
       ItemStack stack = new ItemStack(ItemInit.NORTH_STAR);
       ServerPlayerEntity player = (ServerPlayerEntity) (Object)this;
       if(!player.getWorld().isClient) {
           if(player.getAdvancementTracker().getProgress(player.server.getAdvancementLoader().get(CAVES_AND_CLIFFS)).isDone() &&
                   player.getAdvancementTracker().getProgress(player.server.getAdvancementLoader().get(ADVENTURING_TIME)).isDone() &&
                   player.getAdvancementTracker().getProgress(player.server.getAdvancementLoader().get(HOT_TOURIST_DESTINATION)).isDone() &&
                   player.getAdvancementTracker().getProgress(player.server.getAdvancementLoader().get(SUBSPACE_BUBBLE)).isDone()) {
               if(player.getInventory().contains(stack)) {
                   player.getAdvancementTracker().grantCriterion(player.server.getAdvancementLoader().get(WORLDWALKER), "worldwalker");
               }
           }
       }
   }

}
