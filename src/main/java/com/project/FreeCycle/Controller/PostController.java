package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        return "home";
    }

    //글 작성
    @GetMapping(value = "/post/write")
    public String writePage() {
        return "write-post";
    }

    @PostMapping(value = "/post/write")
    public String writePost(Product product, HttpServletRequest request, HttpSession session){
        String nickname = postService.findNickname((String)session.getAttribute("nickname"));
        if (nickname == null) {
            request.setAttribute("msg", "중복되는 닉네임이 있습니다. 다른 닉네임을 사용해주세요."); //창
            request.setAttribute("url", "/post/write");
            return "Post/alert";
        }
        postService.postProduct(product, nickname);
        return "redirect:/";
    }



}
