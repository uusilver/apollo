package com.kc.apollo.model;

import java.util.Set;

/**
 * Created by lijunying on 16/11/18.
 */
public class SearchResult {

    private Set<SearchItem> searchItemSet;
    private int totalResult;
    private long executeTime;

    public static class SearchItem{

        private String title;
        private String url;
        private String create_date;
        private String body_content;
        private String source;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getCreate_date() {
            return create_date;
        }

        public void setCreate_date(String create_date) {
            this.create_date = create_date;
        }

        public String getBody_content() {
            return body_content;
        }

        public void setBody_content(String body_content) {
            this.body_content = body_content;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SearchItem that = (SearchItem) o;

            return !(url != null ? !url.equals(that.url) : that.url != null);

        }

        @Override
        public int hashCode() {
            return url != null ? url.hashCode() : 0;
        }
    }

    public Set<SearchItem> getSearchItemSet() {
        return searchItemSet;
    }

    public void setSearchItemSet(Set<SearchItem> searchItemSet) {
        this.searchItemSet = searchItemSet;
    }

    public int getTotalResult() {
        return totalResult;
    }

    public void setTotalResult(int totalResult) {
        this.totalResult = totalResult;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }


}
