package ladysnake.ratsmischief.common;

import ladysnake.ratsmischief.common.armormaterials.RatMaskArmorMaterial;
import ladysnake.ratsmischief.common.block.RatNestBlock;
import ladysnake.ratsmischief.common.command.PlayerRatifyCommand;
import ladysnake.ratsmischief.common.command.PlayerUnratifyCommand;
import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.item.RatPouchItem;
import ladysnake.ratsmischief.common.item.RatStaffItem;
import ladysnake.ratsmischief.common.village.MischiefTradeOffers;
import ladysnake.ratsmischief.common.world.RatSpawner;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.item.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import software.bernie.geckolib3.GeckoLib;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mischief implements ModInitializer {
    public static final String MODID = "ratsmischief";
    public static final boolean IS_WORLD_RAT_DAY = DateTimeFormatter.ofPattern("dd/MM").format(LocalDateTime.now()).equals("04/04");
    public static EntityType<RatEntity> RAT;
    public static Item RAT_SPAWN_EGG;

    public static Item LEATHER_RAT_POUCH;
    public static Item TWISTED_RAT_POUCH;
    public static Item PURPUR_RAT_POUCH;

    public static Item HARVEST_STAFF;
    public static Item COLLECTION_STAFF;
    public static Item SKIRMISH_STAFF;
    public static Item LOVE_STAFF;

    public static Item RAT_MASK;
    public static Item ELYTRAT;

    public static Block RAT_NEST;

    private static <T extends Entity> EntityType<T> registerEntity(String s, EntityType<T> entityType) {
        return Registry.register(Registry.ENTITY_TYPE, MODID + ":" + s, entityType);
    }

    public static Item registerItem(Item item, String name) {
        Registry.register(Registry.ITEM, MODID + ":" + name, item);
        return item;
    }

    private static Block registerBlock(Block block, String name, ItemGroup itemGroup) {
        Registry.register(Registry.BLOCK, MODID + ":" + name, block);

        if (itemGroup != null) {
            BlockItem item = new BlockItem(block, new Item.Settings().group(itemGroup));
            item.appendBlocks(Item.BLOCK_ITEMS, item);
            registerItem(item, name);
        }

        return block;
    }

    @Override
    public void onInitialize() {
        GeckoLib.initialize();

        RAT = registerEntity("rat", FabricEntityTypeBuilder.createMob().entityFactory(RatEntity::new).spawnGroup(SpawnGroup.AMBIENT).dimensions(EntityDimensions.changing(0.6F, 0.4F)).trackRangeBlocks(8).spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RatEntity::canMobSpawn).build());
        FabricDefaultAttributeRegistry.register(RAT, RatEntity.createEntityAttributes());

        // ratify and untratify commands
        CommandRegistrationCallback.EVENT.register((commandDispatcher, b) ->
                PlayerRatifyCommand.register(commandDispatcher)
        );
        CommandRegistrationCallback.EVENT.register((commandDispatcher, b) ->
                PlayerUnratifyCommand.register(commandDispatcher)
        );

        // rat custom spawner
        RatSpawner ratSpawner = new RatSpawner();
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // spawn rats
            server.getWorlds().forEach(world -> {
                ratSpawner.spawn(world, server.getSaveProperties().getDifficulty() != Difficulty.PEACEFUL, server.shouldSpawnAnimals());
            });

        });

        RAT_SPAWN_EGG = registerItem(new SpawnEggItem(RAT, 0x1A1A1A, 0xF2ADA1, (new Item.Settings()).group(ItemGroup.MISC)), "rat_spawn_egg");

        LEATHER_RAT_POUCH = registerItem(new RatPouchItem((new Item.Settings()).group(ItemGroup.TOOLS).maxCount(1), 5), "leather_rat_pouch");
        TWISTED_RAT_POUCH = registerItem(new RatPouchItem((new Item.Settings()).group(ItemGroup.TOOLS).maxCount(1), 10), "twisted_rat_pouch");
        PURPUR_RAT_POUCH = registerItem(new RatPouchItem((new Item.Settings()).group(ItemGroup.TOOLS).maxCount(1), 20), "purpur_rat_pouch");

        HARVEST_STAFF = registerItem(new RatStaffItem((new Item.Settings()).group(ItemGroup.TOOLS).maxCount(1), RatStaffItem.Action.HARVEST), "harvest_staff");
        COLLECTION_STAFF = registerItem(new RatStaffItem((new Item.Settings()).group(ItemGroup.TOOLS).maxCount(1), RatStaffItem.Action.COLLECT), "collection_staff");
        LOVE_STAFF = registerItem(new RatStaffItem((new Item.Settings()).group(ItemGroup.TOOLS).maxCount(1), RatStaffItem.Action.LOVE), "love_staff");

        RAT_MASK = registerItem(new ArmorItem(RatMaskArmorMaterial.RAT_MASK, EquipmentSlot.HEAD, (new Item.Settings()).group(ItemGroup.COMBAT)), "rat_mask");
        ELYTRAT = registerItem(new Item(new Item.Settings().group(ItemGroup.MISC).maxCount(16)), "elytrat");

        RAT_NEST = registerBlock(new RatNestBlock(FabricBlockSettings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD).suffocates((state, world, pos) -> false)), "rat_nest", ItemGroup.DECORATIONS);

        // rat kid painting
        Registry.register(Registry.PAINTING_MOTIVE, new Identifier(MODID, "a_rat_in_time"), new PaintingMotive(64, 48));

        TradeOfferHelper.registerWanderingTraderOffers(1, factories -> factories.add(new MischiefTradeOffers.SellItemFactory(Mischief.RAT_MASK, 40, 1, 3, 40)));
    }

}
