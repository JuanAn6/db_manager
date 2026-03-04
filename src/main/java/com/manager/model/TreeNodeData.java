package com.manager.model;

import javafx.scene.image.Image;

public class TreeNodeData {

    private final String name;
    private final Image icon;

    public TreeNodeData(String name, Image icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public Image getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return name;
    }
}
