package com.manager.model;

import javafx.scene.image.Image;

public class TreeNodeData {

    private final String name;
    private final Image icon;
    private final TreeNodeType type;

    public TreeNodeData(String name, Image icon, TreeNodeType type) {
        this.name = name;
        this.icon = icon;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Image getIcon() {
        return icon;
    }

    public TreeNodeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }
}
