package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.Location;
import com.project.FreeCycle.Domain.Product;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.LocationRepository;
import com.project.FreeCycle.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    //마이페이지
    @GetMapping(value = "/{id}/mypage")
    public String myPage(Principal principal) {
        return "mypage";

    }

    //내가 쓴 게시글 목록
    @GetMapping("/{id}/mypost")
    public String myPost(Principal principal, Model model){

        List<Product> products = userService.getUserPosts(principal.getName());
        Collections.reverse(products); //글 최신순으로 정렬

        // 모델에 products 리스트를 추가
        model.addAttribute("products", products);
        return "postlist";
    }

    //내 찜목록
    @GetMapping("/{id}/mydibs")
    public String mydibs(Principal principal, Model model){

        List<Product> products = userService.getDibsPosts(principal.getName());
//        List<Product> products = userService.getDibs(principal.getName()).get().getDibs();
        Collections.reverse(products); //최신순으로 정렬

        // 모델에 dibs 리스트 삽입
        model.addAttribute("products", products);
        return "postlist";
    }

    //개인정보 수정
    @GetMapping("/{id}/modify")
    public String modify( Principal principal, Model model){
        User user = userService.getUser(principal.getName());
        //getName하면 userId를 가져옴
        System.out.println(user.getNickname());
        model.addAttribute("user" , user);
        return "modify";
    }


    @PostMapping("/{id}/myinformodify")
    public String moodify(Principal principal,
                          @RequestParam("nickname") String nickname,
                          @RequestParam("postcode") String postcode,
                          @RequestParam("address") String address,
                          @RequestParam("detail_address") String detailAddress)
    {
        String userId = principal.getName();
//        Location location = new Location();
//        location.setPostcode(postcode);
//        location.setAddress(address);
//        location.setDetailAddress(detailAddress);

        userService.userEdit(userId, nickname, postcode, address, detailAddress);

        return "redirect:/{id}/mypage";
    }


}
