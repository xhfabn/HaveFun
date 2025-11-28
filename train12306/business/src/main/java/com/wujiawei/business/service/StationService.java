package com.wujiawei.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wujiawei.business.domain.Train;
import com.wujiawei.business.domain.TrainExample;
import com.wujiawei.business.resp.TrainQueryResp;
import com.wujiawei.business.util.UniqueQueryUtil;
import com.wujiawei.common.exception.BusinessException;
import com.wujiawei.common.exception.BusinessExceptionEnum;
import com.wujiawei.common.resp.PageResp;
import com.wujiawei.common.util.SnowUtil;
import com.wujiawei.business.domain.Station;
import com.wujiawei.business.domain.StationExample;
import com.wujiawei.business.mapper.StationMapper;
import com.wujiawei.business.req.StationQueryReq;
import com.wujiawei.business.req.StationSaveReq;
import com.wujiawei.business.resp.StationQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {

    private static final Logger LOG = LoggerFactory.getLogger(StationService.class);

    @Resource
    private StationMapper stationMapper;

    public void save(StationSaveReq req) {
        DateTime now = DateTime.now();
        Station station = BeanUtil.copyProperties(req, Station.class);
        if (ObjectUtil.isNull(station.getId())) {
            Station stationDB = UniqueQueryUtil.selectByUnique(StationExample::new,
                    example -> {
                        example.createCriteria().andNameEqualTo(req.getName());
                        return example;
                    },
                    stationMapper,
                    mapper -> mapper::selectByExample);
            if(ObjectUtil.isNotEmpty(stationDB)){
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR);
            }
            station.setId(SnowUtil.getSnowflakeNextId());
            station.setCreateTime(now);
            station.setUpdateTime(now);
            stationMapper.insert(station);
        } else {
            station.setUpdateTime(now);
            stationMapper.updateByPrimaryKey(station);
        }
    }

    public PageResp<StationQueryResp> queryList(StationQueryReq req) {
        StationExample stationExample = new StationExample();
        stationExample.setOrderByClause("id desc");
        StationExample.Criteria criteria = stationExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Station> stationList = stationMapper.selectByExample(stationExample);

        PageInfo<Station> pageInfo = new PageInfo<>(stationList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<StationQueryResp> list = BeanUtil.copyToList(stationList, StationQueryResp.class);

        PageResp<StationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }
    public List<StationQueryResp> queryAll() {
        StationExample stationExample = new StationExample();
        stationExample.setOrderByClause("name desc");
        List<Station> trainList = stationMapper.selectByExample(stationExample);
        return BeanUtil.copyToList(trainList, StationQueryResp.class);
    }

    public void delete(Long id) {
        stationMapper.deleteByPrimaryKey(id);
    }
}
