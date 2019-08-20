function initializeCoreMod() {

    var ASM_HOOKS = "svenhjol/charm/base/CharmAsmHooks";
    var Opcodes = Java.type('org.objectweb.asm.Opcodes');
    var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
    var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
    var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
    var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
    var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');

    return {

        /*
         * Add hook to BrewingRecipeRegistry::isValidInput
         * so that potion stacks can be added to brewing stand slots.
         */
        'BrewingRecipeRegistry': {
            target: {
                'type': 'METHOD',
                'class': 'net.minecraftforge.common.brewing.BrewingRecipeRegistry',
                'methodName': 'isValidInput',
                'methodDesc': '(Lnet/minecraft/item/ItemStack;)Z'
            },
            transformer: function(method) {
                var arrayLength = method.instructions.size();
                for (var i = 0; i < arrayLength; ++i) {
                    var instruction = method.instructions.get(i)
                    var newInstructions = [];

                    if (instruction.getOpcode() == Opcodes.IF_ICMPEQ) {
                        var label = new LabelNode();
                        newInstructions.push(new VarInsnNode(Opcodes.ALOAD, 0));
                        newInstructions.push(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "checkBrewingStandStack", "(Lnet/minecraft/item/ItemStack;)Z", false));
                        newInstructions.push(new JumpInsnNode(Opcodes.IFEQ, label));
                        newInstructions.push(new InsnNode(Opcodes.ICONST_1));
                        newInstructions.push(new InsnNode(Opcodes.IRETURN));
                        newInstructions.push(label);

                        newInstructions.forEach(function(i) {
                            method.instructions.insertBefore(instruction, i);
                        });

                        print("Transformed BrewingRecipeRegistry");
                        break;
                    }
                }

                return method;
            }
        },

        /*
         * Add hook to BarrelTileEntity to work around class type check.
         */
//        'BarrelTileEntity': {
//            target: {
//                'type': 'METHOD',
//                'class': 'net.minecraft.tileentity.BarrelTileEntity',
//                'methodName': 'func_213962_h',
//                'methodDesc': '()V'
//            },
//            transformer: function(method)
//            {
//                var arrayLength = method.instructions.size();
//                for (var i = 0; i < arrayLength; ++i) {
//                    var newInstruction;
//                    var instruction = method.instructions.get(i)
//
//                    if (instruction.getOpcode() == Opcodes.GETSTATIC) {
//
//                        // change GETSTATIC to INVOKESTATIC call to Charm ASM
//                        newInstruction = new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "isBarrel", "(Lnet/minecraft/block/Block;)Z", false);
//                        method.instructions.insertBefore(instruction, newInstruction);
//                        method.instructions.remove(instruction);
//
//                        // change IF_ACMPEQ to IFNE in hook result
//                        var instruction1 = method.instructions.get(i+1);
//                        newInstruction = new JumpInsnNode(Opcodes.IFNE, instruction1.label);
//                        method.instructions.insertBefore(instruction1, newInstruction);
//                        method.instructions.remove(instruction1);
//
//                        print("Transformed BarrelTileEntity");
//                        break;
//                    }
//                }
//
//                return method;
//            }
//        },

        /*
         * Add hook to PotionItem so glint can be disabled in config.
         */
        'PotionItem': {
            target: {
                'type': 'METHOD',
                'class': 'net.minecraft.item.PotionItem',
                'methodName': 'hasEffect',
                'methodDesc': '(Lnet/minecraft/item/ItemStack;)Z'
            },
            transformer: function(method) {
                var arrayLength = method.instructions.size();
                for (var i = 0; i < arrayLength; ++i) {
                    var instruction = method.instructions.get(i);
                    var newInstructions = [];

                    if (instruction.getOpcode() == Opcodes.ALOAD) {
                        var label = new LabelNode();
                        newInstructions.push(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "removePotionGlint", "()Z", false));
                        newInstructions.push(new JumpInsnNode(Opcodes.IFEQ, label));
                        newInstructions.push(new InsnNode(Opcodes.ICONST_0));
                        newInstructions.push(new InsnNode(Opcodes.IRETURN));
                        newInstructions.push(label);

                        newInstructions.forEach(function(i) {
                            method.instructions.insertBefore(instruction, i);
                        });

                        print("Transformed PotionItem");
                        break;
                    }
                }

                return method;
            }
        }
    }
}