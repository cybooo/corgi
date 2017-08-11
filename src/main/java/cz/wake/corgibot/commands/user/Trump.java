package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Arrays;

public class Trump implements ICommand {


    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if(args.length < 1){
            channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle("**Použítí příkazu .trump**")
                    .setDescription("**.trump [text]** - Vygenerování vlastního Trump příkazu").build()).queue();
        } else {
            try (InputStream is = CorgiBot.class.getClassLoader().getResourceAsStream("trump.jpg")) {
                BufferedImage image = ImageIO.read(is);

                Graphics2D g2 = image.createGraphics();

                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                String query = StringUtils.join(Arrays.asList(args), " ");

                if (query.length() > 153) {
                    query = query.substring(0, 150) + "...";
                }


                if (query.isEmpty()) {
                    query = "Try putting in some text into the arguments, ie. \"_trump Pepe\"";
                }

                double fontSize = 65.0 / (0.05 * query.length() + 1.0) + 20;

                Font font = new Font("Times New Roman", Font.PLAIN, (int) fontSize);

                AffineTransform tx = new AffineTransform();
                tx.rotate(Math.toRadians(3));
                tx.shear(0, Math.toRadians(5.5));
                font = font.deriveFont(tx);

                g2.setFont(font);
                g2.setColor(Color.BLACK);

                //String lines = WordUtils.wrap(, 25, null, true);

                float drawPosX = 380;
                float drawPosY = 230;

                AttributedString string = new AttributedString(query);
                string.addAttribute(TextAttribute.FONT, font);

                AttributedCharacterIterator paragraph = string.getIterator();
                int paragraphStart = paragraph.getBeginIndex();
                int paragraphEnd = paragraph.getEndIndex();
                int breakWidth = 230;
                LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, g2.getFontRenderContext());
                lineMeasurer.setPosition(paragraphStart);

                while (lineMeasurer.getPosition() < paragraphEnd) {
                    TextLayout layout = lineMeasurer.nextLayout(breakWidth);

                    drawPosY += layout.getAscent();

                    layout.draw(g2, drawPosX, drawPosY);

                    drawPosY += layout.getDescent() + layout.getLeading();
                    drawPosX -= 1;
                }

                File file = new File("saved.png");

                ImageIO.write(image, "jpg", file);

                g2.dispose();

                channel.sendFile(file, null).queue();

                if (!file.delete()) {
                    file.deleteOnExit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getCommand() {
        return "trump";
    }

    @Override
    public String getDescription() {
        return "Generování obrázků s Trumpem.";
    }

    @Override
    public String getHelp() {
        return ".trump <text>";
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public CommandUse getUse() {
        return CommandUse.GUILD;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}
