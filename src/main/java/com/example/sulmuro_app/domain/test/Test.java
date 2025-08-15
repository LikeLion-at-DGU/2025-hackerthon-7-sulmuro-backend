package com.example.sulmuro_app.domain.test;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter // Lombok 어노테이션
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Lombok 어노테이션
@Entity
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;

    @Column(nullable = false)
    private String name;

    @Lob // Large Object의 약자. 이미지/영상 등 대용량 데이터를 저장하는데 사용
    @Column(name = "image_data", columnDefinition="LONGBLOB") // DB 컬럼과 매핑
    private byte[] imageData;

    public Test(String name) {
        this.name = name;
    }

}
