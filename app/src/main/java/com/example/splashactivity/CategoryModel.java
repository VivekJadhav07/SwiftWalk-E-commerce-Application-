package com.example.splashactivity;

public class CategoryModel {
    private String categoryiconLink;
    private String categoryName;

    public CategoryModel(String categoryiconLink, String categoryName) {
        this.categoryiconLink = categoryiconLink;
        this.categoryName = categoryName;
    }

    public String getCategoryiconLink() {
        return categoryiconLink;
    }

    public void setCategoryiconLink(String categoryiconLink) {
        this.categoryiconLink = categoryiconLink;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
