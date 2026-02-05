package work.metanet.feng.ai.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;


/**
 * 任务管理类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public class TaskManager {
    private static final ConcurrentHashMap<Integer, List<Future<?>>> TASK_MAP = new ConcurrentHashMap<>();

    /**
     * 提交任务
     */
    public static void submitTask(Integer id, Callable<?> function) {
        Future<?> future = AnalysisThreadPool.getThreadPool().submit(function);
        List<Future<?>> orDefault = TASK_MAP.getOrDefault(id, new ArrayList<>());
        orDefault.add(future);
        TASK_MAP.put(id, orDefault);
    }

    /**
     * 弹出任务
     */
    public void popTaskResult(Integer id) {
        TASK_MAP.remove(id);
    }

    public int getCount(Integer id) {
        if (TASK_MAP.containsKey(id)) {
            Collection<?> collection = TASK_MAP.get(id);
            return collection != null ? collection.size() : 0;
        }
        return 0;
    }
}
