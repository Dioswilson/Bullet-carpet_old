package carpet.commands;


import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import static net.minecraft.server.MinecraftServer.prometheusExtension;

public class CommandPrometheusPort extends CommandCarpetBase {

    /**
     * Gets the name of the command
     */
    @Override
    public String getName() {
        return "port";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/port <number>(number has to be between 2 and 65535)";
    }

    /**
     * Return the required permiss on level for this command.
     */
    public int getRequiredPermissionLevel() {
        return 2;
    }

    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), this.getName());
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (checkPermission(server, sender)) {
            if (args.length == 1) {
                if (!(args[0].length() > 5)) {
                    int port;
                    try {
                        port = Integer.parseInt(args[0]);
                        if (port > 1 && port <= 65535) {
                            if (!(port == 123)) {
                                prometheusExtension.changePorts(port);
                                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Port succesfully changed to " + port));
                            }
                            else {
                                sender.sendMessage(new TextComponentString(TextFormatting.RED + "123 can't be a port, idk why, might be more of this"));
                            }
                        }
                        else {
                            sender.sendMessage(new TextComponentString(TextFormatting.RED + "That is not a valid port"));
                        }
                    } catch (NumberFormatException numberFormatException) {
                        throw new SyntaxErrorException("That is not a number", new Object[0]);
                    }
                }

            }
            else {
                throw new WrongUsageException(getUsage(sender), new Object[0]);
            }
        }
    }
}

