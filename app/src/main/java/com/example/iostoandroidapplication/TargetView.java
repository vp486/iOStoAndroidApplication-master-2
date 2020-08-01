package com.example.iostoandroidapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by HP on 11/14/2017.
 */

public class TargetView extends View {

    int xCord, yCord;
    double radius;
    Point centerPoint;
    ArrayList<Point> list;
    ArrayList<Integer> listX, listY;
    Point point;
    Paint blue = new Paint();
    Paint paint = new Paint();

    int a = 0;

    public void setPoints(int xCord, int yCord, double radius) {
        this.xCord = xCord;
        this.yCord = yCord;
        this.radius = radius;

        list = new ArrayList<>();
        listX = new ArrayList<>();
        listY = new ArrayList<>();

        blue.setARGB(255, 0, 0, 255);
        blue.setStrokeWidth(5);

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(25);

        midPointCircle(new Point(0, (int) this.radius), this.radius);
    }

    public TargetView(Context context, AttributeSet attrs) throws NullPointerException {
        super(context, attrs);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        for (Point pointDraw : list) {
            canvas.drawPoint(pointDraw.x + xCord, pointDraw.y + yCord, blue);
        }

        centerPoint = new Point(xCord, yCord);

        canvas.drawPoint(centerPoint.x, centerPoint.y, paint);

        invalidate();
    }

    private void midPointCircle(Point point, double radius) throws NullPointerException {

        double p1 = (5 / 4) - radius;
        this.point = point;

        while (point.x <= point.y) {
            if (p1 < 0) {
                //   System.out.println(p_Ith);
                int x = point.x++;
                int y = point.y;

                listX.add(x);
                listY.add(y);

                list.add(new Point(x, y));
                list.add(new Point(y, x));
                list.add(new Point(-x, y));
                list.add(new Point(y, -x));
                list.add(new Point(-x, -y));
                list.add(new Point(-y, -x));
                list.add(new Point(x, -y));
                list.add(new Point(-y, x));
                a++;
                p1 = p1 + 2.0 * (point.x + 1) + 1;

            } else {
                int x = point.x++;
                int y = point.y--;

                listX.add(x);
                listY.add(y);

                list.add(new Point(x, y));
                list.add(new Point(y, x));
                list.add(new Point(-x, y));
                list.add(new Point(y, -x));
                list.add(new Point(-x, -y));
                list.add(new Point(-y, -x));
                list.add(new Point(x, -y));
                list.add(new Point(-y, x));
                a++;
                p1 = p1 + 2.0 * (point.x + 1) + 1 - (2 * (point.y + 1));
            }
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean handled = false;

        int actionIndex = event.getActionIndex();

        // get touch event coordinates and make transparent circle from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // it's the first pointer, so clear all existing pointers data

                xCord = (int) event.getX(0);
                yCord = (int) event.getY(0);

                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                // It secondary pointers, so obtain their ids and check circles
                xCord = (int) event.getX(actionIndex);
                yCord = (int) event.getY(actionIndex);

                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();

                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    xCord = (int) event.getX(actionIndex);
                    yCord = (int) event.getY(actionIndex);

                }
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_POINTER_UP:
                // not general pointer was up
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                handled = true;
                break;

            default:
                // do nothing
                break;
        }

        return super.onTouchEvent(event) || handled;
    }
}
