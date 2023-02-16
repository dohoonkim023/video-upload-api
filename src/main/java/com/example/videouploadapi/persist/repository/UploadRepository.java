package com.example.videouploadapi.persist.repository;

import com.example.videouploadapi.persist.entity.Upload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadRepository extends JpaRepository<Upload, Long> {

}
