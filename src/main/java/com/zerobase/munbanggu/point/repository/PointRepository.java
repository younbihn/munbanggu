package com.zerobase.munbanggu.point.repository;

import com.zerobase.munbanggu.point.model.Point;
import com.zerobase.munbanggu.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface PointRepository extends JpaRepository<Point,Long> {
  @Query("SELECT p FROM Point p ORDER BY p.createdDate DESC")
  Point findTopByUserByCreatedDateDesc(User user);
}
