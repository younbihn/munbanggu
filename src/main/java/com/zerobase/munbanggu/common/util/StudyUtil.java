package com.zerobase.munbanggu.common.util;


import com.zerobase.munbanggu.study.repository.StudyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyUtil {

    private final StudyMemberRepository studyMemberRepository;

    public boolean existStudyMember(Long studyId, Long userId) {
        return studyMemberRepository.findByStudyId(studyId).stream()
                .anyMatch(studyMember -> studyMember.getUser().getId().equals(userId));
    }

}
