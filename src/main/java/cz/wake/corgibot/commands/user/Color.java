package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@CommandInfo(
        name = "color",
        description = "Get color by code",
        help = "%color [HEX-CODE] - Get color",
        category = CommandCategory.FUN
)
public class Color implements CommandBase {

    /**
     * @param red   - red (0 - 255)
     * @param green - green (0 - 255)
     * @param blue  - blue (0 - 255)
     * @return CMYK-color converted from RGB
     */
    public static String rgbToCmyk(int red, int green, int blue) {
        float r = red / 255f;
        float g = green / 255f;
        float b = blue / 255f;
        float k = 1.0f - Math.max(r, Math.max(g, b));
        float c = (1f - r - k) / (1f - k);
        float m = (1f - g - k) / (1f - k);
        float y = (1f - b - k) / (1f - k);
        float[] cmykArray = {c * 100, m * 100, y * 100, k * 100};
        return Math.round(cmykArray[0]) + "%, " + Math.round(cmykArray[1]) + "%, " + Math.round(cmykArray[2]) + "%, " + Math.round(cmykArray[3]) + "%";
    }

    /**
     * Sends the embed message, in which the quality of the attachment is a specified color image
     *
     * @param c       - color
     * @param channel - MessageReceivedEvent
     * @param builder - embed, which adds an image
     */
    public static void colorCommand(java.awt.Color c, MessageChannel channel, EmbedBuilder builder) {
        int width = 150;
        int height = 150;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.setColor(c);
        g.fillRect(0, 0, width, height);
        g.dispose();
        File file = new File(Integer.toHexString(c.getRGB()).substring(2).toLowerCase() + ".png");
        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MessageBuilder msgBuilder = new MessageBuilder();
        builder.setImage("attachment://" + Integer.toHexString(c.getRGB()).substring(2).toLowerCase() + ".png");
        msgBuilder.setEmbeds(builder.build());
        channel.sendMessage(msgBuilder.build()).addFile(file).queue();
        Executors.newSingleThreadScheduledExecutor().schedule((Runnable) file::delete, 7500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GRAY).setTitle("Color command help").setDescription(getHelp().replace("%", gw.getPrefix())).build()).queue();
        } else {
            try {
                String color = args[0];
                if (Pattern.compile("#?([A-Fa-f\\d]){6}").matcher(color).find()) {
                    EmbedBuilder builder = new EmbedBuilder();
                    String HEX = color.startsWith("#") ? color : "#" + color;
                    String RGB = java.awt.Color.decode(HEX).getRed() + ", " + java.awt.Color.decode(HEX).getGreen() + ", " + java.awt.Color.decode(HEX).getBlue();
                    int DEC = Integer.parseInt(HEX.replace("#", ""), 16);
                    String CMYK = rgbToCmyk(java.awt.Color.decode(HEX).getRed(), java.awt.Color.decode(HEX).getGreen(), java.awt.Color.decode(HEX).getBlue());
                    builder.setColor(java.awt.Color.decode(HEX));
                    builder.setDescription("**HEX**: " + HEX.toLowerCase() + "\n**RGB**: " + RGB + "\n**DEC**: " + DEC + "\n**CMYK**: " + CMYK);
                    builder.setAuthor(HEX + ":");
                    colorCommand(java.awt.Color.decode(HEX), channel, builder);
                } else {
                    MessageUtils.sendErrorMessage("Incorrectly entered command! Example: `%color #B0171F`".replace("%", gw.getPrefix()), channel);
                }
            } catch (NumberFormatException e) {
                MessageUtils.sendErrorMessage("Incorrectly entered command! Example: %color #B0171F`".replace("%", gw.getPrefix()), channel);
            }
        }
    }
}
