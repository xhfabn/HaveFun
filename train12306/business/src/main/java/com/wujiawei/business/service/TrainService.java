package com.wujiawei.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wujiawei.business.domain.TrainCarriage;
import com.wujiawei.business.domain.TrainSeat;
import com.wujiawei.business.enums.SeatColEnum;
import com.wujiawei.business.mapper.TrainSeatMapper;
import com.wujiawei.business.util.UniqueQueryUtil;
import com.wujiawei.common.exception.BusinessException;
import com.wujiawei.common.exception.BusinessExceptionEnum;
import com.wujiawei.common.resp.PageResp;
import com.wujiawei.common.util.SnowUtil;
import com.wujiawei.business.domain.Train;
import com.wujiawei.business.domain.TrainExample;
import com.wujiawei.business.mapper.TrainMapper;
import com.wujiawei.business.req.TrainQueryReq;
import com.wujiawei.business.req.TrainSaveReq;
import com.wujiawei.business.resp.TrainQueryResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainService.class);

    @Resource
    private TrainMapper trainMapper;
    @Resource
    private TrainCarriageService trainCarriageService;

    @Resource
    private TrainSeatMapper trainSeatMapper;

    public void save(TrainSaveReq req) {
        DateTime now = DateTime.now();
        Train train = BeanUtil.copyProperties(req, Train.class);
        LOG.info("测试1");
        if (ObjectUtil.isNull(train.getId())) {
            LOG.info("测试2");
            Train trainDB = UniqueQueryUtil.selectByUnique(
                    TrainExample::new,
                    example -> {
                        example.createCriteria().andCodeEqualTo(req.getCode());
                        return example; // 关键：返回E类型的example
                    }, trainMapper,
                    mapper -> mapper::selectByExample
            );
            if (ObjectUtil.isNotEmpty(trainDB)) {
                LOG.info("测试3");
                throw new BusinessException(BusinessExceptionEnum.BUSSINESS_TAIIN_CODE_UNIQUE_ERROR);
            }
            LOG.info("测试4");
            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainMapper.insert(train);
        } else {
            train.setUpdateTime(now);
            trainMapper.updateByPrimaryKey(train);
        }
    }

    public PageResp<TrainQueryResp> queryList(TrainQueryReq req) {
        TrainExample trainExample = new TrainExample();
        trainExample.setOrderByClause("id desc");
        TrainExample.Criteria criteria = trainExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Train> trainList = trainMapper.selectByExample(trainExample);

        PageInfo<Train> pageInfo = new PageInfo<>(trainList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<TrainQueryResp> list = BeanUtil.copyToList(trainList, TrainQueryResp.class);

        PageResp<TrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }
    public List<TrainQueryResp> queryAll() {
        TrainExample trainExample = new TrainExample();
        trainExample.setOrderByClause("code desc");
        List<Train> trainList = trainMapper.selectByExample(trainExample);
        return BeanUtil.copyToList(trainList, TrainQueryResp.class);
    }

    public void delete(Long id) {
        trainMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public void genSeat(String trainCode) {
        DateTime now = DateTime.now();
        //先清空当前车次下的所有的座位记录
        TrainExample trainExample = new TrainExample();
        TrainExample.Criteria criteria = trainExample.createCriteria();
        criteria.andCodeEqualTo(trainCode);
        trainMapper.deleteByExample(trainExample);

        //查找当前车次下的所有的车厢
        List<TrainCarriage> trainCarriages = trainCarriageService.selectByTrainCode(trainCode);
        LOG.info("当下车次下的车厢数: {}", trainCarriages.size());
        for (TrainCarriage trainCarriage : trainCarriages) {
            Integer rowCount = trainCarriage.getRowCount();
            String seatType = trainCarriage.getSeatType();
             int seatIndex=1;

            List<SeatColEnum> colsByType = SeatColEnum.getColsByType(seatType);
            LOG.info("根据车厢的座位类型筛选出所有的列：{}",colsByType);
            for (int row = 1; row <= rowCount; row++) {
                // 循环列数
                for (SeatColEnum seatColEnum : colsByType) {
                    // 构造座位数据并保存数据库
                    TrainSeat trainSeat = new TrainSeat();
                    trainSeat.setId(SnowUtil.getSnowflakeNextId());
                    trainSeat.setTrainCode(trainCode);
                    trainSeat.setCarriageIndex(trainCarriage.getIndex());
                    trainSeat.setRow(StrUtil.fillBefore(String.valueOf(row), '0', 2));
                    trainSeat.setCol(seatColEnum.getCode());
                    trainSeat.setSeatType(seatType);
                    trainSeat.setCarriageSeatIndex(seatIndex++);
                    trainSeat.setCreateTime(now);
                    trainSeat.setUpdateTime(now);
                    trainSeatMapper.insert(trainSeat);
                }
            }
        }
    }

}
