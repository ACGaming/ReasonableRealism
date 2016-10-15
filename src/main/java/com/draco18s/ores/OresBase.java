package com.draco18s.ores;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.draco18s.hardlib.CogHelper;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.RecipesUtil;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.blockproperties.EnumOreType;
import com.draco18s.hardlib.capability.CapabilityMechanicalPower;
import com.draco18s.ores.block.BlockAxel;
import com.draco18s.ores.block.BlockDummyOre;
import com.draco18s.ores.block.BlockMillstone;
import com.draco18s.ores.block.BlockPackager;
import com.draco18s.ores.block.BlockSifter;
import com.draco18s.ores.block.BlockSluice;
import com.draco18s.ores.block.BlockWindvane;
import com.draco18s.ores.block.ore.BlockHardDiamond;
import com.draco18s.ores.block.ore.BlockHardGold;
import com.draco18s.ores.block.ore.BlockHardIron;
import com.draco18s.ores.block.ore.BlockLimonite;
import com.draco18s.ores.enchantments.EnchantmentProspector;
import com.draco18s.ores.enchantments.EnchantmentPulverize;
import com.draco18s.ores.enchantments.EnchantmentVeinCracker;
import com.draco18s.ores.entities.EntityOreMinecart;
import com.draco18s.ores.entities.TileEntityAxel;
import com.draco18s.ores.entities.TileEntityMillstone;
import com.draco18s.ores.entities.TileEntityPackager;
import com.draco18s.ores.entities.TileEntitySifter;
import com.draco18s.ores.entities.TileEntityBasicSluice;
import com.draco18s.ores.flowers.FlowerIntegration;
import com.draco18s.ores.item.ItemDiamondStudHoe;
import com.draco18s.ores.item.ItemDiamondStudPickaxe;
import com.draco18s.ores.item.ItemDiamondStudShovel;
import com.draco18s.ores.item.ItemDustLarge;
import com.draco18s.ores.item.ItemDustSmall;
import com.draco18s.ores.item.ItemEntityOreCart;
import com.draco18s.ores.item.ItemNugget;
import com.draco18s.ores.item.ItemOreBlock;
import com.draco18s.ores.item.ItemRawOre;
import com.draco18s.ores.networking.ClientOreParticleHandler;
import com.draco18s.ores.networking.ServerOreCartHandler;
import com.draco18s.ores.networking.ToClientMessageOreParticles;
import com.draco18s.ores.networking.ToServerMessageOreCart;
import com.draco18s.ores.recipes.OreProcessingRecipes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStone;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.client.model.ModelFluid;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid="harderores", name="HarderOres", version="{@version:ore}", dependencies = "required-after:hardlib;required-after:oreflowers")//@[{@version:lib},)  [{@version:flowers},)
public class OresBase {
	@Instance("harderores")
	public static OresBase instance;
	
	@SidedProxy(clientSide="com.draco18s.ores.client.ClientProxy", serverSide="com.draco18s.ores.CommonProxy")
	public static CommonProxy proxy;
	
	public static Logger logger;
	
	public static Block oreLimonite;
	public static Block oreIron;
	public static Block oreGold;
	public static Block oreDiamond;
	
	public static Block dummyOreIron;
	public static Block dummyOreGold;
	public static Block dummyOreDiamond;
	
	public static Block millstone;
	public static Block axel;
	public static Block windvane;
	public static Block sifter;
	public static Block sluice;
	public static Block pressurePackager;
	
	public static Item rawOre;
	public static Item smallDust;
	public static Item largeDust;
	public static Item nuggets;
	
	public static Item diaStudPick;
	public static Item diaStudShovel;
	public static Item diaStudHoe;
	public static Item diaStudAxe;
	
	public static Item oreMinecart;
	
	public static Enchantment enchPulverize;
	public static Enchantment enchCracker;
	public static Enchantment enchProspector;
	
	public static ToolMaterial toolMaterialDiamondStud;
	public static EntityMinecart.Type oreCartEnum;
	
