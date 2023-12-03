package com.zerobase.munbanggu.user.repository;

import com.zerobase.munbanggu.study.model.entity.Study;
import com.zerobase.munbanggu.user.model.entity.StudyUser;
import com.zerobase.munbanggu.user.model.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyUserRepository extends JpaRepository<StudyUser,Long> {
  StudyUser findByUserAndStudy(User user, Study study);
  List<StudyUser> findByUser(User user);  //사용자가 참여하고 있는 스터디 반환
  List<StudyUser> findByStudy(Study study);  // 스터디에 참여하고 있는 사용자 반환

}
