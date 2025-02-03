package com.lartimes.tiktok.schedule;

import com.lartimes.tiktok.model.vo.HotVideo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/2/2 19:24
 */
public class TopK {

    private int k = 0;

    private Queue<HotVideo> queue;

    public TopK(int k,Queue<HotVideo> queue){
        this.k = k;
        this.queue = queue;
    }

    public void add(HotVideo hotVideo) {
        if (queue.size() < k) {
            queue.add(hotVideo);
        } else if (queue.peek().getHot() < hotVideo.getHot()){
            queue.poll();
            queue.add(hotVideo);
        }

        return;
    }


    public List<HotVideo> get(){
        final ArrayList<HotVideo> list = new ArrayList<>(queue.size());
        while (!queue.isEmpty()) {
            list.add(queue.poll());
        }
        Collections.reverse(list);
        return list;
    }


}
