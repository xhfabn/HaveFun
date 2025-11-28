package com.wujiawei.business.util;

import cn.hutool.core.collection.CollUtil;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class UniqueQueryUtil {
    /**
     * 根据条件查询唯一记录
     * @param exampleSupplier 提供Example/QueryWrapper对象的 Supplier
     * @param conditionSetter 用于设置查询条件的函数（参数为Example对象，返回设置后的Example）
     * @param mapper 执行查询的Mapper对象
     * @param queryFunction 执行查询的方法（接收Mapper和Example，返回查询结果列表）
     * @param <T> 实体类类型
     * @param <E> Example/QueryWrapper类型
     * @param <M> Mapper接口类型
     * @return 唯一记录或null
     */
    public static <T, E, M> T selectByUnique(
            Supplier<E> exampleSupplier,
            Function<E, E> conditionSetter,
            M mapper,
            Function<M, Function<E, List<T>>> queryFunction) {

        // 创建查询条件对象
        E example = exampleSupplier.get();
        // 设置查询条件
        conditionSetter.apply(example);
        // 执行查询d
        List<T> list = queryFunction.apply(mapper).apply(example);
        // 返回第一条记录或null
        return CollUtil.isNotEmpty(list) ? list.get(0) : null;
    }
}
