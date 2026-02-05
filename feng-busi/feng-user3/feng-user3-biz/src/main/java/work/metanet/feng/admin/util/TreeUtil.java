

package work.metanet.feng.admin.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import work.metanet.feng.admin.api.dto.SysDepartmentTree;
import work.metanet.feng.admin.api.dto.TreeNode;
import work.metanet.feng.admin.api.entity.SysDepartment;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CC
 * @date:2021/1/8
 */
@UtilityClass
public class TreeUtil {

    /**
     * 两层循环实现建树
     *
     * @param treeNodes 传入的树节点列表
     * @return
     */
    public <T extends TreeNode> List<T> build(List<T> treeNodes, Integer root) {

        List<T> trees = new ArrayList<>();

        for (T treeNode : treeNodes) {

            if (root.equals(treeNode.getParentId())) {
                trees.add(treeNode);
            }

            for (T it : treeNodes) {
                if (it.getParentId().equals(treeNode.getId())) {
                    if (treeNode.getChildren() == null) {
                        treeNode.setHasChildren(true);
                        treeNode.setChildren(new ArrayList<>());
                    }
                    treeNode.add(it);
                }
            }
        }
        return trees;
    }

    /**
     * 使用递归方法建树
     *
     * @param treeNodes
     * @return
     */
    public <T extends TreeNode> List<T> buildByRecursive(List<T> treeNodes, Object root) {
        List<T> trees = new ArrayList<T>();
        for (T treeNode : treeNodes) {
            if (root.equals(treeNode.getParentId())) {
                trees.add(findChildren(treeNode, treeNodes));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     *
     * @param treeNodes
     * @return
     */
    public <T extends TreeNode> T findChildren(T treeNode, List<T> treeNodes) {
        for (T it : treeNodes) {
            if (treeNode.getId().equals(it.getParentId())) {
                if (treeNode.getChildren() == null) {
                    treeNode.setHasChildren(true);
                    treeNode.setChildren(new ArrayList<>());
                }
                treeNode.add(findChildren(it, treeNodes));
            }
        }
        return treeNode;
    }

    /**
     * 通过sysMenu创建树形节点
     *
     * @param sysDepartments
     * @param root
     * @return
     */
    public List<SysDepartmentTree> buildTree(List<SysDepartment> sysDepartments, Integer root) {
        List<SysDepartmentTree> trees = new ArrayList<>();
        SysDepartmentTree node;
        for (SysDepartment sysDepartment : sysDepartments) {
            node = new SysDepartmentTree();
            BeanUtil.copyProperties(sysDepartment, node, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            node.setDeptCode(sysDepartment.getDeptCode());
            node.setParentCode(sysDepartment.getParentCode());
            node.setId(sysDepartment.getId());
            node.setParentId(sysDepartment.getParentId());
            trees.add(node);
        }
        return TreeUtil.build(trees, root);
    }

}