	public static Configuration config;

	public static SimpleNetworkWrapper networkWrapper;

	public static boolean sluiceAllowDirt;


	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		config = new Configuration(event.getSuggestedConfigurationFile());
		CogHelper.addCogModule("HarderVanillaOres.xml");
		CogHelper.addCogModule("HarderExtraOres.xml");
		CogHelper.addCogModule("HarderLimonite.xml");
		CapabilityMechanicalPower.register();
		HardLibAPI.oreMachines = new OreProcessingRecipes();
		
		oreIron = new BlockHardIron();
		EasyRegistry.registerBlockWithCustomItem(oreIron, new ItemOreBlock(oreIron), "ore_hardiron");
		oreGold = new BlockHardGold();
		EasyRegistry.registerBlockWithCustomItem(oreGold, new ItemOreBlock(oreGold), "ore_hardgold");
		oreDiamond = new BlockHardDiamond();
		EasyRegistry.registerBlockWithCustomItem(oreDiamond, new ItemOreBlock(oreDiamond), "ore_harddiamond");
		oreLimonite = new BlockLimonite();
		EasyRegistry.registerBlockWithItem(oreLimonite, "ore_limonite");
		
		dummyOreIron = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOreIron, "dummy_ore_iron");
		dummyOreGold = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOreGold, "dummy_ore_gold");
		dummyOreDiamond = new BlockDummyOre();
		EasyRegistry.registerBlockWithItem(dummyOreDiamond, "dummy_ore_diamond");
		
		millstone = new BlockMillstone();
		EasyRegistry.registerBlockWithItem(millstone, "millstone");
		GameRegistry.registerTileEntity(TileEntityMillstone.class, "millstone");
		axel = new BlockAxel();
		EasyRegistry.registerBlockWithItem(axel, "axel");
		GameRegistry.registerTileEntity(TileEntityAxel.class, "axel");
		windvane = new BlockWindvane();
		EasyRegistry.registerBlockWithItem(windvane, "windvane");
		sifter = new BlockSifter();
		EasyRegistry.registerBlockWithItem(sifter, "sifter");
		GameRegistry.registerTileEntity(TileEntitySifter.class, "sifter");
		sluice = new BlockSluice();
		EasyRegistry.registerBlockWithItem(sluice, "basic_sluice");
		GameRegistry.registerTileEntity(TileEntityBasicSluice.class, "basic_sluice");
		pressurePackager = new BlockPackager();
		EasyRegistry.registerBlockWithItem(pressurePackager, "packager");
		GameRegistry.registerTileEntity(TileEntityPackager.class, "packager");
		
		rawOre = new ItemRawOre();
		EasyRegistry.registerItemWithVariants(rawOre, "orechunks", EnumOreType.IRON);
		smallDust = new ItemDustSmall();
		EasyRegistry.registerItemWithVariants(smallDust, "tinydust", EnumOreType.IRON);
		largeDust = new ItemDustLarge();
		EasyRegistry.registerItemWithVariants(largeDust, "largedust", EnumOreType.IRON);
		nuggets = new ItemNugget();
		EasyRegistry.registerItemWithVariants(nuggets, "nuggets", EnumOreType.IRON);
		
		toolMaterialDiamondStud = EnumHelper.addToolMaterial("DIAMOND_STUD", 3, 750, 7.0F, 2.0F, 5);
		toolMaterialDiamondStud.customCraftingMaterial = rawOre;

		EntityRegistry.registerModEntity(EntityOreMinecart.class, "oreMinecart", 0, this, 80, 3, true);
		
		diaStudPick = new ItemDiamondStudPickaxe(toolMaterialDiamondStud);
		EasyRegistry.registerItem(diaStudPick, "diamondstud_pickaxe");
		diaStudShovel = new ItemDiamondStudShovel(toolMaterialDiamondStud);
		EasyRegistry.registerItem(diaStudShovel, "diamondstud_shovel");
		diaStudHoe = new ItemDiamondStudHoe(toolMaterialDiamondStud);
		EasyRegistry.registerItem(diaStudHoe, "diamondstud_hoe");
		oreMinecart = new ItemEntityOreCart(oreCartEnum);
		EasyRegistry.registerItem(oreMinecart, "orecart");

		EntityEquipmentSlot[] slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND};
		enchPulverize = new EnchantmentPulverize(slots);
		enchPulverize.setRegistryName("pulverize");
		GameRegistry.register(enchPulverize);
		
		enchCracker = new EnchantmentVeinCracker(slots);
		enchCracker.setRegistryName("cracker");
		GameRegistry.register(enchCracker);

		slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.OFFHAND};
		enchProspector = new EnchantmentProspector(slots);
		enchProspector.setRegistryName("prospector");
		GameRegistry.register(enchProspector);
		
		proxy.registerEventHandlers();
		proxy.registerRenderers();
		
        //These have to be unique
        byte serverMessageID = 1;
        byte clientMessageID = 2;
		
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("harderores");
		//networkWrapper.registerMessage(PacketHandlerServer.class, ToServerMessage.class, serverMessageID, Side.SERVER);
		networkWrapper.registerMessage(ClientOreParticleHandler.class, ToClientMessageOreParticles.class, clientMessageID, Side.CLIENT);
		networkWrapper.registerMessage(ServerOreCartHandler.class, ToServerMessageOreCart.class, serverMessageID, Side.SERVER);
		
		FlowerIntegration.registerFlowerGen();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		/*Ore Dict*/
		OreDictionary.registerOre("rawOreChunkLimonite", new ItemStack(rawOre, 1, EnumOreType.LIMONITE.meta));
		OreDictionary.registerOre("rawOreChunkIron", new ItemStack(rawOre, 1, EnumOreType.IRON.meta));
		OreDictionary.registerOre("rawOreChunkGold", new ItemStack(rawOre, 1, EnumOreType.GOLD.meta));
		OreDictionary.registerOre("rawOreChunkDiamond", new ItemStack(rawOre, 1, EnumOreType.DIAMOND.meta));
		
		OreDictionary.registerOre("dustTinyIron", new ItemStack(smallDust, 1, EnumOreType.IRON.meta));
		OreDictionary.registerOre("dustTinyGold", new ItemStack(smallDust, 1, EnumOreType.GOLD.meta));
		OreDictionary.registerOre("dustTinyFlour", new ItemStack(smallDust, 1, EnumOreType.FLOUR.meta));
		OreDictionary.registerOre("dustTinySugar", new ItemStack(smallDust, 1, EnumOreType.SUGAR.meta));
		
		OreDictionary.registerOre("dustIron", new ItemStack(largeDust, 1, EnumOreType.IRON.meta));
		OreDictionary.registerOre("dustGold", new ItemStack(largeDust, 1, EnumOreType.GOLD.meta));
		OreDictionary.registerOre("dustFlour", new ItemStack(largeDust, 1, EnumOreType.FLOUR.meta));

		OreDictionary.registerOre("nuggetIron", new ItemStack(nuggets, 1, EnumOreType.IRON.meta));
		
		OreDictionary.registerOre("oreIron", dummyOreIron);
		OreDictionary.registerOre("oreGold", dummyOreGold);
		OreDictionary.registerOre("oreDiamond", dummyOreDiamond);
		
		/*Milling*/
		HardLibAPI.oreMachines.addMillRecipe(new ItemStack(rawOre,1,EnumOreType.IRON.meta), new ItemStack(smallDust,2,EnumOreType.IRON.meta));
		HardLibAPI.oreMachines.addMillRecipe(new ItemStack(rawOre,1,EnumOreType.GOLD.meta), new ItemStack(smallDust,2,EnumOreType.GOLD.meta));
		
		HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(smallDust, 8, EnumOreType.IRON.meta), new ItemStack(largeDust, 1, EnumOreType.IRON.meta));
		HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(smallDust, 8, EnumOreType.GOLD.meta), new ItemStack(largeDust, 1, EnumOreType.GOLD.meta));
		HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(smallDust, 8, EnumOreType.FLOUR.meta), new ItemStack(largeDust, 1, EnumOreType.FLOUR.meta));
		
		/*Smelting*/
		GameRegistry.addSmelting(new ItemStack(rawOre, 1, EnumOreType.LIMONITE.meta), new ItemStack(rawOre, 1, EnumOreType.IRON.meta), 0.05f);
		GameRegistry.addSmelting(new ItemStack(rawOre, 1, EnumOreType.IRON.meta), new ItemStack(nuggets, 1, EnumOreType.IRON.meta), 0.08f);
		GameRegistry.addSmelting(new ItemStack(rawOre, 1, EnumOreType.GOLD.meta), new ItemStack(Items.GOLD_NUGGET, 1), 0.11f);
		GameRegistry.addSmelting(new ItemStack(smallDust, 1, EnumOreType.IRON.meta), new ItemStack(nuggets, 1, EnumOreType.IRON.meta), 0.08f);
		GameRegistry.addSmelting(new ItemStack(smallDust, 1, EnumOreType.GOLD.meta), new ItemStack(Items.GOLD_NUGGET, 1), 0.11f);
		GameRegistry.addSmelting(new ItemStack(largeDust, 1, EnumOreType.IRON.meta), new ItemStack(Items.IRON_INGOT, 1), 0.7f);
		GameRegistry.addSmelting(new ItemStack(largeDust, 1, EnumOreType.GOLD.meta), new ItemStack(Items.GOLD_INGOT, 1), 1.0f);

		/*Crafting*/
		RecipesUtil.craftNineOf(new ItemStack(smallDust, 1, EnumOreType.IRON.meta), new ItemStack(largeDust, 1, EnumOreType.IRON.meta));
		RecipesUtil.craftNineOf(new ItemStack(smallDust, 1, EnumOreType.GOLD.meta), new ItemStack(largeDust, 1, EnumOreType.GOLD.meta));
		RecipesUtil.craftNineOf(new ItemStack(smallDust, 1, EnumOreType.FLOUR.meta), new ItemStack(largeDust, 1, EnumOreType.FLOUR.meta));
		RecipesUtil.craftNineOf(new ItemStack(smallDust, 1, EnumOreType.DIAMOND.meta), new ItemStack(Items.DIAMOND,1));
		RecipesUtil.craftNineOf(new ItemStack(nuggets, 1, EnumOreType.IRON.meta), new ItemStack(Items.IRON_INGOT, 1));
		GameRegistry.addRecipe(new ItemStack(nuggets, 9, EnumOreType.IRON.meta), "x",'x',new ItemStack(Items.IRON_INGOT, 1));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(millstone,9), 	true, "SSS","SWS","SSS", 'S', "stone", 'W', "logWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(sifter), 		true, "PBP","PbP", 'b', Items.BUCKET, 'P', "plankWood", 'B', Blocks.IRON_BARS));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(windvane, 2), 	true, "SW", "SW", "SW", 'S', "stickWood", 'W', Blocks.WOOL));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(axel, 2), 		true, "WWW", 'W', "logWood"));
		
		ItemStack diamondNugget = new ItemStack(rawOre,1,EnumOreType.DIAMOND.meta);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(diaStudPick), true, "dId", " s ", " s ", 's', "stickWood", 'I', "ingotIron", 'd', diamondNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(diaStudAxe), true, "dI ", "Is ", " s ", 's', "stickWood", 'I', "ingotIron", 'd', diamondNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(diaStudShovel), true, " d ", " I ", " s ", 's', "stickWood", 'I', "ingotIron", 'd', diamondNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(diaStudHoe), true, "dI ", " s ", " s ", 's', "stickWood", 'I', "ingotIron", 'd', diamondNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(sluice), true, "sss","ppp",'s',"stickWood",'p',"slabWood"));
		
		HardLibAPI.oreMachines.addSluiceRecipe(Blocks.GRAVEL);
		HardLibAPI.oreMachines.addSluiceRecipe(Blocks.GRAVEL);
		HardLibAPI.oreMachines.addSluiceRecipe(Blocks.GRAVEL);
		if(config.get("SLUICE", "canFindIron", true).getBoolean()) {
			HardLibAPI.oreMachines.addSluiceRecipe(oreIron);
			HardLibAPI.oreMachines.addSluiceRecipe(oreIron);
			HardLibAPI.oreMachines.addSluiceRecipe(oreIron);
		}
		if(config.get("SLUICE", "canFindGold", true).getBoolean()) {
			HardLibAPI.oreMachines.addSluiceRecipe(oreGold);
			HardLibAPI.oreMachines.addSluiceRecipe(oreGold);
			HardLibAPI.oreMachines.addSluiceRecipe(oreGold);
		}
		if(config.get("SLUICE", "canFindDiamond", false).getBoolean()) {
			HardLibAPI.oreMachines.addSluiceRecipe(oreDiamond);
		}
		if(config.get("SLUICE", "canFindRedstone", true).getBoolean()) {
			HardLibAPI.oreMachines.addSluiceRecipe(Blocks.REDSTONE_ORE);
			HardLibAPI.oreMachines.addSluiceRecipe(Blocks.REDSTONE_ORE);
		}
		
		List<ItemStack> list = new ArrayList<ItemStack>();
		list.add(new ItemStack(Items.BUCKET));
		list.add(new ItemStack(Items.MINECART));
		GameRegistry.addRecipe(new ShapelessRecipes(new ItemStack(oreMinecart), list));
		
		config.addCustomCategoryComment("MILLING", "Enable (hard mode) these to remove vanilla recipes for items and instead require the millstone. In general,\neasy means the millstone doubles resources, while hard is near-vanilla.");
		boolean hardOption = config.getBoolean("RequireMillingFlour", "MILLING", false, "");

		String oreIn = "dustFlour";
		if(hardOption) {
			RecipesUtil.RemoveRecipe(Items.BREAD, 1, 0, "Hard Ores");
			RecipesUtil.RemoveRecipe(Items.COOKIE, 8, 0, "Hard Ores");
			RecipesUtil.RemoveRecipe(Items.CAKE, 1, 0, "Hard Ores");
			
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.BREAD, 3), "www", 'w', oreIn)); //works out to 1:1 vanilla
			//hard: wheat is ground to "4/9th flour"
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.WHEAT), new ItemStack(smallDust, 4, EnumOreType.FLOUR.meta));
			//hard: seeds are ground to "1/9ths flour"
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(smallDust, 1, EnumOreType.FLOUR.meta));
		}
		else {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.BREAD), "www", 'w', oreIn));
			//easy: wheat is ground to "2 flour"
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.WHEAT), new ItemStack(smallDust, 18, EnumOreType.FLOUR.meta));
			//easy: seeds are ground to "2/9ths flour"
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(smallDust, 2, EnumOreType.FLOUR.meta));
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.COOKIE, 8), "wcw", 'w', oreIn, 'c', new ItemStack(Items.DYE, 1, EnumDyeColor.BROWN.getDyeDamage())));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.CAKE), "mmm", "ses", "www", 'w', oreIn, 's', Items.SUGAR, 'e', Items.EGG, 'm', Items.MILK_BUCKET));
		
		hardOption = config.getBoolean("RequireMillingSugar", "MILLING", false, "If enabled, sugarcane cannot be crafted into sugar");
		int sugarMulti = config.getInt("MillingMultiplierSugar", "MILLING", 6, 1, 12, "Sugar is a easy-to-get resource and rare-to-use, so it may be desirable to reduce the production.\nOutput of milling sugar (in tiny piles) is this value in hard-milling and 2x this value in\neasy-milling.\nVanilla Equivalence is 9.");

		if(hardOption) {
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.REEDS), new ItemStack(smallDust, sugarMulti, EnumOreType.SUGAR.meta));
		}
		else {
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.REEDS), new ItemStack(smallDust, 2*sugarMulti, EnumOreType.SUGAR.meta));
		}
		hardOption = config.getBoolean("RequireMillingBonemeal", "MILLING", false, "");
		if(hardOption) {
			RecipesUtil.RemoveRecipe(Items.DYE, 3, EnumDyeColor.WHITE.getDyeDamage(), "Hard Ores");
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.BONE), new ItemStack(Items.DYE, 2, EnumDyeColor.WHITE.getDyeDamage()));
		}
		else {
			HardLibAPI.oreMachines.addMillRecipe(new ItemStack(Items.BONE), new ItemStack(Items.DYE, 4, EnumDyeColor.WHITE.getDyeDamage()));
		}

		ItemStack bonemeal = new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage());
		HardLibAPI.oreMachines.addSiftRecipe(bonemeal, bonemeal, false);
		
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(rawOre, 9, EnumOreType.IRON.meta), new ItemStack(dummyOreIron));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(rawOre, 9, EnumOreType.GOLD.meta), new ItemStack(dummyOreGold));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(rawOre, 9, EnumOreType.DIAMOND.meta), new ItemStack(dummyOreDiamond));
		
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.REDSTONE, 9), new ItemStack(Blocks.REDSTONE_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.WHEAT, 9), new ItemStack(Blocks.HAY_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.SNOWBALL, 9), new ItemStack(Blocks.SNOW));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.SLIME_BALL, 9), new ItemStack(Blocks.SLIME_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.IRON_INGOT, 9), new ItemStack(Blocks.IRON_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.GOLD_INGOT, 9), new ItemStack(Blocks.GOLD_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.DIAMOND, 9), new ItemStack(Blocks.DIAMOND_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.EMERALD, 9), new ItemStack(Blocks.EMERALD_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.CLAY_BALL, 9), new ItemStack(Blocks.CLAY));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.BRICK, 4), new ItemStack(Blocks.BRICK_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.STRING, 4), new ItemStack(Blocks.WOOL));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.QUARTZ, 9), new ItemStack(Blocks.QUARTZ_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Blocks.SAND, 9), new ItemStack(Blocks.SANDSTONE));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Blocks.SAND, 9, BlockSand.EnumType.RED_SAND.getMetadata()), new ItemStack(Blocks.RED_SANDSTONE, 1));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.COAL, 9), new ItemStack(Blocks.COAL_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Blocks.ICE, 9), new ItemStack(Blocks.PACKED_ICE));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Blocks.SNOW, 9), new ItemStack(Blocks.ICE));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.DYE, 9, 4), new ItemStack(Blocks.LAPIS_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.MELON, 9), new ItemStack(Blocks.MELON_BLOCK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.MAGMA_CREAM, 4), new ItemStack(Blocks.field_189877_df));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.NETHERBRICK, 4), new ItemStack(Blocks.NETHER_BRICK));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.PRISMARINE_SHARD, 4), new ItemStack(Blocks.PRISMARINE));
		//Conflicts
		//HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.PRISMARINE_SHARD, 9), new ItemStack(Blocks.PRISMARINE, 1, 1));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.NETHER_WART, 9), new ItemStack(Blocks.field_189878_dg));
		HardLibAPI.oreMachines.addPressurePackRecipe(new ItemStack(Items.DYE, 9, EnumDyeColor.WHITE.getDyeDamage()), new ItemStack(Blocks.field_189880_di));
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new OreGuiHandler());
		
		sluiceAllowDirt = config.getBoolean("sluiceAllowsDirt","SLUICE", false, "Set to true to allow dirt to be used in the sluice.");
		int cycle = config.getInt("sluiceCycleTime", "SLUICE", 2, 1, 20, "Time it takes for the sluice to make 1 operation.  This value is multiplied by 75 ticks.");
		//TileEntitySluice.cycleLength = cycle * 15;
		TileEntityBasicSluice.cycleLength = cycle * 15;
		config.save();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}
}
