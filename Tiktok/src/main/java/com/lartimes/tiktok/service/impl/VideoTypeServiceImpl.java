package com.lartimes.tiktok.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lartimes.tiktok.mapper.VideoTypeMapper;
import com.lartimes.tiktok.model.po.VideoType;
import com.lartimes.tiktok.service.VideoTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lartimes
 */
@Slf4j
@Service
public class VideoTypeServiceImpl extends ServiceImpl<VideoTypeMapper, VideoType> implements VideoTypeService {

}
