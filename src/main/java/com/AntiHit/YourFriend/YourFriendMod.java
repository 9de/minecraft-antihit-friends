package com.AntiHit.YourFriend;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Keyboard;
import java.io.*;
import java.util.*;

@Mod(modid = "antihitfriend", name = "AntiHit Friend System", version = "1.0", clientSideOnly = true)
public class YourFriendMod {
    
    private static final List<String> friends = new ArrayList<>();
    private static KeyBinding toggleBinding;
    private static boolean enabled = true;
    private File configFile;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        configFile = new File(event.getModConfigurationDirectory(), "antihitfriend.txt");
        loadFriends();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new FriendCommand());
        
        toggleBinding = new KeyBinding("Toggle Friend System", Keyboard.KEY_R, "AntiHit Friend System");
        ClientRegistry.registerKeyBinding(toggleBinding);
    }
    
    private void loadFriends() {
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
                return;
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    friends.add(line.toLowerCase().trim());
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveFriends() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
            for (String friend : friends) {
                writer.write(friend);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SideOnly(Side.CLIENT)
    public void onAttack(AttackEntityEvent event) {
        if (!enabled) return;
        
        if (event.target instanceof EntityPlayer) {
            EntityPlayer target = (EntityPlayer) event.target;
            System.out.println(target.getName());
            if (friends.contains(target.getName().toLowerCase())) {
                event.setCanceled(true);
                event.entityPlayer.addChatMessage(new ChatComponentText(
                    EnumChatFormatting.RED + "You can't hit your friend!"));
            }
        }
    }
    
    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (toggleBinding.isPressed()) {
            enabled = !enabled;
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(
                EnumChatFormatting.GOLD + "AntiHit Friend System: " + 
                (enabled ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled")));
        }
    }
    
    public class FriendCommand extends CommandBase {
        
        @Override
        public String getCommandName() {
            return "ahf";
        }
        
        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/ahf friend <add/remove/list> [username]";
        }
        
        @Override
        public List<String> getCommandAliases() {
            return Collections.emptyList();
        }
        
        @Override
        public void processCommand(ICommandSender sender, String[] args) throws CommandException {
            if (args.length >= 2 && args[0].equals("friend")) {
                if (args[1].equals("add") && args.length == 3) {
                    String friendName = args[2].toLowerCase();
                    if (!friends.contains(friendName)) {
                        friends.add(friendName);
                        saveFriends();
                        sender.addChatMessage(new ChatComponentText(
                            EnumChatFormatting.GREEN + "Added " + args[2] + " as friend!"));
                    } else {
                        sender.addChatMessage(new ChatComponentText(
                            EnumChatFormatting.RED + args[2] + " is already your friend!"));
                    }
                }
                else if (args[1].equals("remove") && args.length == 3) {
                    String friendName = args[2].toLowerCase();
                    if (friends.remove(friendName)) {
                        saveFriends();
                        sender.addChatMessage(new ChatComponentText(
                            EnumChatFormatting.GREEN + "Removed " + args[2] + " from friends!"));
                    } else {
                        sender.addChatMessage(new ChatComponentText(
                            EnumChatFormatting.RED + args[2] + " is not in your friends list!"));
                    }
                }
                else if (args[1].equals("list")) {
                    if (friends.isEmpty()) {
                        sender.addChatMessage(new ChatComponentText(
                            EnumChatFormatting.YELLOW + "You have no friends added."));
                    } else {
                        sender.addChatMessage(new ChatComponentText(
                            EnumChatFormatting.GREEN + "Your friends: " + String.join(", ", friends)));
                    }
                }
                else if (args[1].equals("toggle")) {
                    enabled = !enabled;
                    sender.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.GOLD + "AntiHit Friend System: " + 
                        (enabled ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled")));
                }
            }
        }
        
        @Override
        public boolean canCommandSenderUseCommand(ICommandSender sender) {
            return true;
        }
        
        @Override
        public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
            if (args.length == 2 && args[0].equals("friend")) {
                return Arrays.asList("add", "remove", "list", "toggle");
            }
            return null;
        }
        
        @Override
        public boolean isUsernameIndex(String[] args, int index) {
            return args.length > 1 && args[1].equals("add") && index == 2;
        }
        
        @Override
        public int getRequiredPermissionLevel() {
            return 0;
        }
    }
}