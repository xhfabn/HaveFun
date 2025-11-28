package com.wujiawei.member.service;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wujiawei.common.context.LoginMemberContext;
import com.wujiawei.common.resp.PageResp;
import com.wujiawei.common.util.SnowUtil;

import com.wujiawei.member.domain.Passenger;
import com.wujiawei.member.domain.PassengerExample;

import com.wujiawei.member.mapper.PassengerMapper;
import com.wujiawei.member.req.*;
import com.wujiawei.member.resp.PassengerQueryResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class PassengerService {

    @Resource
    private PassengerMapper passengerMapper;

    public void save(PassengerSaveReq passengerSaveReq) {
        Passenger passenger = BeanUtil.copyProperties(passengerSaveReq, Passenger.class);
        DateTime now = DateTime.now();
        if(ObjectUtil.isNull(passenger.getId())){
            passenger.setMemberId(LoginMemberContext.getId());
            passenger.setCreateTime(now);
            passenger.setUpdateTime(now);
            passenger.setId(SnowUtil.getSnowflakeNextId());
            passengerMapper.insert(passenger);
        }else{
            passenger.setUpdateTime(now);
            passengerMapper.updateByPrimaryKey(passenger);
        }

    }

    public PageResp<PassengerQueryResp> queryList(PassengerQueryReq passengerQueryReq) {
        PassengerExample passengerExample = new PassengerExample();
        passengerExample.setOrderByClause("id desc");
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        if(ObjectUtil.isNotNull(passengerQueryReq.getMemberId())){
            criteria.andMemberIdEqualTo(passengerQueryReq.getMemberId());
        }
        PageHelper.startPage(passengerQueryReq.getPage(), passengerQueryReq.getSize());
        List<Passenger> passengers = passengerMapper.selectByExample(passengerExample);
        PageInfo<Passenger> pageInfo = new PageInfo<>(passengers);

        List<PassengerQueryResp> list = BeanUtil.copyToList(passengers, PassengerQueryResp.class);
        PageResp<PassengerQueryResp> pageResp = new PageResp<>();
        pageResp.setList(list);
        pageInfo.setTotal(pageInfo.getTotal());
        return pageResp;
    }

    public void delete(long id) {
        passengerMapper.deleteByPrimaryKey(id);
    }

}
