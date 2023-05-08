package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

@Qualifier
public interface UserStorage {

    User register(User user);

    User getUserOne(int id);

    Collection<User> getUsers();

    User update(User user);

    User addFriend(int id, int friendId);

    User deleteFriend(int id, int friendId);

    Collection<User> getFriends(int id);

    List<User> getGeneralFriends(int id, int friendId);

    void deleteUser(int userId);

    Collection<Film> getRecommendation(int id);
}