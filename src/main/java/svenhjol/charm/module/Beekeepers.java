package svenhjol.charm.module;

import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.VillagerHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.event.StructureSetupCallback.VillageType;
import svenhjol.charm.mixin.accessor.PointOfInterestTypeAccessor;
import svenhjol.charm.village.BeekeeperTradeOffers;

import static svenhjol.charm.base.helper.VillagerHelper.addTrade;
import static svenhjol.charm.event.StructureSetupCallback.addVillageHouse;

@Module(mod = Charm.MOD_ID, description = "Beekeepers are villagers that trade beekeeping items. Their job site is the beehive.")
public class Beekeepers extends CharmModule {
    public static Identifier ID = new Identifier(Charm.MOD_ID, "beekeeper");
    public static VillagerProfession BEEKEEPER;

    @Config(name = "Beekeeper house weight", description = "Chance of a custom building to spawn. For reference, a vanilla library is 5.")
    public static int buildingWeight = 5;

    @Override
    public void init() {
        BEEKEEPER = VillagerHelper.addProfession(ID, PointOfInterestType.BEEHIVE, SoundEvents.BLOCK_BEEHIVE_WORK);

        // HACK: set ticketCount so that villager can use it as job site
        ((PointOfInterestTypeAccessor)PointOfInterestType.BEEHIVE).setTicketCount(1);

        // register beekeeper trades
        addTrade(BEEKEEPER, 1, new BeekeeperTradeOffers.EmeraldsForFlowers());
        addTrade(BEEKEEPER, 1, new BeekeeperTradeOffers.BottlesForEmerald());
        addTrade(BEEKEEPER, 2, new BeekeeperTradeOffers.EmeraldsForCharcoal());
        addTrade(BEEKEEPER, 2, new BeekeeperTradeOffers.BeeswaxForEmeralds());
        addTrade(BEEKEEPER, 3, new BeekeeperTradeOffers.EmeraldsForHoneycomb());
        addTrade(BEEKEEPER, 3, new BeekeeperTradeOffers.CampfireForEmerald());
        addTrade(BEEKEEPER, 4, new BeekeeperTradeOffers.LeadForEmeralds());
        addTrade(BEEKEEPER, 5, new BeekeeperTradeOffers.PopulatedBeehiveForEmeralds());

        // register beekeeper structures
        addVillageHouse(VillageType.PLAINS, new Identifier("charm:village/plains/houses/plains_beejack"), buildingWeight);
        addVillageHouse(VillageType.PLAINS, new Identifier("charm:village/plains/houses/plains_beekeeper_1"), buildingWeight);
        addVillageHouse(VillageType.DESERT, new Identifier("charm:village/desert/houses/desert_beekeeper_1"), buildingWeight);
        addVillageHouse(VillageType.SAVANNA, new Identifier("charm:village/savanna/houses/savanna_beekeeper_1"), buildingWeight);
        addVillageHouse(VillageType.SAVANNA, new Identifier("charm:village/savanna/houses/savanna_beekeeper_2"), buildingWeight);
        addVillageHouse(VillageType.TAIGA, new Identifier("charm:village/taiga/houses/taiga_beekeeper_1"), buildingWeight);
        addVillageHouse(VillageType.SNOWY, new Identifier("charm:village/snowy/houses/snowy_lumberbee_1"), buildingWeight);
    }
}
