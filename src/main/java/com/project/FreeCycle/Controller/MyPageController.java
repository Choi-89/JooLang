package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.ApiResponseDTO;
import com.project.FreeCycle.Dto.MyInformDTO;
import com.project.FreeCycle.Service.PostService;
import com.project.FreeCycle.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final UserService userService;
    private final PostService postService;

    //마이페이지
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 조회 성공"),
            @ApiResponse(responseCode = "400", description = "게시물 조회 실패")
    })
    @Operation(summary = "마이페이지",
            description = "마이페이지({id}/mypage)로 이동합니다. 이때 아이디는 userId입니다."
    )
    @GetMapping(value = "/{id}/mypage")
    public ResponseEntity<ApiResponseDTO<String>> myPage(Principal principal) {
        return ResponseEntity.ok(new ApiResponseDTO<>("200", "success", "myPage"));

    }


    //내가 쓴 게시글 목록
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "마이페이지 게시글 리스트 출력 성공"),
            @ApiResponse(responseCode = "400", description = "마이페이지 게시글 리스트 출력 실패")
    })
    @Operation(summary = "마이페이지 내 게시글 리스트",
            description = "사용자가 작성한 게시글 목록을 출력하며, 게시글 클릭시 /post_detail/{id} 로 넘어갑니다. id는 productid"
    )
    @GetMapping("/{id}/mypost")
    public ResponseEntity<ApiResponseDTO<Map<String,List<Product>>>> myPost(Principal principal){

        List<Product> products = userService.getUserPosts(principal.getName());
        Collections.reverse(products); //글 최신순으로 정렬
        Map<String,List<Product>> response = new HashMap<>();
        response.put("products",products);

        return ResponseEntity.ok(new ApiResponseDTO<>("200", "success", response));
    }

    //내 찜목록
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내 찜목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "내 찜목록 조회 실패")
    })
    @Operation(summary = "내 찜목록 페이지",
            description = "내 찜목록 페이지(/{id}/mydibs)로 이동합니다. 이때 아이디는 userId입니다."
    )
    @GetMapping("/{id}/mydibs")
    public ResponseEntity<ApiResponseDTO<Map<String, List<Product>>>> mydibs(Principal principal){

        List<Product> products = postService.getDibsPosts(principal.getName());
        Collections.reverse(products); //최신순으로 정렬

        Map<String, List<Product>> response = new HashMap<>();
        response.put("products", products);
        return ResponseEntity.ok(new ApiResponseDTO<>("200", "success", response));
    }

    //개인정보 수정
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "개인정보 수정 페이지 이동 성공"),
            @ApiResponse(responseCode = "400", description = "개인정보 수정 페이지 이동 실패")
    })
    @Operation(summary = "개인정보 수정 페이지 이동 버튼",
            description = "사용자의 User정보를 가지고 /{id}/modify 로 이동합니다. id는 Userid."
    )
    @GetMapping("/{id}/modify")
    public ResponseEntity<ApiResponseDTO<Map<String,User>>> modify( Principal principal){
        User user = userService.getUser(principal.getName());

        Map<String,User> response = new HashMap<>();
        response.put("user",user);
        return ResponseEntity.ok(new ApiResponseDTO<>("200", "success", response));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "개인정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "개인정보 수정 실패")
    })
    @Operation(summary = "개인정보 수정",
            description = "수정한 개인정보를 저장하고 /{id}/mypage로 넘어갑니다. 이때 id는 userId입니다.")
    @PostMapping("/{id}/myinformodify")
    public ResponseEntity<ApiResponseDTO<Map<String, MyInformDTO>>> moodify(Principal principal,
                          @ModelAttribute MyInformDTO myInformDTO)
    {
        String userId = principal.getName();
        userService.userEdit(userId, myInformDTO);

        Map<String, MyInformDTO> response = new HashMap<>();
        response.put("myInformDTO",myInformDTO);

        return ResponseEntity.ok(new ApiResponseDTO<>("200", "success", response));
    }

}
