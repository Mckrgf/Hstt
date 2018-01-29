package com.example.yb.hstt.Utils;

import android.graphics.Point;
import android.graphics.PointF;

/**
 * 几何图形工具
 */
public class GeometryUtil {

    /**
     * As meaning of method name. 获得两点之间的距离 (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2) 开平方
     * Math.sqrt:开平方 Math.pow(p0.y - p1.y, 2):求一个数的平方
     *
     * @param p0
     * @param p1
     * @return
     */
    public static float getDistanceBetween2Points(PointF p0, PointF p1) {
        float distance = (float) Math.sqrt(Math.pow(p0.y - p1.y, 2)
                + Math.pow(p0.x - p1.x, 2));
        return distance;
    }

    /**
     * Get middle point between p1 and p2. 获得两点连线的中点
     *
     * @param p1
     * @param p2
     * @return
     */
    public static PointF getMiddlePoint(PointF p1, PointF p2) {
        return new PointF((p1.x + p2.x) / 2.0f, (p1.y + p2.y) / 2.0f);
    }

    /**
     * Get point between p1 and p2 by percent. 根据百分比获取两点之间的某个点坐标
     *
     * @param p1
     * @param p2
     * @param percent
     * @return
     */
    public static PointF getPointByPercent(PointF p1, PointF p2, float percent) {
        return new PointF(evaluateValue(percent, p1.x, p2.x), evaluateValue(
                percent, p1.y, p2.y));
    }

    /**
     * 根据分度值，计算从start到end中，fraction位置的值。fraction范围为0 -> 1
     *
     * @param fraction = 1
     * @param start    = 10
     * @param end      = 3
     * @return
     */
    public static float evaluateValue(float fraction, Number start, Number end) {

        return start.floatValue() + (end.floatValue() - start.floatValue())
                * fraction;
    }

    /**
     * Get the point of intersection between circle and line.
     * 获取通过指定圆心，斜率为lineK的直线与圆的交点。
     *
     * @param pMiddle The circle center point.
     * @param radius  The circle radius.
     * @param lineK   The slope of line which cross the pMiddle.
     * @return
     */
    public static PointF[] getIntersectionPoints(PointF pMiddle, float radius,
                                                 Double lineK) {
        PointF[] points = new PointF[2];

        float radian, xOffset = 0, yOffset = 0;
        if (lineK != null) {
            // 计算直角三角形边长
            // 余切函数（弧度）
            radian = (float) Math.atan(lineK);
            // 正弦函数
            xOffset = (float) (Math.sin(radian) * radius);
            // 余弦函数
            yOffset = (float) (Math.cos(radian) * radius);
        } else {
            xOffset = radius;
            yOffset = 0;
        }
        points[0] = new PointF(pMiddle.x + xOffset, pMiddle.y + yOffset);
        points[1] = new PointF(pMiddle.x - xOffset, pMiddle.y - yOffset);

        return points;
    }

    /**
     * Get the point of intersection between circle and line.
     * 获取通过指定圆心，斜率为lineK的直线与圆的交点。
     *
     * @param pMiddle The circle center point.
     * @param radius  The circle radius.
     * @param lineK   The slope of line which cross the pMiddle.
     * @return
     */
    public static PointF[] getIntersectionPoints(PointF pMiddle, float radius,
                                                 Double lineK, boolean direction_bottom) {
        PointF[] points = new PointF[2];

        float radian, xOffset = 0, yOffset = 0;
        if (lineK != null) {
            // 计算直角三角形边长
            // 余切函数（弧度）
            radian = (float) Math.atan(lineK);
            // 正弦函数
            xOffset = (float) (Math.sin(radian) * radius);
            // 余弦函数
            yOffset = (float) (Math.cos(radian) * radius);
        } else {
            xOffset = radius;
            yOffset = 0;
        }
        if (direction_bottom){
            if (lineK > 0) {
                points[0] = new PointF(pMiddle.x - xOffset, pMiddle.y - yOffset);
                points[1] = new PointF(pMiddle.x + xOffset, pMiddle.y + yOffset);
            }else{
                points[0] = new PointF(pMiddle.x + xOffset, pMiddle.y + yOffset);
                points[1] = new PointF(pMiddle.x - xOffset, pMiddle.y - yOffset);
            }
        }else{
            if (lineK > 0) {
                points[0] = new PointF(pMiddle.x + xOffset, pMiddle.y + yOffset);
                points[1] = new PointF(pMiddle.x - xOffset, pMiddle.y - yOffset);
            }else{
                points[0] = new PointF(pMiddle.x - xOffset, pMiddle.y - yOffset);
                points[1] = new PointF(pMiddle.x + xOffset, pMiddle.y + yOffset);
            }
        }

        return points;
    }

    private static final int PART_ONE = 1, PART_TWO = 2, PART_THREE = 3, PART_FOUR = 4;

    /**
     * 根据坐标中心，和当前的点，获取该点在圆中的角度
     * @param pcenter
     * @param pointxy
     * @return
     */
    public static double getAngleByCenterPoint(Point pcenter, float[] pointxy) {
        float x = pointxy[0];
        float y = pointxy[1];
        double angle = 0;
        //判断触摸圆形的那个 区域
        int which = touchOnWhichPart(pcenter, pointxy);
        switch (which) {
            case PART_ONE:
                angle = Math.atan2(x - pcenter.x, pcenter.y - y) * 180 / Math.PI;
                break;
            case PART_TWO:
                angle = Math.atan2(y - pcenter.y, x - pcenter.x) * 180 / Math.PI + 90;
                break;
            case PART_THREE:
                angle = Math.atan2(pcenter.x - x, y - pcenter.y) * 180 / Math.PI + 180;
                break;
            case PART_FOUR:
                angle = Math.atan2(pcenter.y - y, pcenter.x - x) * 180 / Math.PI + 270;
                break;
        }
        return angle-90;//因为这是从“竖直向上”为起点线开始计算
    }

    /**
     * 4 |  1
     * -----|-----
     * 3 |  2
     * 圆被分成四等份，判断点击在园的哪一部分
     */
    private static int touchOnWhichPart(Point pcenter, float[] pointxy) {
        float x = pointxy[0];
        float y = pointxy[1];
        int which = 0;
        if (x > pcenter.x) {
            if (y > pcenter.y) {
                which = PART_TWO;
            } else {
                which = PART_ONE;
            }
        } else {
            if (y > pcenter.y) {
                which = PART_THREE;
            } else {
                which = PART_FOUR;
            }
        }
//        Log.i(TAG, "onTouchEvent:触摸在区域--" + which);
        return which;
    }
}