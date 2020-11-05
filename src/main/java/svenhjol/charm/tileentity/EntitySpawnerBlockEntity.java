package svenhjol.charm.TileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import svenhjol.charm.Charm;
import svenhjol.charm.base.helper.DataBlockHelper;
import svenhjol.charm.module.EntitySpawner;

import java.util.*;

public class EntitySpawnerTileEntity extends TileEntity implements ITickableTileEntity {
    private final static String ENTITY = "entity";
    private final static String PERSIST = "persist";
    private final static String HEALTH = "health";
    private final static String META = "meta";
    private final static String COUNT = "count";
    private final static String ROTATION = "rotation";

    public ResourceLocation entity = null;
    public Rotation rotation = Rotation.NONE;
    public boolean persist = false;
    public double health = 0;
    public int count = 1;
    public String meta = "";

    public EntitySpawnerTileEntity() {
        super(EntitySpawner.BLOCK_ENTITY);
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);

        this.entity = ResourceLocation.tryParse(tag.getString(ENTITY));
        this.persist = tag.getBoolean(PERSIST);
        this.health = tag.getDouble(HEALTH);
        this.count = tag.getInt(COUNT);
        this.meta = tag.getString(META);

        String rot = tag.getString(ROTATION);
        this.rotation = rot.isEmpty() ? BlockRotation.NONE : BlockRotation.valueOf(rot);
    }

    @Override
    public CompoundNBT toTag(CompoundNBT tag) {
        super.write(tag);

        tag.putString(ENTITY, entity.toString());
        tag.putString(ROTATION, rotation.name());
        tag.putBoolean(PERSIST, persist);
        tag.putDouble(HEALTH, health);
        tag.putInt(COUNT, count);
        tag.putString(META, meta);

        return tag;
    }

    @Override
    public void tick() {
        if (world == null || world.getTime() % 10 == 0 || world.getDifficulty() == Difficulty.PEACEFUL)
            return;

        BlockPos pos = getPos();
        List<PlayerEntity> players = world.getNonSpectatingEntities(PlayerEntity.class, new Box(pos).expand(EntitySpawner.triggerDistance));

        if (players.size() == 0)
            return;

        // remove the spawner, create the entity
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
        boolean result = trySpawn(pos);

        if (result) {
            Charm.LOG.debug("EntitySpawner spawned entity " + entity.toString() + " at pos: " + pos);
        } else {
            Charm.LOG.debug("EntitySpawner failed to spawn entity " + entity.toString() + " at pos: " + pos);
        }
    }

    public boolean trySpawn(BlockPos pos) {
        Entity spawned;
        if (world == null)
            return false;

        Optional<EntityType<?>> optionalEntityType = Registry.ENTITY_TYPE.getOrEmpty(entity);
        if (!optionalEntityType.isPresent())
            return false;

        EntityType<?> type = optionalEntityType.get();

        if (type == EntityType.MINECART || type == EntityType.CHEST_MINECART)
            return tryCreateMinecart(type, pos);

        if (type == EntityType.ARMOR_STAND)
            return tryCreateArmorStand(pos);

        for (int i = 0; i < this.count; i++) {
            spawned = type.create(world);
            if (spawned == null)
                return false;

            spawned.refreshPositionAndAngles(pos, 0.0F, 0.0F);

            if (spawned instanceof MobEntity) {
                MobEntity m = (MobEntity) spawned;
                if (persist) m.setPersistent();
                if (health > 0) m.setHealth((float) health);
                m.initialize((ServerIWorld)world, world.getLocalDifficulty(pos), SpawnReason.TRIGGERED, null, null);
            }

            world.addEntity(spawned);
        }
        return true;
    }

    public boolean tryCreateMinecart(EntityType<?> type, BlockPos pos) {
        AbstractMinecartEntity minecart = null;
        if (world == null) return false;

        if (type == EntityType.CHEST_MINECART) {
            minecart = new ChestMinecartEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
            ResourceLocation lootTable = DataBlockHelper.getLootTable(this.meta, LootTables.ABANDONED_MINESHAFT_CHEST);
            ((ChestMinecartEntity)minecart).setLootTable(lootTable, world.random.nextLong());
        } else if (type == EntityType.MINECART) {
            minecart = new MinecartEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        }

        if (minecart == null)
            return false;

        world.addEntity(minecart);

        return true;
    }

    public boolean tryCreateArmorStand(BlockPos pos) {
        if (world == null)
            return false;

        Random random = world.random;
        ArmorStandEntity stand = EntityType.ARMOR_STAND.create(world);
        if (stand == null)
            return false;

        Direction face = DataBlockHelper.getFacing(DataBlockHelper.getValue("facing", this.meta, "north"));
        Direction facing = this.rotation.rotate(face);
        String type = DataBlockHelper.getValue("type", this.meta, "");

        List<Item> ironHeld = new ArrayList<>(Arrays.asList(
            Items.IRON_SWORD, Items.IRON_PICKAXE, Items.IRON_AXE
        ));

        List<Item> goldHeld = new ArrayList<>(Arrays.asList(
            Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE
        ));

        List<Item> diamondHeld = new ArrayList<>(Arrays.asList(
            Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_SHOVEL
        ));

        if (type.equals("chain")) {
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.MAINHAND.getArmorStandSlotId(), new ItemStack(ironHeld.get(random.nextInt(ironHeld.size()))));
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.HEAD.getArmorStandSlotId(), new ItemStack(Items.CHAINMAIL_HELMET));
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.CHEST.getArmorStandSlotId(), new ItemStack(Items.CHAINMAIL_CHESTPLATE));
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.LEGS.getArmorStandSlotId(), new ItemStack(Items.CHAINMAIL_LEGGINGS));
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.FEET.getArmorStandSlotId(), new ItemStack(Items.CHAINMAIL_BOOTS));
        }
        if (type.equals("iron")) {
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.MAINHAND.getArmorStandSlotId(), new ItemStack(ironHeld.get(random.nextInt(ironHeld.size()))));
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.HEAD.getArmorStandSlotId(), new ItemStack(Items.IRON_HELMET));
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.CHEST.getArmorStandSlotId(), new ItemStack(Items.IRON_CHESTPLATE));
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.LEGS.getArmorStandSlotId(), new ItemStack(Items.IRON_LEGGINGS));
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.FEET.getArmorStandSlotId(), new ItemStack(Items.IRON_BOOTS));
        }
        if (type.equals("gold")) {
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.MAINHAND.getArmorStandSlotId(), new ItemStack(goldHeld.get(random.nextInt(goldHeld.size()))));
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.HEAD.getArmorStandSlotId(), new ItemStack(Items.GOLDEN_HELMET));
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.CHEST.getArmorStandSlotId(), new ItemStack(Items.GOLDEN_CHESTPLATE));
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.LEGS.getArmorStandSlotId(), new ItemStack(Items.GOLDEN_LEGGINGS));
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.FEET.getArmorStandSlotId(), new ItemStack(Items.GOLDEN_BOOTS));
        }
        if (type.equals("diamond")) {
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.MAINHAND.getArmorStandSlotId(), new ItemStack(diamondHeld.get(random.nextInt(diamondHeld.size()))));
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.HEAD.getArmorStandSlotId(), new ItemStack(Items.DIAMOND_HELMET));
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.CHEST.getArmorStandSlotId(), new ItemStack(Items.DIAMOND_CHESTPLATE));
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.LEGS.getArmorStandSlotId(), new ItemStack(Items.DIAMOND_LEGGINGS));
            if (random.nextFloat() < 0.25F)
                stand.equip(EquipmentSlot.FEET.getArmorStandSlotId(), new ItemStack(Items.DIAMOND_BOOTS));
        }

        float yaw = facing.getHorizontal();
        stand.refreshPositionAndAngles(pos, yaw, 0.0F);
        world.addEntity(stand);

        return true;
    }
}