package com.graduate.zl.checkConsistency;

import com.graduate.zl.common.Constants;
import com.graduate.zl.common.model.Lts.LNode;
import com.graduate.zl.common.model.Lts.LTS;
import com.graduate.zl.common.model.Lts.LTransition;
import com.graduate.zl.common.model.Lts.LTransitionLabel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vincent on 2018/9/24.
 */
public class LtsConsistency {

    //不一致原因
    private static String error_cause = null;

    /**
     *
     * @param modelLts
     * @param codeLts
     * @return
     */
    public static String[] checkConsistencyBtwLTS(LTS modelLts, LTS codeLts) {
        String[] ret = checkConsistencyBtwLTS(modelLts, codeLts, null);
        return ret;
    }

    public static String[] checkConsistencyBtwLTS(LTS modelLts, LTS codeLts, Map<String, List<String>> modelToClass) {
        String[] ret = new String[2];
        boolean isConsistent = checkConsistency(modelLts.getStart(), codeLts.getStart(), modelToClass);
        if(isConsistent) ret[0] = Constants.YES_STR;
        else ret[0] = Constants.NO_STR;
        ret[1] = error_cause;
        return ret;
    }

    private static boolean checkConsistency(LNode modelNode, LNode codeNode, Map<String, List<String>> modelToClass) {
        return false;
    }

    /**
     * 常规一致性检查（不进行约简，但要过滤CF片段产生的节点和Transition）
     * @param modelPath
     * @param codeNode
     * @param modelToClass
     * @param checkTransition true: 检查transition；false：不检查transition
     * @param matchLevel
     * @return
     */
    private static boolean commonCheck(List<LNodeAndTransition> modelPath, LNode codeNode, Map<String, List<String>> modelToClass, boolean checkTransition, int matchLevel) {

        return false;
    }

    /**
     * 基于搜索回溯的一致性检查
     * @param modelLTS
     * @param codeLTS
     * @param modelToClass
     * @param checkTransition
     * @return
     */
    public static boolean backtrackingCheck(List<LNodeAndTransition> modelLTS, LNode codeLTS, Map<String, List<String>> modelToClass, boolean checkTransition, int mIndex) {
        if(mIndex >= modelLTS.size()) {
            if(codeLTS==null)
                return true;
            else
                return false;
        }
        if(codeLTS == null) {
            if(mIndex < modelLTS.size())
                return false;
        }
        LNodeAndTransition curModelNT = modelLTS.get(mIndex);
        String curModelName = curModelNT.getNode().getLabel();
        //如果当前模型节点与代码节点能够映射
        if(modelToClass.get(curModelName).contains(codeLTS.getLabel())) {
            //如果当前模型节点是最后一个节点
            if(mIndex == modelLTS.size()-1) {
                LNode codeNextNode = null;
                for(LNode node : codeLTS.getNext().keySet()) {
                    codeNextNode = node;
                    break;
                }
                return backtrackingCheck(modelLTS, codeNextNode, modelToClass, checkTransition, mIndex+1);
            }else {
                LNodeAndTransition nextModelNT = modelLTS.get(mIndex+1);
                //如果当前模型节点和相邻后续节点是相同对象，则需要考虑“向前探索”
                if(nextModelNT.getNode().getLabel().equals(curModelNT.getNode().getLabel())) {
                    LNode nextCodeNode = null;
                    for(LNode cNode : codeLTS.getNext().keySet()) {
                        nextCodeNode = cNode;
                        break;
                    }
                    return backtrackingCheck(modelLTS, nextCodeNode, modelToClass, checkTransition, mIndex) ||
                            backtrackingCheck(modelLTS, nextCodeNode, modelToClass, checkTransition, mIndex+1);
                }else {
                    //如果当前模型节点和相邻后续节点不是相同对象，则对于代码节点而言，需要考虑“一对多”实现的情况
                    LNode tmp = codeLTS;
                    //如果当前代码节点与后续节点映射为同一个对象，则一直向后查找，直到找到一个不相同的为止
                    while(modelToClass.get(curModelName).contains(tmp.getLabel())) {
                        if(tmp.getNext().size() == 0) {
                            tmp = null;
                            break;
                        }
                        for(LNode cNode : tmp.getNext().keySet()) {
                            tmp = cNode;
                            break;
                        }
                    }
                    return backtrackingCheck(modelLTS, tmp, modelToClass, checkTransition, mIndex+1);
                }
            }
        }else {
            return false;
        }
    }

