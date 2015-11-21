package vorquel.mod.kudzuclimbers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraft.block.BlockVine.*;
import static net.minecraft.init.Blocks.vine;
import static vorquel.mod.kudzuclimbers.KudzuClimbers.kudzuClimbers;

public class ItemKudzu extends Item {
    
    public ItemKudzu() {
        super();
        setUnlocalizedName("kudzu");
        setCreativeTab(CreativeTabs.tabTools);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
    
    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return 0x00ff00;
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(worldIn.isRemote)
            return true;
        if(side.getAxis() == EnumFacing.Axis.Y)
            return false;
        if(canVineAttach(worldIn, pos)) {
            BlockPos placePos = pos.add(side.getDirectionVec());
            FMLCommonHandler.instance().bus().register(new Climber(worldIn, placePos, placeVine(worldIn, placePos)));
            if(!playerIn.capabilities.isCreativeMode)
                --stack.stackSize;
            return true;
        }
        return false;
    }
    
    private static boolean canVineAttach(World worldIn, BlockPos pos) {
        Block block = worldIn.getBlockState(pos).getBlock();
        return block.isNormalCube(worldIn, pos) && !block.isPassable(worldIn, pos);
    }
    
    public static IBlockState placeVine(World world, BlockPos pos) {
        boolean any, n, s, e, w;
        any  = n = canVineAttach(world, pos.north());
        any |= s = canVineAttach(world, pos.south());
        any |= e = canVineAttach(world, pos.east());
        any |= w = canVineAttach(world, pos.west());
        IBlockState state = vine.getBlockState().getBaseState()
                                    .withProperty(NORTH, n).withProperty(SOUTH, s)
                                    .withProperty(EAST, e).withProperty(WEST, w);
        if(any) {
            world.setBlockState(pos, state);
            world.playSoundEffect(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
                    vine.stepSound.getPlaceSound(), 1, world.rand.nextFloat() * 0.1F + 0.9F);
        }
        return state;
    }
    
    public static class Climber {
        
        private World world;
        private BlockPos pos;
        private int counter = 0;
        private IBlockState state;
        
        public Climber(World world, BlockPos pos, IBlockState state) {
            this.world = world;
            this.pos = pos;
            this.state = state;
        }
        
        @SubscribeEvent
        public void onTick(TickEvent.WorldTickEvent event) {
            if(event.phase == TickEvent.Phase.END || ++counter % kudzuClimbers.tickDelay != 0)
                return;
            pos = pos.up();
            if(!(pos.getY() >= world.getHeight()) && world.isAirBlock(pos)
                       && !(counter > kudzuClimbers.maxLength * kudzuClimbers.tickDelay)
                       && (Boolean.valueOf(true) == state.getValue(NORTH) && canVineAttach(world, pos.north())
                       || Boolean.valueOf(true) == state.getValue(SOUTH) && canVineAttach(world, pos.south())
                       || Boolean.valueOf(true) == state.getValue(EAST) && canVineAttach(world, pos.east())
                       || Boolean.valueOf(true) == state.getValue(WEST) && canVineAttach(world, pos.west())))
                state = placeVine(world, pos);
            else
                FMLCommonHandler.instance().bus().unregister(this);
        }
    }
}
