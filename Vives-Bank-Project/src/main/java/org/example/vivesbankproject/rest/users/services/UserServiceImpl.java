package org.example.vivesbankproject.rest.users.services;

import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.users.dto.UserRequest;
import org.example.vivesbankproject.rest.users.dto.UserResponse;
import org.example.vivesbankproject.rest.users.exceptions.UserExists;
import org.example.vivesbankproject.rest.users.exceptions.UserNotFoundById;
import org.example.vivesbankproject.rest.users.exceptions.UserNotFoundByUsername;
import org.example.vivesbankproject.rest.users.exceptions.UserNotFoundException;
import org.example.vivesbankproject.rest.users.mappers.UserMapper;
import org.example.vivesbankproject.rest.users.models.Role;
import org.example.vivesbankproject.rest.users.models.User;
import org.example.vivesbankproject.rest.users.repositories.UserRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Primary
@CacheConfig(cacheNames = {"usuario"})
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Page<UserResponse> getAll(Optional<String> username, Optional<String> roles, Pageable pageable) {
        log.info("Obteniendo todos los usuarios");
        Specification<User> specUsername = (root, query, criteriaBuilder) ->
                username.map(u -> criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + u.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> specRole = (root, query, criteriaBuilder) ->
                roles.map(r -> {
                    try {
                        return criteriaBuilder.isMember(Role.valueOf(r.toUpperCase()), root.get("roles"));
                    } catch (IllegalArgumentException e) {
                        return criteriaBuilder.isFalse(criteriaBuilder.literal(true));
                    }
                }).orElse(null);

        Specification<User> criterio = Specification.where(specUsername).and(specRole);

        Page<User> userPage = userRepository.findAll(criterio, pageable);

        return userPage.map(userMapper::toUserResponse);
    }

    @Override
    @Cacheable
    public UserResponse getById(String id) {
        log.info("Obteniendo usuarios por id: {}", id);
        var user = userRepository.findByGuid(id).orElseThrow(() -> new UserNotFoundById(id));
        return userMapper.toUserResponse(user);
    }

    @Override
    @Cacheable
    public UserResponse getByUsername(String username) {
        log.info("Obteniendo usuarios por username: {}", username);
        var user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundByUsername(username));
        return userMapper.toUserResponse(user);
    }

    @Override
    @CachePut
    public UserResponse save(UserRequest userRequest) {
        log.info("Guardando usuario");
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new UserExists(userRequest.getUsername());
        }
        var user = userRepository.save(userMapper.toUser(userRequest));
        return userMapper.toUserResponse(user);
    }

    @Override
    @CachePut
    public UserResponse update(String id, UserRequest userRequest) {
        log.info("Actualizando usuario con id: {}", id);
        var user = userRepository.findByGuid(id).orElseThrow(
                () -> new UserNotFoundById(id)
        );
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new UserExists(userRequest.getUsername());
        }
        var userUpdated = userRepository.save(userMapper.toUserUpdate(userRequest, user));
        return userMapper.toUserResponse(userUpdated);
    }

    @Override
    @CacheEvict
    public void deleteById(String id) {
        log.info("Borrando usuario con id: {}", id);
        var user = userRepository.findByGuid(id).orElseThrow(
                () -> new UserNotFoundById(id)
        );
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username)  {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException( username ));
    }
}