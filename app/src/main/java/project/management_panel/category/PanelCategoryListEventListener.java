package project.management_panel.category;

import project.category.Category;

public class PanelCategoryListEventListener {
    Category category;
    public PanelCategoryListEventListener(Category category){
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }
}
