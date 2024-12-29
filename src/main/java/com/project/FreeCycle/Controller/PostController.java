package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.ApiResponseDTO;
import com.project.FreeCycle.Dto.PostDetailDTO;
import com.project.FreeCycle.Dto.ProductDTO;
import com.project.FreeCycle.Dto.ProductFormDTO;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.AttachmentService;
import com.project.FreeCycle.Service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;

    //카테고리 글 목록 나열
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 리스트 불러오기 성공"),
            @ApiResponse(responseCode = "400", description = "게시물 리스트 불러오기 실패")
    })
    @Operation(summary = "게시물 리스트",
            description = "전체 게시물에 대한 리스트를 반환합니다." + " 게시글을 클릭하면 /post_detail/{id}로 이동하며, id는 productid입니다."
    )
    @GetMapping(value = "/postlist/{category}")
    public ResponseEntity<ApiResponseDTO<Map<String,List<Product>>>> categoryList(@PathVariable("category") String category,
                                                       @RequestParam(value = "sort", defaultValue = "latest") String sort){

        List<Product> products = postService.getProducts(category, sort);

        Map<String, List<Product>> response = new HashMap<>();
        response.put("products", products);

        return ResponseEntity.ok(new ApiResponseDTO<>("200", "success", response));
    }

    //글 조회 >> 삭제버튼 db별로 다르게 출력
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 조회 성공"),
            @ApiResponse(responseCode = "400", description = "게시물 조회 실패")
    })
    @Operation(summary = "게시물 조회",
            description = "해당 게시물에 대한 내용을 출력합니다." + 
                    " 게시물의 내용은 PostDetailDTO 에 담아 출력하며," +
                    " PostDetailDTO에는 게시물의 내용은 Product객체로 , 작성자의 정보와 이미지는 각각 String 타입으로 담깁니다." +
                    " 찜버튼은 누르면 /post/{id}/dibs로 이동하며 id는 productid입니다." +
                    " 해당 게시글의 작성자가 조회할 시 수정 버튼과 삭제 버튼이 존재합니다." +
                    " 수정버튼을 누르면 /post/{id}/edit 로 이동하고, 삭제버튼을 누르면 /post/{id}/delete로 이동합니다. id는 productid입니다."
    )
    @GetMapping(value = "/post_detail/{id}")
    public ResponseEntity<ApiResponseDTO<Map<String,PostDetailDTO>>> viewPost(@PathVariable("id") Long id, Principal principal) {
        Product product = postService.checkViews(id);

        String userId = principal.getName();
        User user = userRepository.findByUserId(userId);
        List<String> pictures = attachmentService.getPictures(id);
        String nickname = user.getNickname();   // 프론트에서 nickname이 같으면 수정,삭제 버튼 나오게 사용할 수 있게 model에 추가
        PostDetailDTO postDetailDTO = new PostDetailDTO(product,nickname,pictures);
        Map<String,PostDetailDTO> response = new HashMap<>();
        response.put("post",postDetailDTO);

        return ResponseEntity.ok(new ApiResponseDTO<>("200","success", response));
    }

    //글 작성
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작성창 진입 성공"),
            @ApiResponse(responseCode = "400", description = "작성창 진입 실패")
    })
    @Operation(summary = "글 작성",
            description = "글 작성 페이지로 이동합니다."
    )
    @GetMapping(value = "/post/write")
    public ResponseEntity<ApiResponseDTO<Void>> writePage() {
        ApiResponseDTO<Void> response = new ApiResponseDTO<>("success", "글 작성 페이지로 이동", null);
        return ResponseEntity.ok(response);
    }

    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "작성 성공"),
            @ApiResponse(responseCode = "400", description = "작성 실패")
    })
    @Operation(summary = "글 작성 확인 버튼",
            description = "사용자가 글 작성 버튼을 클릭했을 때 정보를 저장하고 해당 게시글(")
    @PostMapping(value = "/post/write")
    public ResponseEntity<ApiResponseDTO<Map<String, ProductDTO>>> writePost(@ModelAttribute ProductFormDTO productFormDTO,
                            Principal principal) throws IOException {
        String userId = principal.getName();
        ProductDTO productDTO = productFormDTO.createProductDTO();
        postService.postProduct(productDTO,userId);

        Map<String, ProductDTO> response = new HashMap<>();
        response.put("productDTO", productDTO);
        return ResponseEntity.ok(new ApiResponseDTO<>("200", "success", response));
    }

    //글 수정
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "수정 실패")
    })
    @Operation(summary = "글 수정 버튼",
            description = "사용자가 글 수정 버튼을 클릭했을 때 해당 게시글 정보를 가지고 /post/{id}/edit 으로 이동합니다. id는 productid.")
    @GetMapping(value = "/post/{id}/edit")
    public ResponseEntity<ApiResponseDTO<Map<String,Product>>> editPost(@PathVariable("id") long id) {
        Product product = postService.getProduct(id).orElse(null);
        Map<String, Product> response = new HashMap<>();
        response.put("product", product);
        return ResponseEntity.ok(new ApiResponseDTO<>("200","success", response));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "글 수정 실패")
    })
    @Operation(summary = "글 수정에서 확인 버튼",
            description = "사용자가 글 수정에서 확인 버튼을 클릭했을 때 바뀐 정보를 저장하고 /post_detail/{id}로 이동합니다. 이때 id는 productid 입니다.")
    @PostMapping(value = "/post/{id}/edit")
    public ResponseEntity<ApiResponseDTO<Map<String, ProductDTO>>> editPost(@PathVariable("id") long productid,
                           @ModelAttribute ProductFormDTO productFormDTO) throws IOException{

        ProductDTO productDTO = productFormDTO.createProductDTO();
        postService.postEdit(productid, productDTO);
        Map<String, ProductDTO> response = new HashMap<>();
        response.put("productDTO", productDTO);
        return ResponseEntity.ok(new ApiResponseDTO<>("200", "success", response));
    }

    //글 삭제
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "글 삭제 실패")
    })
    @Operation(summary = "글에서 삭제버튼",
            description = "글쓴이가 글에서 삭제 버튼을 눌렀을 때 글을 삭제하고 postlist/전체 로 이동합니다.")
    @PostMapping(value ="/post/{id}/delete")
    public ResponseEntity<ApiResponseDTO<String>> deletePost(@PathVariable("id") long id) {
        postService.postDelete(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("200", "success", "delete success"));
    }

    //글 찜버튼
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글 찜 성공"),
            @ApiResponse(responseCode = "400", description = "글 찜 실패")
    })
    @Operation(summary = "글 찜 버튼",
            description = "사용자가 글 찜버튼을 누르면 글을 찜하고 이동하지 않음")
    @PostMapping(value = "/post/{id}/dibs")
    public ResponseEntity<ApiResponseDTO<String>> dibs(@PathVariable("id") long id, Principal principal){

        String userId = principal.getName();

        postService.saveDibs(userId , id);
        String response = "찜 완료";
        return ResponseEntity.ok(new ApiResponseDTO<>("200", "success", response));
    }
}
