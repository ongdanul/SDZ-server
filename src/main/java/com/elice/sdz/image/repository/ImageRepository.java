package com.elice.sdz.image.repository;

import com.elice.sdz.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}