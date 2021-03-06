package com.se.apiserver.viewcomponent;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by LeeHyungRae on 2016. 11. 29..
 */
//Second Page의 트리를 구성하는 노드
public class Node {
    public String name;
    public List<Node> children = Lists.newArrayList();
    public List<String> URL;
    public String parent_node;
}
