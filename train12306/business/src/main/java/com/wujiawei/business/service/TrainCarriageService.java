package com.wujiawei.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wujiawei.business.enums.SeatColEnum;
import com.wujiawei.business.util.UniqueQueryUtil;
import com.wujiawei.common.exception.BusinessException;
import com.wujiawei.common.exception.BusinessExceptionEnum;
import com.wujiawei.common.resp.PageResp;
import com.wujiawei.common.util.SnowUtil;
import com.wujiawei.business.domain.TrainCarriage;
import com.wujiawei.business.domain.TrainCarriageExample;
import com.wujiawei.business.mapper.TrainCarriageMapper;
import com.wujiawei.business.req.TrainCarriageQueryReq;
import com.wujiawei.business.req.TrainCarriageSaveReq;
import com.wujiawei.business.resp.TrainCarriageQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainCarriageService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainCarriageService.class);

    @Resource
    private TrainCarriageMapper trainCarriageMapper;

    public void save(TrainCarriageSaveReq req) {
        DateTime now = DateTime.now();
        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(seatColEnums.size());
        req.setSeatCount(req.getColCount()*req.getRowCount());
        TrainCarriage trainCarriage = BeanUtil.copyProperties(req, TrainCarriage.class);
        //使用泛型的方式判断主键是否重复
        if (ObjectUtil.isNull(trainCarriage.getId())) {
            TrainCarriage carriageDB= UniqueQueryUtil.selectByUnique(
                    TrainCarriageExample::new,
                    example -> {
                        example.createCriteria().andTrainCodeEqualTo(req.getTrainCode())
                                .andIdEqualTo(req.getId());
                        return example;
                    }, trainCarriageMapper,
                    mapper -> mapper::selectByExample);
            if(ObjectUtil.isNotNull(carriageDB)){
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CARRIAGE_INDEX_UNIQUE_ERROR);
            }
            trainCarriage.setId(SnowUtil.getSnowflakeNextId());
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.insert(trainCarriage);
        } else {
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.updateByPrimaryKey(trainCarriage);
        }
    }

    public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req) {
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        trainCarriageExample.setOrderByClause("id desc");
        TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
        if(ObjectUtil.isNotNull(req.getTrainCode())){
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }
        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<TrainCarriage> trainCarriageList = trainCarriageMapper.selectByExample(trainCarriageExample);

        PageInfo<TrainCarriage> pageInfo = new PageInfo<>(trainCarriageList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<TrainCarriageQueryResp> list = BeanUtil.copyToList(trainCarriageList, TrainCarriageQueryResp.class);

        PageResp<TrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        trainCarriageMapper.deleteByPrimaryKey(id);
    }

    public List<TrainCarriage> selectByTrainCode(String trainCode) {
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        trainCarriageExample.createCriteria().andTrainCodeEqualTo(trainCode);
        return  trainCarriageMapper.selectByExample(trainCarriageExample);
    }
}
