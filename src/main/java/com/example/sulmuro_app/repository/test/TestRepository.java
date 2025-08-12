package com.example.sulmuro_app.repository.test;


import com.example.sulmuro_app.domain.test.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository  extends JpaRepository<Test, Long> {
}
