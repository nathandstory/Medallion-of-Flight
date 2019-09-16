//--MEDALLION-OF-FLIGHT--------------------------------------------------------------------------------------------------
//This plugin handles the creation, crafting, and events associated with the medallion of flight
//author: doopaderp
//-----------------------------------------------------------------------------------------------------------------------
package me.doopaderp.flightmedallion;

//imports
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.ChatColor;

//main class, starts with onEnable and onDisable
public class Main extends JavaPlugin implements Listener{
    @Override
    public void onEnable() {
        //register the medallion item override and recipe
        registerMedallion();
        //start up block place listener for this plugin
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    @Override
    public void onDisable() {
    
    }

    //Create a console sender object for logging
    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
    //create a global key for the flight medallion namespace
    NamespacedKey key = new NamespacedKey(this, "flight_medallion");
    

    //events are handled here (BlockPlaceEvent)
    //disable placing the "medallion" considering its a flower
    @EventHandler
    public void onPlace(BlockPlaceEvent e)
    {
        //Get the meta of the item placed
        ItemMeta itemMeta = e.getItemInHand().getItemMeta();
        //Get the persistant data container
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        //Test if the item the player tried to place was a Medallion of Flight, cancel "planting" if so
        if(container.has(key, PersistentDataType.STRING)) {
            String foundValue = container.get(key, PersistentDataType.STRING);
            if (foundValue.equals("fmIdent")) {e.setCancelled(true);}
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e)
    {
        Player player = (Player) e.getPlayer();
        if(player.getInventory().contains(getMedallion())){
            player.setAllowFlight(true);
            return;
        }
        if(!player.getInventory().contains(getMedallion()) && !(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)){
            player.setAllowFlight(false);
            return;        
        }
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("giveflightmedallion")) {
            if (sender.hasPermission("flightmedallion.give")) {
                //Get the player
                Player player = (Player) sender;
                //get the medallion
                ItemStack fmItem = getMedallion();
                //give it to the player
                player.getInventory().addItem(fmItem);
                return true;
            }
            sender.sendMessage(ChatColor.DARK_RED + "Sorry, but you don't have permission for this command.");
            return true;
        }
        return false;
    }

    public ItemStack getMedallion() {
        //Get the sunflower so we can change it
        ItemStack fmItem = new ItemStack(Material.SUNFLOWER);
        ItemMeta fmMeta = fmItem.getItemMeta();
        //Change the sunflower into the "Medallion of Flight"
        fmMeta.setDisplayName("§aMedallion of Flight");
        fmMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "fmIdent");
        fmItem.setItemMeta(fmMeta);
    //return the medallion to any function that needs it
    return fmItem;
    }

    public void registerMedallion() {
        //create new shaped recipe for the medallion
        ShapedRecipe fmRecipe = new ShapedRecipe(key, getMedallion());
        //Specify the recipe
        fmRecipe.shape("DND","NEN","DND");
        fmRecipe.setIngredient('D', Material.DIAMOND_BLOCK);
        fmRecipe.setIngredient('N', Material.NETHER_STAR);
        fmRecipe.setIngredient('E', Material.ELYTRA);
        //Add the recipe to the game
        this.getServer().addRecipe(fmRecipe);
    }
}

