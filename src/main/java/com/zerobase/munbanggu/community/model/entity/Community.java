package com.zerobase.munbanggu.community.model.entity;

import com.zerobase.munbanggu.community.Converter.CommunityCategoryConverter;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.type.CommunityCategoty;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "community")
public class Community {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String title;
    private String content;
    private String photoImgurl;
    @Enumerated(EnumType.STRING)
    @Convert(converter = CommunityCategoryConverter.class)
    private CommunityCategoty communityCategoty;
    @Builder.Default
    private Long view = 0L;

    @CreatedDate
    private LocalDateTime created_date;

    @LastModifiedDate
    private LocalDateTime modified_date;

    @ElementCollection
    @CollectionTable(name = "community_hashtags", joinColumns = @JoinColumn(name = "community_id"))
    @Column(name = "hashtag")
    private Set<String> hashtags = new HashSet<>();
}