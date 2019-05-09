package org.commonmark.internal.renderer.text;

import org.commonmark.node.BulletList;

public class BulletListHolder extends ListHolder {
    private final char marker;

    public BulletListHolder(ListHolder parent, BulletList list) {
        super(parent);
        marker = list.getBulletMarker();
    }

    public char getMarker() {
        return marker;
    }
}
