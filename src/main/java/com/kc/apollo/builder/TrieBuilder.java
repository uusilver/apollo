package com.kc.apollo.builder;

import java.util.Map;

import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author Junying Li
 */
public class TrieBuilder {

    protected final Map<Character, TrieBuilder> children;
    protected String value;
    protected boolean terminal = false;

    public TrieBuilder() {
        this(null);
    }

    private TrieBuilder(String value) {
        this.value = value;
        children = new HashMap<Character, TrieBuilder>();
    }

    protected void add(char c) {
        String val;
        if (this.value == null) {
            val = Character.toString(c);
        } else {
            val = this.value + c;
        }
        children.put(c, new TrieBuilder(val));
    }

    public void insert(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Cannot add null to a TrieBuilder");
        }
        TrieBuilder node = this;
        for (char c : word.toCharArray()) {
            if (!node.children.containsKey(c)) {
                node.add(c);
            }
            node = node.children.get(c);
        }
        node.terminal = true;
    }

    public String find(String word) {
        TrieBuilder node = this;
        for (char c : word.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return "";
            }
            node = node.children.get(c);
        }
        return node.value;
    }

    public Collection<String> autoComplete(String prefix) {
        int index = 0;
        TrieBuilder node = this;
        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return Collections.emptyList();
            }
            node = node.children.get(c);
        }
        return node.allPrefixes();
    }

    protected Collection<String> allPrefixes() {
        List<String> results = new ArrayList<String>();
        if (this.terminal) {
            results.add(this.value);
        }
        for (Entry<Character, TrieBuilder> entry : children.entrySet()) {
            if(results.size()>4){
                break;
            }
            TrieBuilder child = entry.getValue();
            Collection<String> childPrefixes = child.allPrefixes();
            results.addAll(childPrefixes);
        }
        return results;
    }
}