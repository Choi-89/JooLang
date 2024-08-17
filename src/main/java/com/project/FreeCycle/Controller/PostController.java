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

import java.util.Optional;

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
    public String viewPost(@PathVariable Long id, Model model,HttpSession session) {
        Product product = postService.checkViews(id);
        model.addAttribute("product", product);
        String userId = session.getAttribute("userId").toString();
        if(userId == product.getUser().getUserId()) {
            return "Post/button_post"; // 게시글 수정,삭제 버튼 있는 포스트
        }
        else {
            return "Post/post"; //수정, 삭제, 버튼 없는 포스트
        }
    }

    //글 작성
    @GetMapping(value = "/post/write")
    public String writePage(HttpServletRequest request, HttpSession session) {
        String nickname = postService.findNickname((String)session.getAttribute("nickname")).getNickname();
        if (nickname == null) {
            request.setAttribute("msg", "로그인을 해주세요"); //창
            request.setAttribute("url", "/postlist");
            return "Post/alert";
        }
        return "write-post";
    }

    @PostMapping(value = "/post/write")
    public String writePost(HttpSession session){
        String nickname = postService.findNickname((String)session.getAttribute("nickname")).getNickname();
        postService.postProduct(new Product(), nickname);
        return "redirect:/postlist";
    }

    //글 수정
    @GetMapping(value = "/post/{id}/edit")
    public String editPost(@PathVariable long id, Model model) {
        Product product = postService.getProduct(id).orElse(null);
        model.addAttribute("product", product); //product의 제목 내용 필요
        return "edit-post";
    }

    @PostMapping(value = "/post/{id}/edit")
    public String editPost(@PathVariable long id, String name, String content) {
        postService.postEdit(id , name, content);
        return "redirect:/post/{id}";
        //return "redirect:/post/"+id;
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

    //글 찜버튼
    @PostMapping(value = "/post/{id}/dibs")
    public String dibs(@PathVariable long id, HttpSession session, HttpServletRequest request) {
        String userId = String.valueOf(session.getAttribute("userId"));
        if(userId == null){
            request.setAttribute("msg", "로그인을 해주세요"); //창
            request.setAttribute("url", "/post/"+id+"/dibs");// 로그인창 뜨게 경로설정 해야됨
            return "/Post/alert";
        }
        else {
//            postService.saveDibs(userId, postService.getProduct(id).orElse(null));
            return "redirect:/post/" + id +"/dibs";
        }
    }

}
