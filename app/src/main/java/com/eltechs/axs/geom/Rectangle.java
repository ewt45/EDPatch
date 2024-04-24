package com.eltechs.axs.geom;

/* loaded from: classes.dex */
public final class Rectangle {
    public final int height;
    public final int width;
    public final int x;
    public final int y;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean containsPoint(int x, int y) {
        return this.x <= x && this.y <= y && this.x + this.width > x && this.y + this.height > y;
    }

    public boolean containsPoint(Point point) {
        return containsPoint(point.x, point.y);
    }

    public boolean containsInnerPoint(int x, int y) {
        return x >= 0 && y >= 0 && x < this.width && y < this.height;
    }

    public boolean containsInnerPoint(Point point) {
        return containsInnerPoint(point.x, point.y);
    }

    public boolean containsRectangle(Rectangle rect) {
        return containsPoint(rect.x, rect.y)
                && containsPoint((rect.x + rect.width) - 1, (rect.y + rect.height) - 1);
    }

    public boolean containsInnerRectangle(Rectangle rect) {
        return containsInnerPoint(rect.x, rect.y)
                && containsInnerPoint((rect.x + rect.width) - 1, (rect.y + rect.height) - 1);
    }

    public static Rectangle getIntersection(Rectangle rect1, Rectangle rect2) {
        if (rect1.containsRectangle(rect2)) {
            return rect2;
        }
        if (rect2.containsRectangle(rect1)) {
            return rect1;
        }
        int interLeft = rect1.x;
        int interTop = rect1.y;
        int interRight = (rect1.width + interLeft) - 1;
        int interBottom = (rect1.height + interTop) - 1;

        //如果两者完全不相交
        if (interLeft >= rect2.x + rect2.width || interTop >= rect2.y + rect2.height || interRight < rect2.x || interBottom < rect2.y) {
            return null;
        }
        if (interLeft < rect2.x) {
            interLeft = rect2.x;
        }
        if (interTop < rect2.y) {
            interTop = rect2.y;
        }

        //这是不是写错了？！应该2左+2Width吧 (哦看调用2左永远是0，那加不加都行。。。）
        if (interRight >= rect2.width) {
            interRight = rect2.width - 1;
        }
        if (interBottom >= rect2.height) {
            interBottom = rect2.height - 1;
        }
        return new Rectangle(interLeft, interTop, (interRight - interLeft) + 1, (interBottom - interTop) + 1);
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}