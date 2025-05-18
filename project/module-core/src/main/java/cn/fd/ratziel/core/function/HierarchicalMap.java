package cn.fd.ratziel.core.function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * HierarchicalMap
 *
 * <p>
 * 基于类层次结构的存储表
 *
 * <p>特性说明：</p>
 * <ul>
 *   <li>存入值时自动以值的运行时类作为存储键</li>
 *   <li>查询时支持类继承链查找（包括父类和接口）</li>
 *   <li>可配置层级跨度限制（maxDepth）</li>
 * </ul>
 *
 * @author TheFloodDragon
 * @since 2025/5/18 01:15
 */
public class HierarchicalMap {

    /**
     * 类到值的核心存储映射
     */
    private final Map<Class<?>, Object> map;

    /**
     * 允许的最大类层级跨度 (0=仅精确匹配，1=允许直接父类/接口)
     */
    private final int maxDepth;

    public HierarchicalMap() {
        this.maxDepth = 1; // 默认为 1
        this.map = new HashMap<>();
    }

    /**
     * 构造方法
     *
     * @param maxDepth 允许的最大层级跨度（非负整数）
     * @throws IllegalArgumentException 如果maxDepth为负数
     */
    public HierarchicalMap(int maxDepth) {
        if (maxDepth < 0) {
            throw new IllegalArgumentException("Max depth cannot be negative");
        }
        this.maxDepth = maxDepth;
        this.map = new HashMap<>();
    }

    /**
     * 存入一个值（基于其运行时类）
     *
     * <p>操作逻辑：</p>
     * <ol>
     *   <li>检查新类与现有类的层级关系，确保不超过maxDepth限制</li>
     *   <li>移除受影响的父类/子类条目</li>
     *   <li>将新值存入映射表</li>
     * </ol>
     *
     * @param value 要存储的值（非null）
     * @throws IllegalStateException 如果新类导致层级跨度超过限制
     */
    public synchronized void put(@NotNull Object value) {
        Class<?> clazz = value.getClass();

        // 层级安全检查
        checkHierarchy(clazz);

        // 清理受影响的条目
        removeAffectedEntries(clazz);

        // 存入新值
        map.put(clazz, value);
    }

