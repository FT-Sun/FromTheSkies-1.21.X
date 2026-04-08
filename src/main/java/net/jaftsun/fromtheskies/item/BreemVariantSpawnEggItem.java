package net.jaftsun.fromtheskies.item;

import net.jaftsun.fromtheskies.entity.BreemVariant;
import net.jaftsun.fromtheskies.entity.custom.BreemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class BreemVariantSpawnEggItem extends SpawnEggItem {

    private final BreemVariant variant;

    public BreemVariantSpawnEggItem(EntityType<? extends BreemEntity> entityType, BreemVariant variant,
                                    Item.Properties properties) {
        super(entityType, variant.getSpawnEggBackgroundColor(), variant.getSpawnEggHighlightColor(), properties);

        this.variant = variant;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        Player player = context.getPlayer();

        EntityType<?> type = this.getType(context.getItemInHand());
        var entity = type.spawn(
                serverLevel,
                context.getItemInHand(),
                player,
                pos,
//                MobSpawnType.SPAWN_ITEM,
                MobSpawnType.SPAWN_EGG,
                true,
                context.getClickedFace() == Direction.UP
        );

        if (entity instanceof BreemEntity breem) {
            breem.setVariant(this.variant);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
