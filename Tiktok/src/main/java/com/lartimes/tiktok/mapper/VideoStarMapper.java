package com.lartimes.tiktok.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lartimes.tiktok.model.video.VideoStar;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lartimes
 */
@Mapper
public interface VideoStarMapper extends BaseMapper<VideoStar> {
    Integer insertBatchSomeColumn(Collection<VideoStar> entityList);

}
