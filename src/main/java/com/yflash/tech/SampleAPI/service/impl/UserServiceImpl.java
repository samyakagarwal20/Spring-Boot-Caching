package com.yflash.tech.SampleAPI.service.impl;

import com.yflash.tech.SampleAPI.entity.UserEntity;
import com.yflash.tech.SampleAPI.model.in.PostUserRequest;
import com.yflash.tech.SampleAPI.model.in.PutUserRequest;
import com.yflash.tech.SampleAPI.model.out.User;
import com.yflash.tech.SampleAPI.repository.UserRepository;
import com.yflash.tech.SampleAPI.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    @Override
    @Cacheable(cacheNames = "allUsers")
    public List<UserEntity> getAllUsers() {
        LOGGER.info("Fetching all users info from DB");
        return userRepository.findAll();
    }

    @Override
    @Cacheable(cacheNames = "users", key = "#id")
    public User getUserById(Integer id) {
        LOGGER.info("Fetching user info from DB for id {}", id);
        UserEntity userEntity = userRepository.getReferenceById(id);
        return modelMapper.map(userEntity,User.class);
    }

    @Override
    @CachePut(cacheNames = "users", key = "#result.id")
    @CacheEvict(cacheNames = "allUsers", allEntries = true)
    public User addUserDetails(PostUserRequest userRequest) {
        UserEntity userEntity = modelMapper.map(userRequest,UserEntity.class);
        LOGGER.info("Adding new user details into DB");
        userEntity = userRepository.save(userEntity);
        return modelMapper.map(userEntity, User.class);
    }

    @Override
    @CachePut(cacheNames = "users", key = "#userRequest.id")        // it will either add or update the entry in the cache for specified id
    @CacheEvict(cacheNames = "allUsers", allEntries = true)         // Clear the cache for getAllUsers()
    public User updateUserDetails(PutUserRequest userRequest) {
        LOGGER.info("Updating user info into DB for id {}", userRequest.getId());
        UserEntity userEntity = modelMapper.map(userRequest, UserEntity.class);
        userEntity = userRepository.save(userEntity);
        return modelMapper.map(userEntity, User.class);
    }

    @Override
    @CacheEvict(cacheNames = "users", key = "#id")
    public String deleteUserDetails(Integer id) {
        if(userRepository.existsById(id)) {
            LOGGER.info("Deleting user info from DB for id {}", id);
            userRepository.deleteById(id);
            return id.toString();
        }
        return null;
    }
}
