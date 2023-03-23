package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User register(User user);

    Collection<User> getUsers();

    User update(User user);
}
