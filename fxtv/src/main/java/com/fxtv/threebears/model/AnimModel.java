package com.fxtv.threebears.model;

/**
 * Created by wzh on 2016/1/23.
 *
 * 1）translationX 和 translationY：这两个属性控制了View所处的位置，它们的值是由layout容器设置的，是相对于坐标原点（0，0左上角）的一个偏移量。

 2）rotation, rotationX 和 rotationY：控制View绕着轴点（pivotX和pivotY）旋转。

 3）scaleX 和 scaleY：控制View基于pivotX和pivotY的缩放。

 4）pivotX 和 pivotY：旋转的轴点和缩放的基准点，默认是View的中心点。

 5）x 和 y：描述了view在其父容器中的最终位置，是左上角左标和偏移量（translationX，translationY）的和。

 6）aplha：透明度，1是完全不透明，0是完全透明。
 */
public class AnimModel {
    public static final String
            TransX="translationX",
            TransY="translationY",
            rotation="rotation",
            rotationX="rotationX",
            rotationY="rotationY",
            scaleX="scaleX",
            scaleY="scaleY",
            pivotX="pivotX",
            pivotY="pivotY",
            x="x",
            y="y",
            aplha="aplha"
    ;

    public static final int Duration_300 =300;//时间ms
}