    /**
     * 根据对象查找匹配的值
     *
     * <p>查找规则：</p>
     * <ol>
     *   <li>优先检查对象的精确类</li>
     *   <li>广度优先遍历继承链（父类 -> 接口）</li>
     *   <li>返回第一个找到的匹配值</li>
     * </ol>
     *
     * @param type 查询的类
     * @return 找到的匹配值，未找到时返回null
     */
    public synchronized @Nullable Object get(@NotNull Class<?> type) {
        for (Class<?> current : getHierarchy(type)) {
            Object value = map.get(current);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 删除值
     */
    public synchronized void remove(@NotNull Class<?> type) {
        map.remove(type);
    }

    /**
     * 获取所有存储的值
     */
    public synchronized Collection<Object> values() {
        return Collections.unmodifiableCollection(map.values());
    }

    /**
     * 检查新类与现有类的层级关系安全性
     *
     * @param newClass 新插入的类
     * @throws IllegalStateException 如果发现超过maxDepth的层级跨度
     */
    private void checkHierarchy(Class<?> newClass) {
        for (Class<?> existing : map.keySet()) {
            int distance = getDistance(existing, newClass);
            if (distance != -1 && distance > maxDepth) {
                throw new IllegalStateException(String.format(
                        "Class %s exceeds max depth %d with %s (distance=%d)",
                        newClass.getSimpleName(),
                        maxDepth,
                        existing.getSimpleName(),
                        distance
                ));
            }
        }
    }

    /**
     * 移除受新类影响的条目
     *
     * <p>影响范围包括：</p>
     * <ul>
     *   <li>新类的父类/接口（在maxDepth范围内）</li>
     *   <li>新类的子类（超过maxDepth范围）</li>
     * </ul>
     */
    private void removeAffectedEntries(Class<?> newClass) {
        // 移除父类/接口
        Set<Class<?>> supers = getAllSupers(newClass, maxDepth);
        supers.stream().filter(map::containsKey).forEach(map::remove);

        // 移除超过深度的子类
        map.keySet().removeIf(existing -> {
            int distance = getDistance(newClass, existing);
            return distance != -1 && distance > maxDepth;
        });
    }

    /**
     * 生成类的继承链（广度优先顺序）
     *
     * @param clazz 起始类
     * @return 继承链列表（包含自身、父类和接口）
     */
    private static List<Class<?>> getHierarchy(Class<?> clazz) {
        List<Class<?>> hierarchy = new ArrayList<>();
        Queue<Class<?>> queue = new LinkedList<>();
        Set<Class<?>> visited = new HashSet<>();

        queue.add(clazz);
        visited.add(clazz);

        while (!queue.isEmpty()) {
            Class<?> current = queue.poll();
            hierarchy.add(current);

            // 添加父类
            Class<?> superClass = current.getSuperclass();
            if (superClass != null && visited.add(superClass)) {
                queue.add(superClass);
            }

            // 添加接口
            for (Class<?> interfaceClazz : current.getInterfaces()) {
                if (visited.add(interfaceClazz)) {
                    queue.add(interfaceClazz);
                }
            }
        }
        return hierarchy;
    }

    /**
     * 计算两个类之间的最小层级距离
     *
     * @param subClass   子类（或实现类）
     * @param superClass 父类（或接口）
     * @return 层级距离（-1表示无继承关系）
     */
    private static int getDistance(Class<?> subClass, Class<?> superClass) {
        if (!superClass.isAssignableFrom(subClass)) return -1;

        // 使用BFS计算最短路径
        Map<Class<?>, Integer> distances = new HashMap<>();
        Queue<Class<?>> queue = new LinkedList<>();

        distances.put(subClass, 0);
        queue.add(subClass);

        while (!queue.isEmpty()) {
            Class<?> current = queue.poll();
            int currentDist = distances.get(current);

            // 找到目标类
            if (current.equals(superClass)) {
                return currentDist;
            }

            // 处理父类
            Class<?> currentSuper = current.getSuperclass();
            if (currentSuper != null && !distances.containsKey(currentSuper)) {
                distances.put(currentSuper, currentDist + 1);
                queue.add(currentSuper);
            }

            // 处理接口
            for (Class<?> interfaceClazz : current.getInterfaces()) {
                if (!distances.containsKey(interfaceClazz)) {
                    distances.put(interfaceClazz, currentDist + 1);
                    queue.add(interfaceClazz);
                }
            }
        }
        return -1;
    }

    /**
     * 获取指定范围内的所有父类和接口
     *
     * @param clazz    起始类
     * @param maxDepth 最大搜索深度
     * @return 符合条件的父类/接口集合
     */
    private static Set<Class<?>> getAllSupers(Class<?> clazz, int maxDepth) {
        Set<Class<?>> supers = new HashSet<>();
        Queue<Map.Entry<Class<?>, Integer>> queue = new LinkedList<>();

        queue.offer(new AbstractMap.SimpleEntry<>(clazz, 0));

        while (!queue.isEmpty()) {
            Map.Entry<Class<?>, Integer> entry = queue.poll();
            Class<?> current = entry.getKey();
            int depth = entry.getValue();

            if (depth > maxDepth) continue;

            // 处理父类
            Class<?> superClass = current.getSuperclass();
            if (superClass != null && supers.add(superClass)) {
                queue.offer(new AbstractMap.SimpleEntry<>(superClass, depth + 1));
            }

            // 处理接口
            for (Class<?> interfaceClazz : current.getInterfaces()) {
                if (supers.add(interfaceClazz)) {
                    queue.offer(new AbstractMap.SimpleEntry<>(interfaceClazz, depth + 1));
                }
            }
        }
        return supers;
    }

}
