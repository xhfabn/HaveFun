package com.wujiawei.member.controller;


import com.wujiawei.common.resp.CommonResp;
import com.wujiawei.member.req.MemberLoginReq;
import com.wujiawei.member.req.MemberRegisterReq;
import com.wujiawei.member.resp.MemberLoginResp;
import com.wujiawei.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Resource
    private MemberService memberService;

    @GetMapping("/count")
    public String count(){
        return Integer.toString(memberService.count());
    }

    @PostMapping("/register")
    public CommonResp<Long> register(@Valid MemberRegisterReq registerReq){
        long register = memberService.register(registerReq);
        return new CommonResp<Long>(register);
    }


    @PostMapping("/login")
    public CommonResp<MemberLoginResp> sendcode(@Valid @RequestBody MemberLoginReq memberLoginReq){
        MemberLoginResp login = memberService.login(memberLoginReq);
        return new CommonResp<>(login);
    }
}
