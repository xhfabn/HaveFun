package com.wujiawei.business.controller.admin;

import com.wujiawei.common.context.LoginMemberContext;
import com.wujiawei.common.resp.CommonResp;
import com.wujiawei.common.resp.PageResp;
import com.wujiawei.business.req.ConfirmOrderQueryReq;
import com.wujiawei.business.req.ConfirmOrderSaveReq;
import com.wujiawei.business.resp.ConfirmOrderQueryResp;
import com.wujiawei.business.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/confirm-order")
public class ConfirmOrderAdminController {

    @Resource
    private ConfirmOrderService confirmOrderService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ConfirmOrderSaveReq req) {
        confirmOrderService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<ConfirmOrderQueryResp>> queryList(@Valid ConfirmOrderQueryReq req) {
        PageResp<ConfirmOrderQueryResp> list = confirmOrderService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        confirmOrderService.delete(id);
        return new CommonResp<>();
    }

}
