package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {

    User register(User user);

    User getUserOne(int id);

    Map<Integer,User> getUsers();

    User update(User user);
}
