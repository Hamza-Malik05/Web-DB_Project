package com.plant_management.service;

import com.plant_management.model.User;
import com.plant_management.dao.UserDao;
import com.plant_management.dao.EmployeeDao;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.Optional;

@Service
public class UserService {

    private final UserDao userDao;
    private final EmployeeDao employeeDao;

    @Autowired
    public UserService(UserDao userDao, EmployeeDao employeeDao) {
        this.userDao = userDao;
        this.employeeDao = employeeDao;
    }

    public User createUser(User user) {
        return userDao.insert(user);
    }

    public Optional<User> getUserById(Long userId) {
        return userDao.findById(userId);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User updateUser(Long userId, User userDetails) {
        return userDao.findById(userId)
                .map(existingUser -> {
                    existingUser.setUsername(userDetails.getUsername());
                    existingUser.setPassword(userDetails.getPassword());
                    existingUser.setRole(userDetails.getRole());
                    return userDao.save(existingUser);
                })
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found."));
    }

    public void deleteUser(Long userId) {
        userDao.deleteById(userId);
    }

    @Transactional
    public void registerUserFromEmployee(Integer empId, String username, String password) {
        if (!employeeDao.existsById(empId)) {
            throw new IllegalArgumentException("Employee not found");
        }
        userDao.registerUserFromEmployee(empId, username, password);
    }

    @Transactional
    public void makeUserAdmin(Long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(User.Role.admin);  // Make sure this enum matches your Role setup
        userDao.save(user);
    }
    public boolean usernameExists(String username) {
        return userDao.existsByUsername(username);
    }

}
