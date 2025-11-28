package com.wujiawei.member.controller;

import com.wujiawei.common.resp.CommonResp;
import com.wujiawei.common.resp.PageResp;
import com.wujiawei.member.req.*;
import com.wujiawei.member.resp.PassengerQueryResp;
import com.wujiawei.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/passenger")
public class PassengerController {
    @Resource
    private PassengerService passengerService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody PassengerSaveReq passengerSaveReq){
        passengerService.save(passengerSaveReq);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<PassengerQueryResp>> queryList(@Valid PassengerQueryReq passengerQueryReq){
        PageResp<PassengerQueryResp> queryList = passengerService.queryList(passengerQueryReq);
        return new CommonResp<>(queryList);
    }

    @GetMapping("/delete/{id}")
    public CommonResp<String> delete(@PathVariable long id){
        passengerService.delete(id);
        return new CommonResp<>("删除成功");
    }
}
