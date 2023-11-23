package com.zerobase.munbanggu.study.service;

import com.zerobase.munbanggu.common.exception.NotFoundChecklistException;
import com.zerobase.munbanggu.common.exception.NotFoundStudyException;
import com.zerobase.munbanggu.study.model.entity.Checklist;
import com.zerobase.munbanggu.study.model.entity.Study;
import com.zerobase.munbanggu.study.repository.ChecklistRepository;
import com.zerobase.munbanggu.study.repository.StudyRepository;
import com.zerobase.munbanggu.study.type.AccessType;
import com.zerobase.munbanggu.common.type.ErrorCode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChecklistServiceTest {

  @Autowired
  private ChecklistRepository checklistRepository;
  @Autowired
  private ChecklistService checklistService;
  @Autowired
  private StudyRepository studyRepository;

  Long userId = 1L;
  Long checklistId = 10L;
  Long studyId = 1L;

  public void setUp() {
//    User user = User.builder().email("tmp@naver.com").build();

    // 테스트용 데이터 생성
    Study study1 = Study.builder().title("토익 스터디").build();
    Study study2 = Study.builder().title("알고리즘 스터디").build();
    studyRepository.save(study1);
    studyRepository.save(study2);
    System.out.println("\n>>>>>>>>>>>>>>>>>>>>>");

    Checklist lst1 = Checklist.builder()
            .study(study1)
            .todo("단어 50개")
            .build();
    Checklist lst2 = Checklist.builder()
            .study(study1)
            .todo("수특 10문제")
            .build();
    Checklist lst3 = Checklist.builder()
            .study(study1)
            .todo("오답노트")
            .build();

    Checklist lst4 = Checklist.builder()
            .study(study2)
            .todo("백준 10문제")
            .build();
    Checklist lst5 = Checklist.builder()
            .study(study2)
            .todo("프로그래머스 10문제")
            .build();
    Checklist lst6 = Checklist.builder()
            .study(study2)
            .todo("강의듣기")
            .build();

    checklistRepository.saveAll(Arrays.asList(lst1, lst2, lst3, lst4, lst5, lst6));

    System.out.println("\n------------------------");
    if (studyRepository.findById(studyId).isPresent())
      System.out.println(studyRepository.findById(studyId).get().getUserId());
    else {
      System.out.println("NOT EXISTS");
    }
  }

  @Test
  void createChecklistTest() {
    String title = "Test Checklist";
    AccessType accessType = AccessType.STUDY;

    checklistService.createChecklist(userId, studyId, title, accessType);
  }

  @Test
  void editChecklist() {

    createChecklistTest();
    String newTitle = "TEST22";

    checklistService.editChecklist(userId, checklistId, newTitle);

    Checklist checklist = checklistRepository.findById(checklistId)
            .orElseThrow(() -> new NotFoundChecklistException(ErrorCode.CHECKLIST_NOT_EXIST));
    assert (!checklist.getTodo().equals(newTitle));
  }

  @Test
  void deleteChecklist() {
    createChecklistTest();

    System.out.println("\nPREV " + checklistRepository.findById(checklistId).get().getTodo());
    checklistService.deleteChecklist(userId, checklistId);
    System.out.println("\nAFTER " + checklistRepository.findById(checklistId).isEmpty());
  }

  @Test
  void changeStatus() {
    createChecklistTest();

    Checklist checklist = checklistRepository.findById(checklistId)
            .orElseThrow(() -> new NotFoundStudyException(ErrorCode.CHECKLIST_NOT_EXIST));
    System.out.println("\n>> PREV : " + checklist.isDone());
    checklist.setDone(true);
    checklistRepository.save(checklist);
    System.out.println("\n>> AFTER: " + checklist.isDone());
  }

  @Test
  void findAllMissionsTest() {
    setUp();

    // 참여하고 있는 스터디 아이디
    List<Study> studyIds = Arrays.asList(studyRepository.findById(1L)
                    .orElseThrow(() -> new NotFoundStudyException(ErrorCode.STUDY_NOT_EXIST))
            , studyRepository.findById(2L)
                    .orElseThrow(() -> new NotFoundStudyException(ErrorCode.STUDY_NOT_EXIST)));
    System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    Map<String, List<Checklist>> missions =
            checklistService.findAllMissions(studyIds);

    assert (missions.size() == 2);

    List<Checklist> lst1 = missions.get("토익 스터디");
    System.out.println("\n -------- " + lst1.isEmpty());
  }
}