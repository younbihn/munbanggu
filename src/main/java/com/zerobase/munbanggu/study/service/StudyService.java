package com.zerobase.munbanggu.study.service;

import static com.zerobase.munbanggu.type.ErrorCode.STUDY_NOT_EXIST;
import com.zerobase.munbanggu.study.dto.StudyDto;
import com.zerobase.munbanggu.study.exception.StudyException;
import com.zerobase.munbanggu.study.model.entity.Study;
import com.zerobase.munbanggu.study.repository.StudyRepository;
import com.zerobase.munbanggu.user.exception.UserException;
import com.zerobase.munbanggu.type.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor  
public class StudyService {
    private final StudyRepository studyRepository;
    
    public Study getStudy(Long id){
        return studyRepository.findById(id)
            .orElseThrow(() -> new StudyException(ErrorCode.STUDY_NOT_EXISTS));
    }
  
    public Study openStudy(StudyDto studyDto) {
        Study newStudy = convertToEntity(studyDto);
        return studyRepository.save(newStudy);
    }
  
    private Study convertToEntity(StudyDto studyDto) {
        // StudyDto를 Study 엔티티로 변환

        return Study.builder()
                .user_id(studyDto.getUserId())
                .title(studyDto.getTitle())
                .content(studyDto.getContent())
                .min_user(studyDto.getMinUser())
                .max_user(studyDto.getMaxUser())
                .public_or_not(studyDto.isPublicOrNot())
                .password(studyDto.getPassword())
                .start_date(studyDto.getStartDate())
                .end_date(studyDto.getEndDate())
                .start_rule(studyDto.isStartRule())
                .start_attend_or_not(studyDto.isStartAttendOrNot())
                .checklist_cycle(studyDto.getChecklistCycle())
                .fee(studyDto.getFee())
                .refundCycle(studyDto.getRefundCycle())
                .status(studyDto.getStatus())
                .build();
    }

    public Study updateStudy(Long studyId, StudyDto updatedStudyDto) {
        // studyId를 사용하여 기존 스터디 정보를 가져옴
        Study existingStudy = studyRepository.findById(studyId)
                .orElseThrow(() -> new UserException(STUDY_NOT_EXIST));

        // 업데이트된 정보로 기존 스터디 업데이트
        existingStudy.setTitle(updatedStudyDto.getTitle());
        existingStudy.setContent(updatedStudyDto.getContent());
        existingStudy.setMin_user(updatedStudyDto.getMinUser());
        existingStudy.setMax_user(updatedStudyDto.getMaxUser());
        existingStudy.setPublic_or_not(updatedStudyDto.isPublicOrNot());
        existingStudy.setPassword(updatedStudyDto.getPassword());
        existingStudy.setStart_date(updatedStudyDto.getStartDate());
        existingStudy.setEnd_date(updatedStudyDto.getEndDate());
        existingStudy.setStart_rule(updatedStudyDto.isStartRule());
        existingStudy.setStart_attend_or_not(updatedStudyDto.isStartAttendOrNot());
        existingStudy.setChecklist_cycle(updatedStudyDto.getChecklistCycle());
        existingStudy.setFee(updatedStudyDto.getFee());
        existingStudy.setRefundCycle(updatedStudyDto.getRefundCycle());

        // 기존 스터디 저장
        return studyRepository.save(existingStudy);
    }

    public void deleteStudy(Long id) {
        Study study = studyRepository.findById(id)
                .orElseThrow(() -> new UserException(STUDY_NOT_EXIST));

        studyRepository.delete(study);
    }

    public List<Study> searchStudiesByKeyword(String content) {
        // 키워드가 제목에 포함된 스터디들을 조회
        return studyRepository.findAllByContent(content);
    }
    public List<Study> getAllOpenStudies() {
        return studyRepository.findAll();
    }

    public Study getStudyDetails(Long id) {
        return studyRepository.findById(id).orElse(null);
    }
  
    /**
     * 사용자가 참여하고있는 스터디의 ID 목록을 조회
     * @param userId 사용자ID
     * @return 참여하고 있는 스터디ID 목록
     */
    public List<Study> findStudiesByUserId(Long userId) {
      return studyRepository.findStudyIdByUserId(userId);
    }
}
