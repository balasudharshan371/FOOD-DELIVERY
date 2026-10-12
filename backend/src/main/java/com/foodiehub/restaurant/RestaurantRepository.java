package com.foodiehub.restaurant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    List<Restaurant> findByOwnerId(String ownerId);
    Optional<Restaurant> findFirstByOwnerId(String ownerId);
    Page<Restaurant> findByIsActiveTrue(Pageable pageable);

    @Query("{ $and: [ " +
           "  { $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] }, " +
           "  { $or: [ { ?1: null }, { 'cuisineTypes': ?1 } ] }, " +
           "  { $or: [ { ?2: null }, { 'city': { $regex: ?2, $options: 'i' } } ] }, " +
           "  { 'isActive': true } " +
           "] }")
    Page<Restaurant> searchRestaurants(String search, String cuisine, String city, Pageable pageable);

    long countByIsActiveTrue();
}
