package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static ru.yandex.practicum.filmorate.storage.InMemoryUserStorage.users;

@Slf4j
@Service
public class UserService {

    public void addFriend(int id, int friendId) {
        if (users.containsKey(id) && users.containsKey(friendId)) {
            users.get(id).getFriends().add(friendId);
            users.get(friendId).getFriends().add(id);

        } else {
            log.error("Человека с таким Id нет");
            throw new NullPointerException("Такого id нет");
        }
    }

    public void deleteFriend(int id, int friendId) {
        if (users.containsKey(id) && users.containsKey(friendId)) {
            Set<Integer> friends = users.get(id).getFriends();
            friends.remove(friendId);
            Set<Integer> friendsFriend = users.get(friendId).getFriends();
            friendsFriend.remove(id);
        } else {
            log.error("Человека с таким Id нет");
            throw new NullPointerException("Такого id нет");
        }
    }

    public Collection<User> getFriends(int id) {
        if (users.containsKey(id)) {
            List<User> allFriends = new ArrayList<>();
            Set<Integer> friendsId = users.get(id).getFriends();

            for (int i : friendsId) {
                allFriends.add(users.get(i));
            }
            return allFriends;

        } else {
            log.error("Человека с таким Id нет");
            throw new NullPointerException();
        }
    }
    public List<User> getGeneralFriends(int id,int friendId){
        if (users.containsKey(id) && users.containsKey(friendId)) {
            Set<Integer> friends = users.get(id).getFriends();
            Set<Integer> friendsFriend = users.get(friendId).getFriends();
            friends.retainAll(friendsFriend);
            List<User> result = new ArrayList<>();
            for(int i : friends){
                result.add(users.get(i));
            }
            return result;
        } else {
            log.error("Человека с таким Id нет");
            throw new NullPointerException();
        }

    }

}

