package com.ewt45.patchapp.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

//用于content_pt_main.xml的最外层布局。在activity里设置吧
public class AdaptSnackBehavior extends CoordinatorLayout.Behavior<ConstraintLayout> {
    private static final String TAG = "AdaptSnackBehavior";

    public AdaptSnackBehavior() {
        super();
    }

    public AdaptSnackBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * Determine whether the supplied child view has another specific sibling view as a layout dependency.
     * <p>
     * This method will be called at least once in response to a layout request. If it returns true for a given child and dependency view pair, the parent CoordinatorLayout will:
     * <p>
     * Always lay out this child after the dependent child is laid out, regardless of child order.
     * Call onDependentViewChanged(CoordinatorLayout, V, View) when the dependency view's layout or position changes.
     *
     * @param parent:    the parent view of the given child
     * @param child      the child view to test
     * @param dependency the proposed dependency of child
     * @return true if child's layout depends on the proposed dependency's layout, false otherwise
     */
    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull ConstraintLayout child, @NonNull View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    /**
     * Respond to a change in a child's dependent view
     * <p>
     * This method is called whenever a dependent view changes in size or position outside of the standard layout flow. A Behavior may use this method to appropriately update the child view in response.
     * <p>
     * A view's dependency is determined by layoutDependsOn(CoordinatorLayout, View, View) or if child has set another view as it's anchor.
     * <p>
     * Note that if a Behavior changes the layout of a child via this method, it should also be able to reconstruct the correct position in onLayoutChild. onDependentViewChanged will not be called during normal layout since the layout of each child view will always happen in dependency order.
     * <p>
     * If the Behavior changes the child view's size or position, it should return true. The default implementation returns false.
     *
     * @param parent     the parent view of the given child
     * @param child      the child view to manipulate
     * @param dependency the dependent view that changed
     * @return true if the Behavior changed the child view's size or position, false otherwise
     */
    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull ConstraintLayout child, @NonNull View dependency) {
        if (!(dependency instanceof Snackbar.SnackbarLayout) || dependency.getY() <= child.getY())
            return super.onDependentViewChanged(parent, child, dependency);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        params.bottomMargin = (int) (parent.getHeight() - dependency.getY());
        if (params.bottomMargin < 0) params.bottomMargin = 0;
        child.setLayoutParams(params);
        return true;
    }

    @Override
    public void onDependentViewRemoved(@NonNull CoordinatorLayout parent, @NonNull ConstraintLayout child, @NonNull View dependency) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        params.bottomMargin = 0;
        parent.updateViewLayout(child, params);
    }

    /**
     * Called when the parent CoordinatorLayout is about the lay out the given child view.
     * <p>
     * This method can be used to perform custom or modified layout of a child view in place of the default child layout behavior. The Behavior's implementation can delegate to the standard CoordinatorLayout measurement behavior by calling parent.onLayoutChild.
     * <p>
     * If a Behavior implements onDependentViewChanged(CoordinatorLayout, View, View) to change the position of a view in response to a dependent view changing, it should also implement onLayoutChild in such a way that respects those dependent views. onLayoutChild will always be called for a dependent view after its dependency has been laid out.
     *
     * @param parent          the parent CoordinatorLayout
     * @param child           child view to lay out
     * @param layoutDirection the resolved layout direction for the CoordinatorLayout, such as LAYOUT_DIRECTION_LTR or LAYOUT_DIRECTION_RTL.
     * @return true if the Behavior performed layout of the child view, false to request default layout behavior
     */
    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull ConstraintLayout child, int layoutDirection) {
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    /**
     * Called when a child of the view associated with this behavior wants a particular rectangle to be positioned onto the screen.
     * <p>
     * The contract for this method is the same as ViewParent.requestChildRectangleOnScreen(View, Rect, boolean).
     *
     * @param rectangle The rectangle which the child wishes to be on the screen in the child's coordinates
     * @param immediate true to forbid animated or delayed scrolling, false otherwise
     * @return true if the Behavior handled the request
     */
    @Override
    public boolean onRequestChildRectangleOnScreen(@NonNull CoordinatorLayout coordinatorLayout, @NonNull ConstraintLayout child, @NonNull Rect rectangle, boolean immediate) {
        return super.onRequestChildRectangleOnScreen(coordinatorLayout, child, rectangle, immediate);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull ConstraintLayout child, @NonNull MotionEvent ev) {
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull ConstraintLayout child, @NonNull MotionEvent ev) {
        return super.onTouchEvent(parent, child, ev);
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull ConstraintLayout child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        Log.d(TAG, "onNestedScroll: ");
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull ConstraintLayout child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        Log.d(TAG, "onStartNestedScroll: ");
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull CoordinatorLayout coordinatorLayout, @NonNull ConstraintLayout child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        Log.d(TAG, "onNestedScrollAccepted: ");
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes, type);
    }
}
