package com.zerobase.munbanggu.studyboard.controller;

import static com.zerobase.munbanggu.common.type.ErrorCode.INVALID_REQUEST_BODY;

import com.zerobase.munbanggu.auth.TokenProvider;
import com.zerobase.munbanggu.common.dto.PageResponse;
import com.zerobase.munbanggu.common.exception.InvalidRequestBodyException;
import com.zerobase.munbanggu.studyboard.model.dto.PostRequest;
import com.zerobase.munbanggu.studyboard.model.dto.PostResponse;
import com.zerobase.munbanggu.studyboard.service.StudyBoardService;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class StudyBoardController {

    private final StudyBoardService studyBoardService;
    private final TokenProvider tokenProvider;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * 게시글 생성 - type이 `VOTE`인 경우에만 `vote` 보냄
     * @param id studyId
     * @param request
     * {
     *   "type": NOTICE/GENERAL/VOTE 중 하나
     *   "title": String
     *   "userId": Long
     *   "content": String
     *   "vote": {
     *     "title": String
     *     "options": [
     *       {
     *         "optionText": String
     *       },
     *       {
     *         "optionText": String
     *       },
     *     ],
     *     "endDate": LocalDateTime
     *   }
     * }
     * @param result
     * {
     *     id: Long,
     *     title: String,
     *     userId: Long,
     *     nickname: String,
     *     createdDate: LocalDateTime
     * }
     * @param authHeader Bearer 방식 JWT
     * @return
     */
    @PostMapping("/{study_id}/post")
    public ResponseEntity<PostResponse> create(@PathVariable("study_id") Long id,
            @Valid @RequestBody PostRequest request, BindingResult result,
            @RequestHeader(value = AUTHORIZATION_HEADER) String authHeader) {
        if (result.hasErrors()) {
            Map<String, String> errorMap = buildErrorMap(result);
            throw new InvalidRequestBodyException(INVALID_REQUEST_BODY, errorMap);
        }

        String token = tokenProvider.getRawToken(authHeader);
        return ResponseEntity.ok().body(studyBoardService.create(request, id, token));
    }

    private static Map<String, String> buildErrorMap(BindingResult result) {
        Map<String, String> errorMap = new HashMap<>();
        for (FieldError fieldError : result.getFieldErrors()) {
            String fieldName = fieldError.getField();
            String[] fieldPath = fieldName.split("\\.");
            String simpleFieldName = fieldPath.length > 1 ? fieldPath[1] : fieldPath[0];
            errorMap.put(simpleFieldName, fieldError.getDefaultMessage());
        }
        return errorMap;
    }

    /**
     *
     * @param studyId 스터디 아이디
     * @param postId 게시글 아이디
     * @param request
     * {
     *   "type": NOTICE/GENERAL/VOTE 중 하나
     *   "title": String
     *   "userId": Long
     *   "content": String
     *   "vote": {
     *     "title": String
     *     "options": [
     *       {
     *         "optionText": String
     *       },
     *       {
     *         "optionText": String
     *       },
     *     ],
     *     "endDate": LocalDateTime
     *   }
     * }
     * @param result
     * {
     *     id: Long,
     *     title: String,
     *     userId: Long,
     *     nickname: String,
     *     createdDate: LocalDateTime
     * }
     * @param authHeader Bearer 방식 JWT
     * @return
     */
    @PutMapping("/{study_id}/post/{post_id}")
    public ResponseEntity<PostResponse> update(@PathVariable("study_id") Long studyId, @PathVariable("post_id") Long postId,
            @Valid @RequestBody PostRequest request, BindingResult result,
            @RequestHeader(value = AUTHORIZATION_HEADER) String authHeader) {

        if (result.hasErrors()) {
            Map<String, String> errorMap = buildErrorMap(result);
            throw new InvalidRequestBodyException(INVALID_REQUEST_BODY, errorMap);
        }

        String token = tokenProvider.getRawToken(authHeader);
        return ResponseEntity.ok().body(studyBoardService.update(request, studyId, postId, token));
    }

    /**
     * 게시글 삭제
     * @param studyId 스터디 아이디
     * @param postId 게시글 아이디
     * @param authHeader Bearer 방식 JWT
     * @return String
     */
    @DeleteMapping("/{study_id}/post/{post_id}")
    public ResponseEntity<String> delete(@PathVariable("study_id") Long studyId, @PathVariable("post_id") Long postId,
            @RequestHeader(value = AUTHORIZATION_HEADER) String authHeader) {
        String token = tokenProvider.getRawToken(authHeader);

        studyBoardService.delete(studyId, postId, token);
        return ResponseEntity.ok().body("삭제되었습니다.");

    }

    /**
     * 스터디 게시판 게시글 조회
     * @param studyId 스터디 아이디
     * @param keyword 검색어
     * @param pageable 기본 size 20
     * @param authHeader Bearer 방식 JWT
     * @return
     * {
     * content: [
     *  {
     *      id: Long,
     *      title: string,
     *      userId: Long,
     *      nickname: String,
     *      createdDate: LocalDateTime
     *  }
     *  ],
     *  pageNumber: int, 현재 페이지
     *  pageSize: int, 반환된 게시글 수(limit)
     *  totalPages: int 전체 페이지
     *  first: boolean 현재 페이지가 첫번째 페이지이면 true
     *  last: boolean 현재 페이지가 마지막 페이지이면 true
     *  empty: boolean 게시글 없으면 true
     *  }
     */
    @GetMapping("/{study_id}/post")
    public ResponseEntity<PageResponse<PostResponse>> search(@PathVariable("study_id") Long studyId,
            @RequestParam(value = "keyword") String keyword, @PageableDefault(size = 20) Pageable pageable,
            @RequestHeader(value = AUTHORIZATION_HEADER) String authHeader) {
        String token = tokenProvider.getRawToken(authHeader);

        return ResponseEntity.ok().body(PageResponse.from(studyBoardService.search(studyId, keyword, pageable, token)));

    }

}
