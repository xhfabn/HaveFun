package com.wujiawei.business.controller.admin;

import com.wujiawei.common.resp.CommonResp;
import com.wujiawei.common.resp.PageResp;
import com.wujiawei.business.req.TrainQueryReq;
import com.wujiawei.business.req.TrainSaveReq;
import com.wujiawei.business.resp.TrainQueryResp;
import com.wujiawei.business.service.TrainService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/train")
public class TrainAdminController {

    @Resource
    private TrainService trainService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainSaveReq req) {
        trainService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainQueryResp>> queryList(@Valid TrainQueryReq req) {
        PageResp<TrainQueryResp> list = trainService.queryList(req);
        return new CommonResp<>(list);
    }
    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> queryAll() {
        List<TrainQueryResp> list = trainService.queryAll();
        return new CommonResp<>(list);
    }


    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/gen-seat/{trainCode}")
    public CommonResp<Object> genseat(@PathVariable String trainCode) {
        trainService.genSeat(trainCode);
        return new CommonResp<>();
    }

}
