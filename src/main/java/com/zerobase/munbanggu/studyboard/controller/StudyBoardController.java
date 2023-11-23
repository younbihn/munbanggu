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

    @DeleteMapping("/{study_id}/post/{post_id}")
    public ResponseEntity<String> delete(@PathVariable("study_id") Long studyId, @PathVariable("post_id") Long postId,
            @RequestHeader(value = AUTHORIZATION_HEADER) String authHeader) {
        String token = tokenProvider.getRawToken(authHeader);

        studyBoardService.delete(studyId, postId, token);
        return ResponseEntity.ok().body("삭제되었습니다.");

    }

    @GetMapping("/{study_id}/post")
    public ResponseEntity<PageResponse<PostResponse>> search(@PathVariable("study_id") Long studyId,
            @RequestParam(value = "keyword") String keyword, @PageableDefault() Pageable pageable,
            @RequestHeader(value = AUTHORIZATION_HEADER) String authHeader) {
        String token = tokenProvider.getRawToken(authHeader);

        return ResponseEntity.ok().body(PageResponse.from(studyBoardService.search(studyId, keyword, pageable, token)));

    }

}
