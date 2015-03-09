package ca.panchem.savagederby;

import ca.panchem.savagederby.network.Message;

import java.util.ArrayList;

public class ChatBox {

    private ArrayList<Message> messages;

    public ChatBox() {
        messages = new ArrayList<Message>();
    }

    public void update() {
        for (int i = 0; i < messages.size(); i++) {
            messages.get(i).posY = (messages.size() - i) * 15;
            if(i > 7) {
                messages.remove(i);
            }
        }
    }

    public void addMessage(String message) {
        messages.add(new Message(message));
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }
}
