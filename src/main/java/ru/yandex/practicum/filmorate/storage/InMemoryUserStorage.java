package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    public static final Map<Integer, User> users = new HashMap<>();

    private int id = 1;

    @Override
    public User register(User user) {
        //тут идет проверка на то, что логин не содержит пробелы
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getId() == null) {
            user.setId(id++);
        }
        users.put(user.getId(), user);
        log.debug("Пользователь с логином {} добавлен", user.getLogin());

        return user;
    }

    @Override
    public User getUserOne(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            log.error("Пользователя с таким {} нет", id);
            throw new EntityNotFoundException("Такого id нет");
        }
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User update(@RequestBody @Valid User user) {
        if (users.containsKey(user.getId())) {
            if (user.getName().isBlank() || user.getName() == null) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
        } else {
            log.error("Пользователя с id {} нет", user.getId());
            throw new EntityNotFoundException("Пользователя с таким id нет");
        }
        return user;
    }

    @Override
    public User addFriend(int id, int friendId) {
        if (users.containsKey(id) && users.containsKey(friendId)) {
            users.get(id).getFriends().add(friendId);
            users.get(friendId).getFriends().add(id);
            return users.get(id);
        } else {
            log.error("Человека с таким Id нет");
            throw new NullPointerException("Такого id нет");
        }
    }

    @Override
    public User deleteFriend(int id, int friendId) {
        if (users.containsKey(id) && users.containsKey(friendId)) {
            Set<Integer> friends = users.get(id).getFriends();
            friends.remove(friendId);
            Set<Integer> friendsFriend = users.get(friendId).getFriends();
            friendsFriend.remove(id);
            return users.get(id);
        } else {
            log.error("Человека с таким Id нет");
            throw new NullPointerException("Такого id нет");
        }
    }

    @Override
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

    @Override
    public List<User> getGeneralFriends(int id, int friendId) {
        if (users.containsKey(id) && users.containsKey(friendId)) {
            Set<Integer> friends = new HashSet<>(users.get(id).getFriends());
            Set<Integer> friendsFriend = users.get(friendId).getFriends();
            friends.retainAll(friendsFriend);
            List<User> result = new ArrayList<>();
            for (int i : friends) {
                result.add(users.get(i));
            }
            return result;
        } else {
            log.error("Человека с таким Id нет");
            throw new NullPointerException();
        }

    }

    @Override
    public void deleteUser(int userId) {
    }

    public Collection<Film> getRecommendation(int id) {
        return null;
    }
}