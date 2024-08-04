package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //게시판
    @GetMapping(value = "/postlist")
    public String home(@RequestParam(value="page") int page, Model model) {
        PageRequest pageable = PageRequest.of(page, 15, Sort.by("id").descending());
        Page<Product> postPage = postService.getPosts(pageable);
        model.addAttribute("posts", postPage);
        return "Post/list";
    }

    //글 조회 >> 삭제버튼 db별로 다르게 출력
    @GetMapping(value = "post/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Product product = postService.checkViews(id);
        model.addAttribute("product", product);
        return "Post/post";
    }

    //글 작성
    @GetMapping(value = "/post/write")
    public String writePage() {
        return "write-post";
    }

    @PostMapping(value = "/post/write")
    public String writePost(Product product, HttpServletRequest request, HttpSession session){
        String nickname = postService.findNickname((String)session.getAttribute("nickname")).getNickname();
        if (nickname == null) {
            request.setAttribute("msg", "중복되는 닉네임이 있습니다. 다른 닉네임을 사용해주세요."); //창
            request.setAttribute("url", "/post/write");
            return "Post/alert";
        }
        postService.postProduct(product, nickname);
        return "redirect:/";
    }

    //글 수정
    @GetMapping(value = "/post/{id}/edit")
    public String editPost(@PathVariable long id, Model model) {
        Product product = postService.getProduct(id).orElse(null);
        model.addAttribute("product", product);
        return "edit-post";
    }

    @PostMapping(value = "/post/{id}/edit")
    public String editPost(@PathVariable long id, String name, String content) {
        postService.postEdit(id , name, content);
        return "redirect:/post/{id}";
    }

    //글 삭제
    @PostMapping(value ="/post/{id}/delete")
    public ResponseEntity<String> deletePost(@PathVariable long id, HttpServletRequest request) {
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
        return postService.postDelete(id);
    }



}
