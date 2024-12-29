package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.AttachmentType;
import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.PostDetailDTO;
import com.project.FreeCycle.Dto.ProductDTO;
import com.project.FreeCycle.Dto.ProductFormDTO;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.AttachmentService;
import com.project.FreeCycle.Service.PostService;
import com.project.FreeCycle.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.project.FreeCycle.Domain.AttachmentType.GENERAL;
import static com.project.FreeCycle.Domain.AttachmentType.IMAGE;

import java.net.URLEncoder;

@Controller
@RequiredArgsConstructor
public class PostController_React {

    private final PostService postService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;

    //카테고리 글 목록 나열
    @GetMapping(value = "/postlist/{category}")
    public String categoryList(@PathVariable("category") String category ,Model model,
                               @RequestParam(value = "sort", defaultValue = "latest") String sort){

        List<Product> products = postService.getProducts(category, sort);

        model.addAttribute("products", products);
        return "postlist1";
    }


    //글 조회 >> 삭제버튼 db별로 다르게 출력
    @GetMapping(value = "/post_detail/{id}")
    public String viewPost(@PathVariable("id") Long id, Model model, Principal principal) {
        Product product = postService.checkViews(id);

        String userId = principal.getName();
        User user = userRepository.findByUserId(userId);
        List<String> pictures = attachmentService.getPictures(id);
        String nickname = user.getNickname();   // 프론트에서 nickname이 같으면 수정,삭제 버튼 나오게 사용할 수 있게 model에 추가
        PostDetailDTO postDetailDTO = new PostDetailDTO(product,nickname,pictures);

        model.addAttribute("product", postDetailDTO.getProduct());
        model.addAttribute("nickname", postDetailDTO.getNickname());
        model.addAttribute("pictures",postDetailDTO.getPictures());

        return "post_detail";
    }

    //글 작성
    @GetMapping(value = "/post/write")
    public String writePage() {

        return "write1";
    }

    @PostMapping(value = "/post/write")
    public String writePost(@ModelAttribute ProductFormDTO productFormDTO,
                            Principal principal) throws IOException {
        String userId = principal.getName();
        ProductDTO productDTO = productFormDTO.createProductDTO();
        postService.postProduct(productDTO,userId);
        String encodedCategory = URLEncoder.encode("전체", "UTF-8");
        return "redirect:/postlist/" + encodedCategory;
    }

    //글 수정
    @GetMapping(value = "/post/{id}/edit")
    public String editPost(@PathVariable("id") long id, Model model) {
        Product product = postService.getProduct(id).orElse(null);
        model.addAttribute("product", product); //product의 제목 내용 필요
        return "edit-post";
    }

    @PostMapping(value = "/post/{id}/edit")
    public String editPost(@PathVariable("id") long productid,
//                           @RequestParam(value = "attachmentFiles", required = false) List<MultipartFile> attachmentFiles,
                           @ModelAttribute ProductFormDTO productFormDTO) throws IOException{

        ProductDTO productDTO = productFormDTO.createProductDTO();
        postService.postEdit(productid, productDTO);

        return "redirect:/post_detail/"+productid;
    }

    //글 삭제
    @PostMapping(value ="/post/{id}/delete")
    public String deletePost(@PathVariable("id") long id) {
        postService.postDelete(id);
        return "redirect:/postlist";
    }

    //글 찜버튼
    @PostMapping(value = "/post/{id}/dibs")
    public String dibs(@PathVariable("id") long id, Principal principal){

        String userId = principal.getName();

        postService.saveDibs(userId , id);

        return "redirect:/post_detail/" + id;
    }
}
