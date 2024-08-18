package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.PostService;
import com.project.FreeCycle.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController_React {

    private final PostService postService;
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping(value = "/postlist")
    public String postList(Model model) {
        // Product 리스트를 가져오는 로직 (예: 서비스 레이어에서 가져오기)
        List<Product> products = postService.getAllProducts(); // productService는 Product를 관리하는 서비스 클래스

        // 모델에 products 리스트를 추가
        model.addAttribute("products", products);

        // postlist 템플릿으로 이동
        return "postlist";
    }

    //글 조회 >> 삭제버튼 db별로 다르게 출력
    @GetMapping(value = "/post_detail/{id}")
    public String viewPost(@PathVariable Long id, Model model, Principal principal) {
        Product product = postService.checkViews(id);

        String userId = principal.getName();
        User user = userRepository.findByUserId(userId);
        String nickname = user.getNickname();   // 프론트에서 nickname이 같으면 수정,삭제 버튼 나오게 사용할 수 있게 model에 추가
        model.addAttribute("product", product);
        model.addAttribute("nickname", nickname);

        return "post_detail";
    }

    //글 작성
    @GetMapping(value = "/post/write")
    public String writePage() {

        return "write";
    }

    @PostMapping(value = "/post/write")
    public String writePost(@ModelAttribute Product product, Principal principal){
        String userID = principal.getName();
        // getName은 userID 가져옴.
        User user = userRepository.findByUserId(userID);
        String nickname = user.getNickname();

        // 게시물 작성자 설정
        product.setUser(user);

        postService.postProduct(product , nickname);

        return "redirect:/postlist";
    }

    //글 수정
    @GetMapping(value = "/post/{id}/edit")
    public String editPost(@PathVariable("id") long id, Model model) {
        Product product = postService.getProduct(id).orElse(null);
        model.addAttribute("product", product); //product의 제목 내용 필요
        return "edit-post";
    }

    @PostMapping(value = "/post/{id}/edit")
    public String editPost(@PathVariable("id") long id, String name, String content) {
        postService.postEdit(id , name, content);
//        return ResponseEntity.ok("edit complete");
        return "redirect:/post_detail/"+id;
    }

    //글 삭제
    @PostMapping(value ="/post/{id}/delete")
    public String deletePost(@PathVariable("id") long id) {
//        if(postService.postDelete(id)) {
//            request.setAttribute("msg", "삭제되었습니다."); //창
//            request.setAttribute("url", "/post/write");
//            return ResponseEntity.ok().body("Post/alert"); // 삭제 완!
//        }
//        else{
//            request.setAttribute("msg", "존재하지 않습니다."); //창
//            request.setAttribute("url", "/post/list");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post/alert"); // 이미 삭제된 게시글!
//        }
        postService.postDelete(id);
        return "redirect:/postlist";
    }



}
