package vorquel.mod.kudzuclimbers;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends Proxy {
    
    @Override
    public void registerItemModels() {
        ModelLoader.setCustomModelResourceLocation(KudzuClimbers.kudzuClimbers.kudzu, 0, new ModelResourceLocation("Minecraft:vine", "inventory"));
    }
}
