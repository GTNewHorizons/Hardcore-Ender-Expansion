package chylex.hee.block;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ObjectIntIdentityMap;

import com.gtnewhorizon.gtnhlib.reflect.Fields;

import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.util.ReflectionUtils;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;

public class BlockReplaceHelper {

    public static void replaceBlock(Block toReplace, Block replacement) {
        Stopwatch.time("BlockReplace");

        Class<?>[] classTest = new Class<?>[4];
        Exception exception = null;

        try {
            final Fields.ClassFields blocksFields = Fields.ofClass(Blocks.class);
            for (Field blockField : Blocks.class.getFields()) {
                if (Block.class.isAssignableFrom(blockField.getType())) {
                    Block block = (Block) blockField.get(null);

                    if (block == toReplace) {
                        final Fields.ClassFields.Field fieldAccessor = blocksFields
                                .getUntypedField(Fields.LookupType.PUBLIC, blockField.getName());
                        String registryName = Block.blockRegistry.getNameForObject(block);
                        int id = Block.getIdFromBlock(block);

                        Log.debug("Replacing block - $0/$1", id, registryName);

                        ReflectionUtils.setFieldValue(
                                ((ItemBlock) Item.getItemFromBlock(block)),
                                "field_150939_a",
                                replacement);

                        FMLControlledNamespacedRegistry<Block> registryBlocks = GameData.getBlockRegistry();
                        Log.debug("Got BlockRegistry.");

                        Map registryObjects = ReflectionUtils.getFieldValue(registryBlocks, "registryObjects");
                        Log.debug("Got registryObjects.");
                        ObjectIntIdentityMap underlyingIntegerMap = ReflectionUtils
                                .getFieldValue(registryBlocks, "underlyingIntegerMap");
                        Log.debug("Got underlyingIntegerMap.");
                        registryObjects.put(registryName, replacement);
                        Log.debug(
                                "Put " + replacement.getUnlocalizedName()
                                        + " in registryObjects with key: "
                                        + registryName);
                        underlyingIntegerMap.func_148746_a(replacement, id); // OBFUSCATED put object
                        Log.debug("OBFUSCATED put object - underlyingIntegerMap");
                        ReflectionUtils.setFieldValue(registryBlocks, "registryObjects", registryObjects);
                        Log.debug("Set new value for registryObjects: registryObjects. For object registryBlocks.");
                        ReflectionUtils.setFieldValue(registryBlocks, "underlyingIntegerMap", underlyingIntegerMap);
                        Log.debug(
                                "Set new value for underlyingIntegerMap: underlyingIntegerMap. For object registryBlocks.");

                        Log.debug("Made " + blockField.getName() + " accessible.");
                        fieldAccessor.setValue(null, replacement);
                        Log.debug("Set " + blockField.getName() + " to " + replacement.getUnlocalizedName() + ".");

                        Method delegateNameMethod = replacement.delegate.getClass()
                                .getDeclaredMethod("setName", String.class);
                        delegateNameMethod.setAccessible(true);
                        Log.debug("Made " + delegateNameMethod.getName() + " accessible.");
                        delegateNameMethod.invoke(replacement.delegate, toReplace.delegate.name());
                        Log.debug("Invoked " + delegateNameMethod.getName() + ".");

                        classTest[0] = blockField.get(null).getClass();
                        classTest[1] = Block.blockRegistry.getObjectById(id).getClass();
                        classTest[2] = ((ItemBlock) Item.getItemFromBlock(replacement)).field_150939_a.getClass();
                    }
                }
            }
        } catch (Exception e) {
            exception = e;
        }

        Stopwatch.finish("BlockReplace");

        Log.debug("Check field: $0", classTest[0]);
        Log.debug("Check block registry: $0", classTest[1]);
        Log.debug("Check item: $0", classTest[2]);

        if (classTest[0] != classTest[1] || classTest[0] != classTest[2] || classTest[0] == null) {
            throw new RuntimeException(
                    "HardcoreEnderExpansion was unable to replace block " + toReplace.getUnlocalizedName()
                            + "! Debug info to report: "
                            + classTest[0]
                            + ","
                            + classTest[1]
                            + ","
                            + classTest[2],
                    exception);
        }
    }
}
