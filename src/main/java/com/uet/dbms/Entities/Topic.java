package com.uet.dbms.Entities;

import com.uet.dbms.Process.SQLiteJDBC;
import com.uet.dbms.Process.Word;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    private String content;
    private List<Word> wordList;

    public Topic() {
        this.content = "";
        this.wordList = new ArrayList<>();
    }

    public Topic(String content) {
        this.content = content;
        wordList = SQLiteJDBC.queryWordByTopic(content);
    }

    public List<Word> getWordList() {
        return this.wordList;
    }

    public void setWordList(List<Word> list) {
        this.wordList.addAll(list);
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean contains(Word word) {
        return this.wordList.contains(word);
    }

    public String toString() {
        String res = this.content + ":\n";
        for (Word word : wordList) {
            res += "\t" + word + "\n";
        }
        return res;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Topic other)) return false;
        return other.getContent().equals(this.content);
    }
}
