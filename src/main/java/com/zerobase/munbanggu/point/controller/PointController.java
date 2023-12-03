package com.zerobase.munbanggu.point.controller;

import com.zerobase.munbanggu.common.exception.NotFoundStudyException;
import com.zerobase.munbanggu.common.exception.NotFoundUserException;
import com.zerobase.munbanggu.common.type.ErrorCode;
import com.zerobase.munbanggu.point.service.PointService;
import com.zerobase.munbanggu.study.model.entity.Study;
import com.zerobase.munbanggu.study.repository.StudyRepository;
import com.zerobase.munbanggu.user.model.entity.User;
import com.zerobase.munbanggu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/point")
@RequiredArgsConstructor
public class PointController {
    private final PointService pointService;
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
//    // 카카오페이결제 요청
//    @GetMapping("/order/pay")
//    public @ResponseBody ReadyResponse payReady(@RequestParam(name = "total_amount") int totalAmount, Order order, Model model) {
//
//        log.info("주문정보:"+order);
//        log.info("주문가격:"+totalAmount);
//        // 카카오 결제 준비하기	- 결제요청 service 실행.
//        ReadyResponse readyResponse = kakaopayService.payReady(totalAmount);
//        // 요청처리후 받아온 결재고유 번호(tid)를 모델에 저장
//        model.addAttribute("tid", readyResponse.getTid());
//        log.info("결재고유 번호: " + readyResponse.getTid());
//        // Order정보를 모델에 저장
//        model.addAttribute("order",order);
//
//        return readyResponse; // 클라이언트에 보냄.(tid,next_redirect_pc_url이 담겨있음.)
//    }
//
//    // 결제승인요청
//    @GetMapping("/order/pay/completed")
//    public String payCompleted(@RequestParam("pg_token") String pgToken, @ModelAttribute("tid") String tid, @ModelAttribute("order") Order order,  Model model) {
//
//        log.info("결제승인 요청을 인증하는 토큰: " + pgToken);
//        log.info("주문정보: " + order);
//        log.info("결재고유 번호: " + tid);
//
//        // 카카오 결재 요청하기
//        ApproveResponse approveResponse = kakaopayService.payApprove(tid, pgToken);
//
//        // 5. payment 저장
//        //	orderNo, payMathod, 주문명.
//        // - 카카오 페이로 넘겨받은 결재정보값을 저장.
//        Payment payment = Payment.builder()
//                .paymentClassName(approveResponse.getItem_name())
//                .payMathod(approveResponse.getPayment_method_type())
//                .payCode(tid)
//                .build();
//
//        orderService.saveOrder(order,payment);
//
//        return "redirect:/orders";
//    }
//    // 결제 취소시 실행 url
//    @GetMapping("/order/pay/cancel")
//    public String payCancel() {
//        return "redirect:/carts";
//    }
//
//    // 결제 실패시 실행 url
//    @GetMapping("/order/pay/fail")
//    public String payFail() {
//        return "redirect:/carts";
//    }

  /**
   * 환급 기능
   * @param studyId 스터디 아이디
   * @param userId 유저 아이디
   * @return HttpStatus.OK
   */
    @PostMapping("/study/{study_id}/user/{user_id}")
    public ResponseEntity<?> refund(
        @PathVariable("study_id")Long studyId,@PathVariable("user_id")Long userId){

      User user = userRepository.findById(userId)
          .orElseThrow(() -> new NotFoundUserException(ErrorCode.USER_NOT_EXIST));
      Study study = studyRepository.findById(studyId)
          .orElseThrow(() -> new NotFoundStudyException(ErrorCode.STUDY_NOT_EXIST));

      pointService.getUserRefund(user, study);
      return ResponseEntity.ok().body("환급이 완료되었습니다");
    }
}
