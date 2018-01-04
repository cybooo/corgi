package cz.wake.corgibot.managers;

import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;

public class UserManagement {

    private static HashMap<User, CorgiUser> list = new HashMap<>();

    public HashMap<User, CorgiUser> getList() {
        return list;
    }

    public void addToList(User u, CorgiUser cu){
        list.put(u, cu);
    }

    public void removeFromList(CorgiUser cu){
        list.remove(cu);
    }

    public CorgiUser getCorgiUser(User user){
        return list.get(user);
    }


    
}
