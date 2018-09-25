package com.graduate.zl.checkConsistency;

import com.graduate.zl.common.Constants;
import com.graduate.zl.common.model.Lts.LNode;
import com.graduate.zl.common.model.Lts.LTS;
import com.graduate.zl.common.model.Lts.LTransition;
import com.graduate.zl.common.model.Lts.LTransitionLabel;

import java.util.ArrayList;
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
     * 规约一致性检查
     * @param modelPath
     * @param codeNode
     * @param modelToClass
     * @param checkTransition
     * @param matchLevel
     * @return
     */
    private static boolean reductionCheck(List<LNodeAndTransition> modelPath, LNode codeNode, Map<String, List<String>> modelToClass, boolean checkTransition, int matchLevel) {

        return false;
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
        private LNode node;
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

    public static void main(String[] args) {
        LNode start = new LNode(0, "start");
        LNode node1 = new LNode(1, "node1");
        LNode node2 = new LNode(2, "node2");
        LNode node3 = new LNode(3, "node3");
        LNode node4 = new LNode(4, "node4");
        LNode node5 = new LNode(5, "node5");
        LNode node6 = new LNode(6, "node6");
        start.getNext().put(node1, new LTransition(new LTransitionLabel("startTo1")));
        node1.getNext().put(node2, new LTransition(new LTransitionLabel("node1To2")));
        node2.getNext().put(node3, new LTransition(new LTransitionLabel("node2To3")));
        node3.getNext().put(node4, new LTransition(new LTransitionLabel("node3To4")));
        node4.getNext().put(node5, new LTransition(new LTransitionLabel("node4To5")));
        node2.getNext().put(node6, new LTransition(new LTransitionLabel("node2To6")));
        node6.getNext().put(node4, new LTransition(new LTransitionLabel("node6To4")));
        List<List<LNodeAndTransition>> allPath = getAllPath(start);
        for(List<LNodeAndTransition> path : allPath) {
            for(LNodeAndTransition nt : path) {
                System.out.println(nt.toString());
            }
            System.out.println("---------------------");
        }
    }
}
