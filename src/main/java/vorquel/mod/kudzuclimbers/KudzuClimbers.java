package vorquel.mod.kudzuclimbers;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = "KudzuClimbers", name = "Kudzu Climbers", version = "@VERSION@")
public class KudzuClimbers {
    
    @Mod.Instance
    public static KudzuClimbers kudzuClimbers;
    
    @SidedProxy(serverSide = "vorquel.mod.kudzuclimbers.Proxy", clientSide = "vorquel.mod.kudzuclimbers.ClientProxy")
    public static Proxy proxy;
    
    public ItemKudzu kudzu = new ItemKudzu();
    public int tickDelay;
    public int maxLength;
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        tickDelay = config.getInt("Tick_Delay", "general", 10, 1, 20, "How long to wait between kudzu growth cycles");
        maxLength = config.getInt("Max_Length", "general", 64, 1, 99, "How many growth cycles kudzu should last");
        if(config.hasChanged())
            config.save();
        GameRegistry.registerItem(kudzu, "kudzu");
        proxy.registerItemModels();
        GameRegistry.addShapedRecipe(new ItemStack(kudzu), "XXX", "XOX", "XXX", 'X', new ItemStack(Items.dye, 1, 15), 'O', Blocks.vine);
    }
}
