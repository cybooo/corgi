package cz.wake.corgibot.objects;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.utils.buttons.ButtonRunnable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

public class ButtonGroup {

    private List<Button> buttons;
    private long ownerID;
    private String name;

    public ButtonGroup(long ownerID, String name) {
        this.ownerID = ownerID;
        buttons = new ArrayList<>();
        this.name = name;
    }

    /**
     * Adds a button to the button group.
     *
     * @param btn The button which you would like to add.
     */
    public void addButton(Button btn) {
        // Note I don't check if it already exists... so just don't fuck up :blobthumbsup:
        this.buttons.add(btn);
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public long getOwner() {
        return ownerID;
    }

    public String getName() {
        return name;
    }

    public static class Button {

        private String unicode;
        private long emoteId;
        private ButtonRunnable runnable;
        private Message message;

        public Button(String unicode, ButtonRunnable runnable) {
            this.unicode = unicode;
            this.runnable = runnable;
        }

        public Button(long emoteId, ButtonRunnable runnable) {
            this.emoteId = emoteId;
            this.runnable = runnable;
        }

        public long getEmoteId() {
            return emoteId;
        }

        public String getUnicode() {
            return unicode;
        }

        public void addReaction(Message message) {
            if (!(message.getChannel().getType() == ChannelType.TEXT && message.getGuild().getSelfMember()
                    .hasPermission(message.getTextChannel(), Permission.MESSAGE_HISTORY))) {
                message.getChannel().sendMessage("I can't add buttons due to not having the `Message History` permission!").queue();
                return;
            }

            this.message = message;
            if (unicode != null)
                message.addReaction(unicode).queue();
            else {
                try {
                    message.addReaction(CorgiBot.getJda().getEmoteById(emoteId)).queue();
                } catch (IllegalArgumentException ignored) {}
            }
        }

        public void onClick(long ownerID, User user) {
            runnable.run(ownerID, user, message);
        }
    }
}
