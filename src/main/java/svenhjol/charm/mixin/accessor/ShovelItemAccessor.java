package svenhjol.charm.mixin.accessor;

import net.minecraft.block.Block;
import net.minecraft.item.ShovelItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ShovelItem.class)
public interface ShovelItemAccessor {
    @Accessor("EFFECTIVE_BLOCKS")
    static Set<Block> getEffectiveBlocks() {
        throw new IllegalStateException();
    }
}