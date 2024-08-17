package com.project.FreeCycle.Controller;


import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Service.PostService;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PostController {

    //게시글 목록
    @GetMapping("/postList")
    public String postList(@RequestParam(value = "page", defaultValue = "0") int page, Model model){
        ;PageRequest pageable = PageRequest.of(page, 30, Sort.by("id").descending());
//        Page<Product> postPage = PostService.getPosts(pageable);
//        model.addAttribute("posts", postPage);
        return "post";
    }


}
