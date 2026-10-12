package com.foodiehub.user;

import com.foodiehub.exception.BadRequestException;
import com.foodiehub.exception.ResourceNotFoundException;
import com.foodiehub.user.dto.AddressRequest;
import com.foodiehub.user.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return mapToProfile(user);
    }

    public UserProfileResponse addAddress(String userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (request.isDefault() && user.getAddresses() != null) {
            user.getAddresses().forEach(a -> a.setDefault(false));
        }

        Address address = Address.builder()
                .id(UUID.randomUUID().toString())
                .label(request.getLabel() != null ? request.getLabel() : "Home")
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .contactPhone(request.getContactPhone())
                .isDefault(user.getAddresses().isEmpty() || request.isDefault())
                .build();

        user.getAddresses().add(address);
        user.setUpdatedAt(Instant.now());
        User saved = userRepository.save(user);
        return mapToProfile(saved);
    }

    public UserProfileResponse deleteAddress(String userId, String addressId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.getAddresses().removeIf(a -> a.getId().equals(addressId));
        user.setUpdatedAt(Instant.now());
        User saved = userRepository.save(user);
        return mapToProfile(saved);
    }

    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToProfile)
                .collect(Collectors.toList());
    }

    public UserProfileResponse updateUserStatus(String userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!List.of("ACTIVE", "DEACTIVATED", "SUSPENDED").contains(status.toUpperCase())) {
            throw new BadRequestException("Invalid account status: " + status);
        }

        user.setAccountStatus(status.toUpperCase());
        user.setUpdatedAt(Instant.now());
        return mapToProfile(userRepository.save(user));
    }

    private UserProfileResponse mapToProfile(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .accountStatus(user.getAccountStatus())
                .addresses(user.getAddresses())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