    /**
     * 获取LTS的所有路径
     * @param root
     * @return
     */
    private static List<List<LNodeAndTransition>> getAllPath(LNode root) {
        List<List<LNodeAndTransition>> ret = new ArrayList<>();
        dfs(ret, root, new ArrayList<>());
        return ret;
    }

    /**
     * 深度优先搜索获取所有路径
     * @param ret
     * @param node
     * @param sigPath
     */
    private static void dfs(List<List<LNodeAndTransition>> ret, LNode node, List<LNodeAndTransition> sigPath) {
        if(node==null) return;

        if(node.getNext().size() == 0) {
            LNodeAndTransition nt = new LNodeAndTransition(node, null);
            sigPath.add(nt);

            ret.add(new ArrayList<>(sigPath));
            sigPath.remove(sigPath.size() - 1);
            return;
        }

        for(LNode key : node.getNext().keySet()) {
            LNodeAndTransition nt = new LNodeAndTransition(node, node.getNext().get(key));
            sigPath.add(nt);
            dfs(ret, key, sigPath);
            sigPath.remove(sigPath.size() - 1);
        }
    }

    /**
     * 检查s1与s2是否匹配：如果modelToClass不为null，则为检查s1对应的list中是否有元素与s2匹配
     * @param s1
     * @param s2
     * @param modelToClass
     * @param matchLevel matchLevel为1，则为字符串严格匹配；如果为2，则为字符串中只要存在子串匹配即可（例如：SendPing与PingEcho）
     * @return
     */
    private static boolean match(String s1, String s2, Map<String, List<String>> modelToClass, int matchLevel) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        if(modelToClass == null) {
            if(matchLevel == 1) {
                return s1.equals(s2);
            }else if(matchLevel == 2) {
                if(longestCommonSubstring(s1, s2) > 3)
                    return true;
            }
        } else {
            List<String> mapping = modelToClass.get(s1);
            for(String str : mapping) {
                if(matchLevel == 1) {
                    if(s2.equals(str)) return true;
                } else if(matchLevel == 2) {
                    //此处默认两个字符串只要有长度大于3的公共子串，则认为match
                    if(longestCommonSubstring(str, s2) > 3) return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回两个字符串最长公共子串的长度
     * @param str1
     * @param str2
     * @return
     */
    private static int longestCommonSubstring(String str1, String str2) {
        if(str1==null || str2==null) return 0;
        int len1 = str1.length(), len2 = str2.length();
        if(len1==0 || len2==0) return 0;

        int[] topLine = new int[len1], currentLine = new int[len1];
        int maxLen = 0; char ch = ' ';

        for(int i=0; i<len2; i++){
            ch = str2.charAt(i);
            for(int j=0; j<len1; j++){
                if( ch == str1.charAt(j)){
                    if(j==0){
                        currentLine[j] = 1;
                    } else{
                        currentLine[j] = topLine[j-1] + 1;
                    }
                    if(currentLine[j] > maxLen){
                        maxLen = currentLine[j];
                    }
                }
            }
            for(int k=0; k<len1; k++){
                topLine[k] = currentLine[k];
                currentLine[k] = 0;
            }
        }
        return maxLen;
    }

    private static class LNodeAndTransition{
        @Getter
        private LNode node;
        @Getter
        private LTransition transition;

        public LNodeAndTransition(LNode node, LTransition transition) {
            this.node = node;
            this.transition = transition;
        }

        @Override
        public String toString() {
            return "LNodeAndTransition{" +
                    "node=" + node +
                    ", transition=" + transition +
                    '}';
        }
    }

    /**
     * 生产一个LTS，用于测试路径打印
     * 0->1->2->3->4->5->6->7->8
     *       |->9->|  |---->|
     * 该LTS一共包括4条分支路径
     * @return
     */
    public static LNode produceLTS() {
        LNode start = new LNode(0, "start");
        LNode node1 = new LNode(1, "node1");
        LNode node2 = new LNode(2, "node2");
        LNode node3 = new LNode(3, "node3");
        LNode node4 = new LNode(4, "node4");
        LNode node5 = new LNode(5, "node5");
        LNode node6 = new LNode(6, "node6");
        LNode node7 = new LNode(7, "node7");
        LNode node8 = new LNode(8, "node8");
        LNode node9 = new LNode(9, "node9");
        start.getNext().put(node1, new LTransition(new LTransitionLabel("startTo1")));
        node1.getNext().put(node2, new LTransition(new LTransitionLabel("node1To2")));
        node2.getNext().put(node3, new LTransition(new LTransitionLabel("node2To3")));
        node3.getNext().put(node4, new LTransition(new LTransitionLabel("node3To4")));
        node4.getNext().put(node5, new LTransition(new LTransitionLabel("node4To5")));
        node2.getNext().put(node9, new LTransition(new LTransitionLabel("node2To9")));
        node9.getNext().put(node4, new LTransition(new LTransitionLabel("node9To4")));
        node5.getNext().put(node6, new LTransition(new LTransitionLabel("node5To6")));
        node6.getNext().put(node7, new LTransition(new LTransitionLabel("node6To7")));
        node7.getNext().put(node8, new LTransition(new LTransitionLabel("node7To8")));
        node5.getNext().put(node7, new LTransition(new LTransitionLabel("node5To7")));
        return start;
    }


    /**
     * 产生模型LTS，用于测试一致性检测
     * A1->B1->B2->C1->B3->A2
     *     |------>|
     * @return
     */
    public static LNode produceModelLTS() {
        LNode A1 = new LNode(0, "A");
        LNode B1 = new LNode(0, "B");
        LNode B2 = new LNode(0, "B");
        LNode C1 = new LNode(0, "C");
        LNode B3 = new LNode(0, "B");
        LNode A2 = new LNode(0, "A");
        A1.getNext().put(B1, new LTransition(new LTransitionLabel("msg1")));
        B1.getNext().put(B2, new LTransition(new LTransitionLabel("msg2")));
        B2.getNext().put(C1, new LTransition(new LTransitionLabel("msg3")));
        C1.getNext().put(B3, new LTransition(new LTransitionLabel("msg4")));
        B3.getNext().put(A2, new LTransition(new LTransitionLabel("msg5")));
        B1.getNext().put(C1, new LTransition(new LTransitionLabel("msg6")));
        return A1;
    }


    /**
     * 产生三条代码执行路径，用于一致性检测
     * 其中，第一条是无法匹配的；第二条是可匹配的，但不需要“向前探索”；第三条是可匹配的，但需要“向前探索”
     * @return
     */
    public static List<LNode> produceCodeLTS() {
        List<LNode> ret = new ArrayList<>();

        //A->B->C->B->B,无法匹配任何一条模型分支路径
        LNode error1 = new LNode(0, "AClass1");
        LNode error2 = new LNode(1, "BClass1");
        LNode error3 = new LNode(2, "CClass1");
        LNode error4 = new LNode(3, "BClass2");
        LNode error5 = new LNode(4, "BClass3");
        error1.getNext().put(error2, new LTransition(new LTransitionLabel("msg1")));
        error2.getNext().put(error3, new LTransition(new LTransitionLabel("msg2")));
        error3.getNext().put(error4, new LTransition(new LTransitionLabel("msg3")));
        error4.getNext().put(error5, new LTransition(new LTransitionLabel("msg4")));
        ret.add(error1);

        //A->A->B->B->C->B->A，可以匹配A->B->B->C->B->A
        LNode correct1 = new LNode(0, "AClass1");
        LNode correct2 = new LNode(1, "AClass2");
        LNode correct3 = new LNode(2, "BClass1");
        LNode correct4 = new LNode(3, "BClass2");
        LNode correct5 = new LNode(4, "CClass1");
        LNode correct6 = new LNode(5, "BClass3");
        LNode correct7 = new LNode(6, "AClass3");
        correct1.getNext().put(correct2, new LTransition(new LTransitionLabel("msg1")));
        correct2.getNext().put(correct3, new LTransition(new LTransitionLabel("msg2")));
        correct3.getNext().put(correct4, new LTransition(new LTransitionLabel("msg3")));
        correct4.getNext().put(correct5, new LTransition(new LTransitionLabel("msg4")));
        correct5.getNext().put(correct6, new LTransition(new LTransitionLabel("msg5")));
        correct6.getNext().put(correct7, new LTransition(new LTransitionLabel("msg6")));
        ret.add(correct1);

        //A->B->B->B->B->C->B->A，可以匹配A->B->B->C->B->A
        LNode correct1FW = new LNode(0, "AClass1");
        LNode correct2FW = new LNode(1, "BClass1");
        LNode correct3FW = new LNode(2, "BClass2");
        LNode correct4FW = new LNode(3, "BClass3");
        LNode correct5FW = new LNode(4, "BClass4");
        LNode correct6FW = new LNode(5, "CClass1");
        LNode correct7FW = new LNode(6, "BClass5");
        LNode correct8FW = new LNode(7, "AClass2");
        correct1FW.getNext().put(correct2FW, new LTransition(new LTransitionLabel("msg1")));
        correct2FW.getNext().put(correct3FW, new LTransition(new LTransitionLabel("msg2")));
        correct3FW.getNext().put(correct4FW, new LTransition(new LTransitionLabel("msg3")));
        correct4FW.getNext().put(correct5FW, new LTransition(new LTransitionLabel("msg4")));
        correct5FW.getNext().put(correct6FW, new LTransition(new LTransitionLabel("msg5")));
        correct6FW.getNext().put(correct7FW, new LTransition(new LTransitionLabel("msg6")));
        correct7FW.getNext().put(correct8FW, new LTransition(new LTransitionLabel("msg7")));
        ret.add(correct1FW);

        return ret;
    }

    /**
     * 产生模型对象到代码类的映射，用于测试一致性
     * @return
     */
    public static Map<String, List<String>> produceModelMapClass() {
        Map<String, List<String>> modelMapClass = new HashMap<>();
        List<String> al = new ArrayList<>();
        List<String> bl = new ArrayList<>();
        List<String> cl = new ArrayList<>();
        for(int i=1;i<=5;i++) {
            al.add("AClass"+String.valueOf(i));
            bl.add("BClass"+String.valueOf(i));
            cl.add("CClass"+String.valueOf(i));
        }
        modelMapClass.put("A", al);
        modelMapClass.put("B", bl);
        modelMapClass.put("C", cl);
        return modelMapClass;
    }

    /**
     * 测试从LTS中获取所有的路径
     * @param start LTS模型的入口节点
     */
    public static void testGetAllPath(LNode start) {
        List<List<LNodeAndTransition>> allPath = getAllPath(start);
        StringBuilder sb;
        for(List<LNodeAndTransition> path : allPath) {
            sb = new StringBuilder();
            for(LNodeAndTransition nt : path) {
                sb.append(nt.getNode().getNumber()+">");
                System.out.println(nt.toString());
            }
            System.out.println(sb.substring(0, sb.length()-1));
            System.out.println("---------------------");
        }
    }

    public static void testCheckConsistency() {
        Map<String, List<String>> modelToClass = produceModelMapClass();

        List<LNode> codePathList = produceCodeLTS();

        LNode modelLTS = produceModelLTS();
        List<List<LNodeAndTransition>> modelPaths = getAllPath(modelLTS);
        for(LNode codePath : codePathList) {
            boolean match = false;
            for(List<LNodeAndTransition> modelPath : modelPaths) {
                if(backtrackingCheck(modelPath, codePath, modelToClass, true, 0)) {
                    match = true;
                    break;
                }
            }
            if(match) {
                System.out.println("当前代码执行路径与模型路径符合一致性检测");
            }else {
                System.out.println("当前代码执行路径与模型路径不符合一致性检测");
            }
        }
    }

    public static void main(String[] args) {
        //测试获取全部分支路径
        testGetAllPath(produceLTS());

        //测试一致性检测，包括三种情况
//        testCheckConsistency();
    }
}
