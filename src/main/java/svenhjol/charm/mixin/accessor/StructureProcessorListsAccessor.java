package svenhjol.charm.mixin.accessor;

import com.google.common.collect.ImmutableList;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructureProcessorLists.class)
public interface StructureProcessorListsAccessor {
    @Invoker("register")
    static StructureProcessorList invokeRegister(String id, ImmutableList<StructureProcessor> processorList) {
        throw new IllegalStateException();
    }
}
